package com.podcast.ai.models.replicate.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Data
public class ReplicateMetrics {
    @JsonProperty("predict_time")
    private Double predictTime;
    @JsonProperty("total_time")
    private Double totalTime;
    @JsonProperty("tokens_per_second")
    private Double tokensPerSecond;
    @JsonProperty("input_token_count")
    private Integer inputTokenCount;
    @JsonProperty("output_token_count")
    private Integer outputTokenCount;
}
