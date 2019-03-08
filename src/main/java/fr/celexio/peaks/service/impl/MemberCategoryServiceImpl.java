package fr.celexio.peaks.service.impl;

import fr.celexio.peaks.service.MemberCategoryService;
import fr.celexio.peaks.domain.MemberCategory;
import fr.celexio.peaks.repository.MemberCategoryRepository;
import fr.celexio.peaks.repository.search.MemberCategorySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing MemberCategory.
 */
@Service
@Transactional
public class MemberCategoryServiceImpl implements MemberCategoryService {

    private final Logger log = LoggerFactory.getLogger(MemberCategoryServiceImpl.class);

    private final MemberCategoryRepository memberCategoryRepository;

    private final MemberCategorySearchRepository memberCategorySearchRepository;

    public MemberCategoryServiceImpl(MemberCategoryRepository memberCategoryRepository, MemberCategorySearchRepository memberCategorySearchRepository) {
        this.memberCategoryRepository = memberCategoryRepository;
        this.memberCategorySearchRepository = memberCategorySearchRepository;
    }

    /**
     * Save a memberCategory.
     *
     * @param memberCategory the entity to save
     * @return the persisted entity
     */
    @Override
    public MemberCategory save(MemberCategory memberCategory) {
        log.debug("Request to save MemberCategory : {}", memberCategory);        MemberCategory result = memberCategoryRepository.save(memberCategory);
        memberCategorySearchRepository.save(result);
        return result;
    }

    /**
     * Get all the memberCategories.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MemberCategory> findAll(Pageable pageable) {
        log.debug("Request to get all MemberCategories");
        return memberCategoryRepository.findAll(pageable);
    }


    /**
     * Get one memberCategory by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<MemberCategory> findOne(Long id) {
        log.debug("Request to get MemberCategory : {}", id);
        return memberCategoryRepository.findById(id);
    }

    /**
     * Delete the memberCategory by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete MemberCategory : {}", id);
        memberCategoryRepository.deleteById(id);
        memberCategorySearchRepository.deleteById(id);
    }

    /**
     * Search for the memberCategory corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MemberCategory> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MemberCategories for query {}", query);
        return memberCategorySearchRepository.search(queryStringQuery(query), pageable);    }
}
