package com.podcast.ai.models.hugging_face;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@NoArgsConstructor
@Data
public class ChatResponse {
    private String object;
    private String id;
    private Long created;
    private String model;
    @JsonProperty("system_fingerprint")
    private String systemFingerprint;
    private List<ChatChoice> choices;
    private ChatUsage usage;
}
