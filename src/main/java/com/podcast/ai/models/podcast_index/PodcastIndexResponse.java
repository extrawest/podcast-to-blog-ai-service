package com.podcast.ai.models.podcast_index;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class PodcastIndexResponse {
    private boolean status;
    private Integer query;
    private Integer count;
    private List<PodcastIndexEpisode> items;
    private String description;
}
