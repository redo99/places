package fr.celexio.peaks.repository.search;

import fr.celexio.peaks.domain.LabelTranslation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the LabelTranslation entity.
 */
public interface LabelTranslationSearchRepository extends ElasticsearchRepository<LabelTranslation, Long> {
}
