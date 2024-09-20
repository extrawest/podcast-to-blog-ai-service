package com.podcast.ai.controllers;

import com.podcast.ai.configurations.SwaggerPageable;
import com.podcast.ai.models.api.PodcastEpisodesRequest;
import com.podcast.ai.models.dto.PodcastEpisodeDTO;
import com.podcast.ai.services.PodcastProcessingService;
import com.podcast.ai.services.chat.ClientChatRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PodcastProcessingController {
    private final PodcastProcessingService service;

    @Operation(summary = "Ready to use")
    @PostMapping( "/podcastEpisodes/download")
    public ResponseEntity<String> downloadPodcastEpisodes(@RequestParam String podcastGuid) {
        String result = service.downloadPodcastEpisodes(podcastGuid);
        return ResponseEntity.ok().body(result);
    }

    @Operation(summary = "Ready to use")
    @PostMapping( "/podcastEpisodes/createContentSummary")
    public ResponseEntity<String> createContentSummary(@RequestParam String podcastGuid, @RequestParam Long episodeId) {
        String response = service.createContentSummary(podcastGuid, episodeId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Ready to use")
    @PostMapping( "/podcastEpisodes/translateSummaryToFrench")
    public ResponseEntity<String> translateSummaryToFrench(@RequestParam String podcastGuid, @RequestParam Long episodeId) {
        String response = service.translateSummaryToFrench(podcastGuid, episodeId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Ready to use")
    @PostMapping( "/podcastEpisodes/generateTitle")
    public ResponseEntity<String> generateTitle(@RequestParam String podcastGuid, @RequestParam Long episodeId) {
        String response = service.generateTitle(podcastGuid, episodeId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Ready to use")
    @PostMapping( "/podcastEpisodes/generateImage")
    public ResponseEntity<ByteArrayResource> generateImage(@RequestParam String podcastGuid, @RequestParam Long episodeId) {
        byte[] response = service.generateImage(podcastGuid, episodeId);
        ByteArrayResource resource = new ByteArrayResource(response);

        return ResponseEntity.ok()
                .contentLength(response.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "image_episod_" + episodeId + "\"")
                .body(resource);
    }

    @Operation(summary = "Ready to use")
    @PostMapping( "/podcastEpisodes/generateSpeech")
    public ResponseEntity<ByteArrayResource> generateSpeech(@RequestParam String podcastGuid, @RequestParam Long episodeId) {
        byte[] response = service.generateSpeech(podcastGuid, episodeId);
        ByteArrayResource resource = new ByteArrayResource(response);

        return ResponseEntity.ok()
                .contentLength(response.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "speech_episod_" + episodeId + "\"")
                .body(resource);
    }

    @SwaggerPageable
    @PostMapping("/podcastEpisodes/search")
    public ResponseEntity<Page<PodcastEpisodeDTO>> findEpisodesByFilters(
            @RequestBody @Valid PodcastEpisodesRequest request,
            @PageableDefault(value = 15) Pageable pageable
    ) {
        Page<PodcastEpisodeDTO> response = service.findEpisodesByFilters(request, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Ready to use")
    @PostMapping( "/podcastEpisodes/chat")
    public ResponseEntity<String> chat(@RequestBody ClientChatRequest request) {
        String response = service.chat(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
