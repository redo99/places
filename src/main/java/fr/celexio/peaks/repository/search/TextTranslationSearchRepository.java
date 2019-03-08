package fr.celexio.peaks.repository.search;

import fr.celexio.peaks.domain.TextTranslation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the TextTranslation entity.
 */
public interface TextTranslationSearchRepository extends ElasticsearchRepository<TextTranslation, Long> {
}
