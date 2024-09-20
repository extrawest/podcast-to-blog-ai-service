package com.podcast.ai.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.podcast.ai.exceptions.InvalidDataException;
import com.podcast.ai.exceptions.NotFoundException;
import com.podcast.ai.http_clients.*;
import com.podcast.ai.mappers.PodcastEpisodeMapper;
import com.podcast.ai.models.api.PodcastEpisodesRequest;
import com.podcast.ai.models.dto.PodcastEpisodeDTO;
import com.podcast.ai.models.entities.PodcastEpisode;
import com.podcast.ai.models.podcast_index.PodcastIndexResponse;
import com.podcast.ai.models.replicate.image.ReplicateGenerateImageRequest;
import com.podcast.ai.models.replicate.image.ReplicateGenerateImageResponse;
import com.podcast.ai.models.replicate.speech_to_text.ReplicateSpeechToTextResponse;
import com.podcast.ai.models.replicate.text.ReplicateTextChatMessage;
import com.podcast.ai.models.replicate.text.ReplicateTextChatRequest;
import com.podcast.ai.models.replicate.text.ReplicateTranslateMessage;
import com.podcast.ai.models.replicate.text_to_speech.ReplicateTextToSpeechRequest;
import com.podcast.ai.models.replicate.text_to_speech.ReplicateTextToSpeechResponse;
import com.podcast.ai.repositories.PodcastEpisodeRepository;
import com.podcast.ai.repositories.criteries.PodcastEpisodesCriteria;
import com.podcast.ai.services.chat.ChatService;
import com.podcast.ai.services.chat.ClientChatRequest;
import com.podcast.ai.services.chat.DataIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PodcastProcessingService {
    private static final String ERROR_RESPONSE = "Error: ";
    private static final String SUCCESS_RESPONSE = "Success";
    private static final String PODCAST_EPISODE_NOT_FOUND = "Podcast episode %s not found";

    private static final ObjectMapper UNMARSHALLER = new ObjectMapper();
    static {
        UNMARSHALLER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final PodcastIndexHttpClient podcastIndexHttpClient;
    private final DownloadHttpClient downloadHttpClient;
    private final PodcastEpisodeRepository podcastEpisodeRepository;
    private final PodcastEpisodeMapper podcastEpisodMapper;
    private final DataIngestionService dataIngestionService;
    private final ReplicateHttpClient replicateHttpClient;
    private final ChatService chatService;

    @Transactional(rollbackFor = RuntimeException.class)
    public String downloadPodcastEpisodes(String podcastGuid) {
        try {
            PodcastIndexResponse podcast = podcastIndexHttpClient.downloadEpisodesMetaData(podcastGuid);
            podcast.getItems().forEach(episode -> {
                PodcastEpisode podcastEpisode = podcastEpisodMapper.map(episode);
                podcastEpisode.setPodcastGuid(podcastGuid);
                podcastEpisodeRepository.save(podcastEpisode);
            });
            dataIngestionService.createCollection(podcastGuid);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ERROR_RESPONSE + e.getMessage();
        }

        return SUCCESS_RESPONSE;
    }

    @Transactional(readOnly = true)
    public Page<PodcastEpisodeDTO> findEpisodesByFilters(PodcastEpisodesRequest request, Pageable pageable) {
        PodcastEpisodesCriteria criteria = new PodcastEpisodesCriteria(request);
        Page<PodcastEpisode> page = podcastEpisodeRepository.findAll(criteria, pageable);

        return new PageImpl<>(
                page.getContent().stream().map(podcastEpisodMapper::map).toList(),
                pageable,
                page.getTotalElements()
        );
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public ReplicateTextChatMessage createContentSummary(String podcastGuid, Long episodeId) {
        PodcastEpisode episod = getPodcastEpisod(podcastGuid, episodeId);
        if (Objects.nonNull(episod.getContentSummary())) {
            return new ReplicateTextChatMessage(episod.getTitle(), episod.getContentSummary());
        }

        try {
            ReplicateSpeechToTextResponse speechToTextResponse = replicateHttpClient.speechToText(episod);
            dataIngestionService.insertDocuments(speechToTextResponse.getOutput().getTranscription(), podcastGuid);

            String json = replicateHttpClient.textChat(
                    new ReplicateTextChatRequest(
                            String.format("""
                                    Here is text of the podcast episode. Text: %s
                                    Please generate content summary and title for this text.
                                    Answer length for title must be maximum 8 words.
                                    Answer must be in JSON format.
                                    JSON answer must have only two fields 'title' and 'summary'
                                    """, speechToTextResponse.getOutput().getTranscription())
                    )
            );
            ReplicateTextChatMessage message = UNMARSHALLER.readValue(json, new TypeReference<>() {});

            if (Objects.nonNull(message)) {
                episod.setTitle(message.getTitle());
                episod.setContentSummary(message.getSummary());
            }

            return message;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new InvalidDataException(e.getMessage());
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public ReplicateTranslateMessage translateSummaryToFrench(String podcastGuid, Long episodeId) {
        PodcastEpisode episod = getPodcastEpisod(podcastGuid, episodeId);
        if (Objects.isNull(episod.getContentSummary())) {
            throw new InvalidDataException(String.format("Podcast episode %s doesn't have summary", episodeId));
        }

        try {
            String json = replicateHttpClient.textChat(
                    new ReplicateTextChatRequest(
                            String.format("""
                                    Here is summary text of the podcast episode. Text: %s
                                    Please translate it on french.
                                    Answer must be in JSON format.
                                    JSON answer must have only one field 'translatedText'
                                    """, episod.getContentSummary())
                    )
            );
            ReplicateTranslateMessage message = UNMARSHALLER.readValue(json, new TypeReference<>() {});
            message.setText(episod.getContentSummary());

            return message;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new InvalidDataException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateImage(String podcastGuid, Long episodeId) {
        PodcastEpisode episod = getPodcastEpisod(podcastGuid, episodeId);
        if (Objects.isNull(episod.getTitle())) {
            throw new InvalidDataException(String.format("Podcast episode %s doesn't title", episodeId));
        }

        try {
            ReplicateGenerateImageResponse response = replicateHttpClient.generateImage(new ReplicateGenerateImageRequest(episod.getTitle()));
            return downloadHttpClient.download(URI.create(response.getOutput().getFirst()).toURL());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new byte[]{};
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateSpeech(String podcastGuid, Long episodeId) {
        PodcastEpisode episod = getPodcastEpisod(podcastGuid, episodeId);
        if (Objects.isNull(episod.getContentSummary())) {
            throw new InvalidDataException(String.format("Podcast episode %s doesn't content summary", episodeId));
        }

        try {
            ReplicateTextToSpeechResponse response = replicateHttpClient.generateSpeech(new ReplicateTextToSpeechRequest(episod.getContentSummary()));
            return downloadHttpClient.download(URI.create(response.getOutput()).toURL());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new byte[]{};
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public String chat(ClientChatRequest request) {
        return chatService.advancedRag(request);
    }

    private PodcastEpisode getPodcastEpisod(String podcastGuid, Long episodeId) {
        Optional<PodcastEpisode> episodOptional = podcastEpisodeRepository.findByIdAndPodcastGuid(episodeId, podcastGuid);
        if (episodOptional.isEmpty()) {
            throw new NotFoundException(String.format(PODCAST_EPISODE_NOT_FOUND, episodeId));
        }
        return episodOptional.get();
    }
}
