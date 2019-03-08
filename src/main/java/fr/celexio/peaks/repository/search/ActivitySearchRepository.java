package fr.celexio.peaks.repository.search;

import fr.celexio.peaks.domain.Activity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Activity entity.
 */
public interface ActivitySearchRepository extends ElasticsearchRepository<Activity, Long> {
}
