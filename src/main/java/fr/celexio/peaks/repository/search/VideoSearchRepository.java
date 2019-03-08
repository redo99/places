package fr.celexio.peaks.repository.search;

import fr.celexio.peaks.domain.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Video entity.
 */
public interface VideoSearchRepository extends ElasticsearchRepository<Video, Long> {
}
