package com.podcast.ai.models.replicate.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Data
public class ReplicateResponse {
    @JsonProperty("completed_at")
    private String completedAt;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("data_removed")
    private Boolean dataRemoved;
    private String error;
    private String id;
    private String logs;
    private ReplicateMetrics metrics;
    @JsonProperty("started_at")
    private String startedAt;
    private String status;
    private ReplicateUrl urls;
    private String version;
}