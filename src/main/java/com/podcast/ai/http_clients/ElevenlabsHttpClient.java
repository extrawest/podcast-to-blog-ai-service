package com.podcast.ai.http_clients;

import com.podcast.ai.models.elevenlabs.ElevenlabsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElevenlabsHttpClient {
    private static final String API_URL = "https://api.elevenlabs.io";

    @Value("${elevenlabs.api-key}")
    private String elevenlabsApiKey;

    public byte[] textToSpeech(String text) {
        ElevenlabsRequest request = new ElevenlabsRequest(text);
        return getRestClient()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/text-to-speech/pMsXgVXv3BLzUgSXRplE")
                        .build()
                )
                .body(request)
                .retrieve()
                .body(byte[].class);
    }

    private RestClient getRestClient() {
        return RestClient.builder()
                .baseUrl(API_URL)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("xi-api-key", elevenlabsApiKey);
                    httpHeaders.set("Accept", "audio/mpeg");
                    httpHeaders.set("Content-Type", "application/json");
                }).build();
    }

}
