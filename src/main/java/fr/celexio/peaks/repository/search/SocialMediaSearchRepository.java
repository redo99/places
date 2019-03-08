package fr.celexio.peaks.repository.search;

import fr.celexio.peaks.domain.SocialMedia;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the SocialMedia entity.
 */
public interface SocialMediaSearchRepository extends ElasticsearchRepository<SocialMedia, Long> {
}
