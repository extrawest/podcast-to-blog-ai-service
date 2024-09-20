package com.podcast.ai.models.api;

import lombok.Data;

import java.io.Serializable;

@Data
public class PodcastEpisodesRequest implements Serializable {
    private String podcastGuid;
    private Long id;
}
