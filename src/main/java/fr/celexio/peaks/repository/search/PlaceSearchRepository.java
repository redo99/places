package fr.celexio.peaks.repository.search;

import fr.celexio.peaks.domain.Place;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Place entity.
 */
public interface PlaceSearchRepository extends ElasticsearchRepository<Place, Long> {
}
