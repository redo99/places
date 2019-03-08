package fr.celexio.peaks.repository.search;

import fr.celexio.peaks.domain.Picture;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Picture entity.
 */
public interface PictureSearchRepository extends ElasticsearchRepository<Picture, Long> {
}
