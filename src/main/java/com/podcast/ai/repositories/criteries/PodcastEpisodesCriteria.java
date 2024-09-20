package com.podcast.ai.repositories.criteries;

import com.podcast.ai.models.api.PodcastEpisodesRequest;
import com.podcast.ai.models.entities.PodcastEpisode;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PodcastEpisodesCriteria implements Specification<PodcastEpisode> {
    private final PodcastEpisodesRequest request;

    public PodcastEpisodesCriteria(PodcastEpisodesRequest request) {
        this.request = request;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<PodcastEpisode> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if (request.getPodcastGuid() != null) {
            predicates.add(builder.equal(root.get("podcastGuid"), request.getPodcastGuid()));
        }

        if (request.getId() != null) {
            predicates.add(builder.equal(root.get("id"), request.getId()));
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
