package com.podcast.ai.models.replicate.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Data
public class ReplicateUrl {
    private String stream;
    private String get;
    private String cancel;
}
