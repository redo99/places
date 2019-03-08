package fr.celexio.peaks.repository.search;

import fr.celexio.peaks.domain.PlaceReview;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the PlaceReview entity.
 */
public interface PlaceReviewSearchRepository extends ElasticsearchRepository<PlaceReview, Long> {
}
