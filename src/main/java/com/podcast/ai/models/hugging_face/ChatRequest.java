package com.podcast.ai.models.hugging_face;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@Data
public class ChatRequest {
    private List<ChatMessage> messages = new LinkedList<>();
    private String model = "HuggingFaceH4/zephyr-7b-beta";
    @JsonProperty("max_tokens")
    private Integer maxTokens = 500;
}
