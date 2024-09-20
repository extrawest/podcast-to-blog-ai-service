package com.podcast.ai.models.dto;

import lombok.Data;

@Data
public class PodcastEpisodeDTO {
    private Long id;
    private String podcastGuid;
    private String enclosureUrl;
    private String enclosureType;
    private Long enclosureLength;
    private Integer duration;
    private String contentSummary;
    private String title;
}
