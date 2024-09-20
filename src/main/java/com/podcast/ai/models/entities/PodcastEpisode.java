package com.podcast.ai.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Entity
@Table(name = "podcast_episodes")
@Getter
@Setter
@NoArgsConstructor
public class PodcastEpisode {
    @Id
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "podcast_guid")
    private String podcastGuid;
    @NotNull
    @Column(name = "enclosure_url")
    private String enclosureUrl;
    @NotNull
    @Column(name = "enclosure_type")
    private String enclosureType;
    @NotNull
    @Column(name = "enclosure_length")
    private Long enclosureLength;
    @NotNull
    @Column(name = "duration")
    private Integer duration;

    @Lob
    @Column(name = "content_summary", columnDefinition = "text")
    private String contentSummary;
    @Lob
    @Column(name = "title", columnDefinition = "text")
    private String title;
}
