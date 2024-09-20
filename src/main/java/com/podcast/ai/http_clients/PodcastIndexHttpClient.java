package com.podcast.ai.http_clients;

import com.podcast.ai.models.podcast_index.PodcastIndexResponse;
import com.podcast.ai.utils.DateUtils;
import com.podcast.ai.utils.Encryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PodcastIndexHttpClient {
    public static final String PODCAST_INDEX_URL = "https://api.podcastindex.org/api/1.0";

    @Value("${podcast-index.api.key}")
    private String podcastIndexApiKey;
    @Value("${podcast-index.api.secret}")
    private String podcastIndexApiSecret;

    public PodcastIndexResponse downloadEpisodesMetaData(String podcastGuid) {
        String unixTime = String.valueOf(DateUtils.toSeconds(LocalDateTime.now().plusMinutes(3)));
        String sha = Encryptor.sha1(podcastIndexApiKey + podcastIndexApiSecret + unixTime);

        RestClient restClient = RestClient.builder()
                .baseUrl(PODCAST_INDEX_URL)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("Authorization", sha);
                    httpHeaders.set("X-Auth-Date", unixTime);
                    httpHeaders.set("X-Auth-Key", podcastIndexApiKey);
                    httpHeaders.set("User-Agent", "curl/7.54.1");
                }).build();

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/episodes/bypodcastguid")
                        .queryParam("guid", podcastGuid)
                        .queryParam("pretty", true)
                        .build()
                )
                .retrieve()
                .body(PodcastIndexResponse.class);
    }
}
