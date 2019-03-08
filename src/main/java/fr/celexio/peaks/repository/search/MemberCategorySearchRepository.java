package fr.celexio.peaks.repository.search;

import fr.celexio.peaks.domain.MemberCategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the MemberCategory entity.
 */
public interface MemberCategorySearchRepository extends ElasticsearchRepository<MemberCategory, Long> {
}
