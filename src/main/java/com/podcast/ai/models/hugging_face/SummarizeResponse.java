package com.podcast.ai.models.hugging_face;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Data
public class SummarizeResponse {
    @JsonProperty("summary_text")
    private String summaryText;
}
