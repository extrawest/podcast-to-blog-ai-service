package com.podcast.ai.models.hugging_face;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
public class ChatChoice {
    private Integer index;
    private ChatMessage message;
    private String logprobs;
    @JsonProperty("finish_reason")
    private String finishReason;
}
