package fr.celexio.peaks.service.impl;

import fr.celexio.peaks.service.MemberStatusService;
import fr.celexio.peaks.domain.MemberStatus;
import fr.celexio.peaks.repository.MemberStatusRepository;
import fr.celexio.peaks.repository.search.MemberStatusSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing MemberStatus.
 */
@Service
@Transactional
public class MemberStatusServiceImpl implements MemberStatusService {

    private final Logger log = LoggerFactory.getLogger(MemberStatusServiceImpl.class);

    private final MemberStatusRepository memberStatusRepository;

    private final MemberStatusSearchRepository memberStatusSearchRepository;

    public MemberStatusServiceImpl(MemberStatusRepository memberStatusRepository, MemberStatusSearchRepository memberStatusSearchRepository) {
        this.memberStatusRepository = memberStatusRepository;
        this.memberStatusSearchRepository = memberStatusSearchRepository;
    }

    /**
     * Save a memberStatus.
     *
     * @param memberStatus the entity to save
     * @return the persisted entity
     */
    @Override
    public MemberStatus save(MemberStatus memberStatus) {
        log.debug("Request to save MemberStatus : {}", memberStatus);        MemberStatus result = memberStatusRepository.save(memberStatus);
        memberStatusSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the memberStatuses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MemberStatus> findAll(Pageable pageable) {
        log.debug("Request to get all MemberStatuses");
        return memberStatusRepository.findAll(pageable);
    }


    /**
     * Get one memberStatus by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<MemberStatus> findOne(Long id) {
        log.debug("Request to get MemberStatus : {}", id);
        return memberStatusRepository.findById(id);
    }

    /**
     * Delete the memberStatus by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete MemberStatus : {}", id);
        memberStatusRepository.deleteById(id);
        memberStatusSearchRepository.deleteById(id);
    }

    /**
     * Search for the memberStatus corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MemberStatus> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MemberStatuses for query {}", query);
        return memberStatusSearchRepository.search(queryStringQuery(query), pageable);    }
}
