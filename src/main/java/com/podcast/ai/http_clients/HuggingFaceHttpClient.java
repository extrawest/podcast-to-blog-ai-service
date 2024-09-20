package com.podcast.ai.http_clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.podcast.ai.models.hugging_face.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HuggingFaceHttpClient {
    private static final ObjectMapper UNMARSHALLER = new ObjectMapper();
    static {
        UNMARSHALLER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    private static final String API_URL = "https://api-inference.huggingface.co";

    @Value("${hugging-face.api-key}")
    private String hfApiKey;

    public SpeechToTextResponse speechToText(byte[] file) {
        return getRestClient()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/facebook/wav2vec2-base-960h")
                        .build()
                )
                .body(file)
                .retrieve()
                .body(SpeechToTextResponse.class);
    }

    public List<SummarizeResponse> summarize(SummarizeRequest request) throws JsonProcessingException {
        String jsonResponse = getRestClient()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/facebook/bart-large-cnn")
                        .build()
                )
                .body(request)
                .retrieve()
                .body(String.class);

        return UNMARSHALLER.readValue(jsonResponse, new TypeReference<>(){});
    }

    public ChatResponse chat(ChatRequest request) {
        return getRestClient()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/HuggingFaceH4/zephyr-7b-beta/v1/chat/completions")
                        .build()
                )
                .body(request)
                .retrieve()
                .body(ChatResponse.class);
    }

    public byte[] textToImage(TextToImageRequest request) {
        return getRestClient()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/ZB-Tech/Text-to-Image")
                        .build()
                )
                .body(request)
                .retrieve()
                .body(byte[].class);
    }

    public List<TranslationResponse> translation(TranslationRequest request) throws JsonProcessingException {
        String jsonResponse = getRestClient()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/Helsinki-NLP/opus-mt-en-fr")
                        .build()
                )
                .body(request)
                .retrieve()
                .body(String.class);

        return UNMARSHALLER.readValue(jsonResponse, new TypeReference<>(){});
    }

    private RestClient getRestClient() {
        return RestClient.builder()
                .baseUrl(API_URL)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("Authorization", "Bearer " + hfApiKey);
                    httpHeaders.set("Content-Type", "application/json");
                }).build();
    }
}
