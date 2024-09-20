package com.podcast.ai.http_clients;

import com.podcast.ai.models.entities.PodcastEpisode;
import com.podcast.ai.models.replicate.common.ReplicateResponse;
import com.podcast.ai.models.replicate.image.ReplicateGenerateImageRequest;
import com.podcast.ai.models.replicate.image.ReplicateGenerateImageResponse;
import com.podcast.ai.models.replicate.speech_to_text.ReplicateSpeechToTextRequest;
import com.podcast.ai.models.replicate.speech_to_text.ReplicateSpeechToTextResponse;
import com.podcast.ai.models.replicate.text.ReplicateTextChatRequest;
import com.podcast.ai.models.replicate.text.ReplicateTextChatResponse;
import com.podcast.ai.models.replicate.text_to_speech.ReplicateTextToSpeechRequest;
import com.podcast.ai.models.replicate.text_to_speech.ReplicateTextToSpeechResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplicateHttpClient {
    private static final String API_URL = "https://api.replicate.com";
    private static final String PREDICTIONS_URL = "/v1/predictions";

    @Value("${replicate.api-key}")
    private String replicateApiKey;

    public ReplicateSpeechToTextResponse speechToText(PodcastEpisode episod) {
        ReplicateSpeechToTextRequest request = new ReplicateSpeechToTextRequest(episod.getEnclosureUrl());
        ReplicateSpeechToTextResponse response = speechToTextCall(request);
        boolean isSucceeded = checkResponse(response);
        if (isSucceeded) {
            return response;
        } else {
            if (Objects.nonNull(response)) {
                isSucceeded = replicateCheckLoop(response, episod.getDuration());
                if (isSucceeded) {
                    response = speechToTextCheckCall(response.getUrls().getGet());
                }
            }
        }

        return response;
    }

    public String textChat(ReplicateTextChatRequest request) {
        ReplicateTextChatResponse response = textChatCall(request);
        boolean isSucceeded = checkResponse(response);
        if (isSucceeded) {
            return String.join("", response.getOutput());
        } else {
            if (Objects.nonNull(response)) {
                isSucceeded = replicateCheckLoop(response, request.getInput().getMaxTokens());
                if (isSucceeded) {
                    response = textChatCheckCall(response.getUrls().getGet());
                }
            }
        }

        if (!checkResponse(response)) {
            return null;
        }

        return String.join("", response.getOutput());
    }

    public ReplicateGenerateImageResponse generateImage(ReplicateGenerateImageRequest request) {
        ReplicateGenerateImageResponse response = generateImageCall(request);
        boolean isSucceeded = checkResponse(response);
        if (isSucceeded) {
            return response;
        } else {
            if (Objects.nonNull(response)) {
                isSucceeded = replicateCheckLoop(response, 128);
                if (isSucceeded) {
                    response = generateImageCheckCall(response.getUrls().getGet());
                }
            }
        }

        return response;
    }

    public ReplicateTextToSpeechResponse generateSpeech(ReplicateTextToSpeechRequest request) {
        ReplicateTextToSpeechResponse response = generateSpeechCall(request);
        boolean isSucceeded = checkResponse(response);
        if (isSucceeded) {
            return response;
        } else {
            if (Objects.nonNull(response)) {
                isSucceeded = replicateCheckLoop(response, 128);
                if (isSucceeded) {
                    response = generateSpeechCheckCall(response.getUrls().getGet());
                }
            }
        }

        return response;
    }

    private boolean replicateCheckLoop(ReplicateResponse response, Integer loopSize) {
        String getUrl = response.getUrls().getGet();
        for (int i = 0; i < loopSize; i++) {
            ReplicateResponse checkResponse = replicateCheckCall(getUrl);
            if (checkResponse(checkResponse)) {
                return true;
            }
        }
        /*
         * Cancel prediction if still going
         */
        if (!checkResponse(response)) {
            replicateCheckCall(response.getUrls().getCancel());
        }

        return false;
    }

    private RestClient getRestClientWithBaseUrl() {
        return RestClient.builder()
                .baseUrl(API_URL)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("Authorization", "Bearer " + replicateApiKey);
                    httpHeaders.set("Content-Type", "application/json");
                }).build();
    }

    private RestClient getRestClientWithoutBaseUrl() {
        return RestClient.builder()
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("Authorization", "Bearer " + replicateApiKey);
                    httpHeaders.set("Content-Type", "application/json");
                }).build();
    }

    private ReplicateTextChatResponse textChatCall(ReplicateTextChatRequest request) {
        return getRestClientWithBaseUrl()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/models/meta/meta-llama-3-70b-instruct/predictions")
                        .build()
                )
                .body(request)
                .retrieve()
                .body(ReplicateTextChatResponse.class);
    }

    private ReplicateGenerateImageResponse generateImageCall(ReplicateGenerateImageRequest request) {
        return getRestClientWithBaseUrl()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PREDICTIONS_URL)
                        .build()
                )
                .body(request)
                .retrieve()
                .body(ReplicateGenerateImageResponse.class);
    }

    private ReplicateTextToSpeechResponse generateSpeechCall(ReplicateTextToSpeechRequest request) {
        return getRestClientWithBaseUrl()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PREDICTIONS_URL)
                        .build()
                )
                .body(request)
                .retrieve()
                .body(ReplicateTextToSpeechResponse.class);
    }

    private ReplicateSpeechToTextResponse speechToTextCall(ReplicateSpeechToTextRequest request) {
        return getRestClientWithBaseUrl()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PREDICTIONS_URL)
                        .build()
                )
                .body(request)
                .retrieve()
                .body(ReplicateSpeechToTextResponse.class);
    }

    private ReplicateSpeechToTextResponse speechToTextCheckCall(String getUrl) {
        return getRestClientWithoutBaseUrl()
                .get()
                .uri(URI.create(getUrl))
                .retrieve()
                .body(ReplicateSpeechToTextResponse.class);
    }

    private ReplicateResponse replicateCheckCall(String getUrl) {
        return getRestClientWithoutBaseUrl()
                .get()
                .uri(URI.create(getUrl))
                .retrieve()
                .body(ReplicateResponse.class);
    }

    private ReplicateTextChatResponse textChatCheckCall(String getUrl) {
        return getRestClientWithoutBaseUrl()
                .get()
                .uri(URI.create(getUrl))
                .retrieve()
                .body(ReplicateTextChatResponse.class);
    }

    private ReplicateGenerateImageResponse generateImageCheckCall(String getUrl) {
        return getRestClientWithoutBaseUrl()
                .get()
                .uri(URI.create(getUrl))
                .retrieve()
                .body(ReplicateGenerateImageResponse.class);
    }

    private ReplicateTextToSpeechResponse generateSpeechCheckCall(String getUrl) {
        return getRestClientWithoutBaseUrl()
                .get()
                .uri(URI.create(getUrl))
                .retrieve()
                .body(ReplicateTextToSpeechResponse.class);
    }

    private boolean checkResponse(ReplicateResponse response) {
        return Objects.nonNull(response) && response.getStatus().equals("succeeded");
    }
}
