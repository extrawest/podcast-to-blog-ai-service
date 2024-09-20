package com.podcast.ai.mappers;

import com.podcast.ai.models.dto.PodcastEpisodeDTO;
import com.podcast.ai.models.entities.PodcastEpisode;
import com.podcast.ai.models.podcast_index.PodcastIndexEpisode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PodcastEpisodeMapper {

    @Mapping(target = "podcastGuid", ignore = true)
    @Mapping(target = "contentSummary", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "id", source = "episode.id")
    PodcastEpisode map(PodcastIndexEpisode episode);

    PodcastEpisodeDTO map(PodcastEpisode episode);

}
