package fr.celexio.peaks.repository.search;

import fr.celexio.peaks.domain.PlaceCategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the PlaceCategory entity.
 */
public interface PlaceCategorySearchRepository extends ElasticsearchRepository<PlaceCategory, Long> {
}
