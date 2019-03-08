package fr.celexio.peaks.repository.search;

import fr.celexio.peaks.domain.MemberStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the MemberStatus entity.
 */
public interface MemberStatusSearchRepository extends ElasticsearchRepository<MemberStatus, Long> {
}
