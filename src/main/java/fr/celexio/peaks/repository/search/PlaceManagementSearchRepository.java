package fr.celexio.peaks.repository.search;

import fr.celexio.peaks.domain.PlaceManagement;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the PlaceManagement entity.
 */
public interface PlaceManagementSearchRepository extends ElasticsearchRepository<PlaceManagement, Long> {
}
