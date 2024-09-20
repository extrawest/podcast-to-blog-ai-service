package com.podcast.ai.models.podcast_index;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Data
public class PodcastIndexEpisode {
    private Long id;
    private String title;
    private String link;
    private String description;
    private String guid;
    private Long datePublished;
    private String datePublishedPretty;
    private Long dateCrawled;
    private String enclosureUrl;
    private String enclosureType;
    private Long enclosureLength;
    private Integer duration;
}
