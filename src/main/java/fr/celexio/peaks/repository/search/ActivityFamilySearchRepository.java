package fr.celexio.peaks.repository.search;

import fr.celexio.peaks.domain.ActivityFamily;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ActivityFamily entity.
 */
public interface ActivityFamilySearchRepository extends ElasticsearchRepository<ActivityFamily, Long> {
}
