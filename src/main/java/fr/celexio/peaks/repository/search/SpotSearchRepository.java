package fr.celexio.peaks.repository.search;

import fr.celexio.peaks.domain.Spot;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Spot entity.
 */
public interface SpotSearchRepository extends ElasticsearchRepository<Spot, Long> {
}
