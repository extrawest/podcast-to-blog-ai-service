package com.podcast.ai.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.podcast.ai.exceptions.InvalidDataException;
import com.podcast.ai.exceptions.NotFoundException;
import com.podcast.ai.http_clients.DownloadSpeechHttpClient;
import com.podcast.ai.http_clients.ElevenlabsHttpClient;
import com.podcast.ai.http_clients.HuggingFaceHttpClient;
import com.podcast.ai.http_clients.PodcastIndexHttpClient;
import com.podcast.ai.mappers.PodcastEpisodeMapper;
import com.podcast.ai.models.api.PodcastEpisodesRequest;
import com.podcast.ai.models.dto.PodcastEpisodeDTO;
import com.podcast.ai.models.entities.PodcastEpisode;
import com.podcast.ai.models.hugging_face.*;
import com.podcast.ai.models.podcast_index.PodcastIndexResponse;
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
    private final HuggingFaceHttpClient huggingFaceHttpClient;
    private final DownloadSpeechHttpClient downloadSpeechHttpClient;
    private final ElevenlabsHttpClient elevenlabsHttpClient;
    private final PodcastEpisodeRepository podcastEpisodeRepository;
    private final PodcastEpisodeMapper podcastEpisodMapper;
    private final DataIngestionService dataIngestionService;
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
    public String createContentSummary(String podcastGuid, Long episodeId) {
        PodcastEpisode episod = getPodcastEpisod(podcastGuid, episodeId);
        if (Objects.nonNull(episod.getContentSummary())) {
            return episod.getContentSummary();
        }

        try {
            byte[] bytes = downloadSpeechHttpClient.download(URI.create(episod.getEnclosureUrl()).toURL());
            SpeechToTextResponse speech = huggingFaceHttpClient.speechToText(bytes);
            dataIngestionService.insertDocuments(speech.getText(), podcastGuid);
            List<SummarizeResponse> summarize = huggingFaceHttpClient.summarize(new SummarizeRequest(speech.getText()));
            String summaryText = summarize.getFirst().getSummaryText().toLowerCase(Locale.ROOT);

            episod.setContentSummary(summaryText);

            return summaryText;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ERROR_RESPONSE + e.getMessage();
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public String translateSummaryToFrench(String podcastGuid, Long episodeId) {
        PodcastEpisode episod = getPodcastEpisod(podcastGuid, episodeId);
        if (Objects.isNull(episod.getContentSummary())) {
            throw new InvalidDataException(String.format("Podcast episode %s doesn't have summary", episodeId));
        }

        try {
            List<TranslationResponse> translation = huggingFaceHttpClient.translation(new TranslationRequest(episod.getContentSummary()));
            return translation.getFirst().getTranslationText().toLowerCase(Locale.ROOT);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ERROR_RESPONSE+ e.getMessage();
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public String generateTitle(String podcastGuid, Long episodeId) {
        PodcastEpisode episod = getPodcastEpisod(podcastGuid, episodeId);
        if (Objects.isNull(episod.getContentSummary())) {
            throw new InvalidDataException(String.format("Podcast episode %s doesn't have summary", episodeId));
        }

        try {
            ChatRequest request = new ChatRequest();
            request.getMessages().add(new ChatMessage(
                    "user",
                            """
                            Here is summary of the podcast episode.
                            Please generate short title for this text.
                            Answer length must be maximum 8 words.
                            Answer must be in JSON format.
                            JSON answer must have only one field 'title'
                            """
            ));

            ChatResponse chat = huggingFaceHttpClient.chat(request);
            EpisodTitle title = UNMARSHALLER.readValue(chat.getChoices().getFirst().getMessage().getContent(), new TypeReference<>() {});

            episod.setTitle(title.getTitle().toLowerCase(Locale.ROOT));

            return title.getTitle();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ERROR_RESPONSE+ e.getMessage();
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateImage(String podcastGuid, Long episodeId) {
        PodcastEpisode episod = getPodcastEpisod(podcastGuid, episodeId);
        if (Objects.isNull(episod.getTitle())) {
            throw new InvalidDataException(String.format("Podcast episode %s doesn't title", episodeId));
        }

        try {
            return huggingFaceHttpClient.textToImage(new TextToImageRequest(episod.getTitle()));
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
            return elevenlabsHttpClient.textToSpeech(episod.getContentSummary());
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
