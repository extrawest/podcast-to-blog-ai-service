package com.podcast.ai.repositories;

import com.podcast.ai.models.entities.PodcastEpisode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PodcastEpisodeRepository extends JpaRepository<PodcastEpisode, Long>, JpaSpecificationExecutor<PodcastEpisode> {

    Optional<PodcastEpisode> findByIdAndPodcastGuid(Long id, String podcastGuid);

}
