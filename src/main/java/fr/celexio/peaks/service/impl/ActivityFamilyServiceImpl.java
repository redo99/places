package fr.celexio.peaks.service.impl;

import fr.celexio.peaks.service.ActivityFamilyService;
import fr.celexio.peaks.domain.ActivityFamily;
import fr.celexio.peaks.repository.ActivityFamilyRepository;
import fr.celexio.peaks.repository.search.ActivityFamilySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing ActivityFamily.
 */
@Service
@Transactional
public class ActivityFamilyServiceImpl implements ActivityFamilyService {

    private final Logger log = LoggerFactory.getLogger(ActivityFamilyServiceImpl.class);

    private final ActivityFamilyRepository activityFamilyRepository;

    private final ActivityFamilySearchRepository activityFamilySearchRepository;

    public ActivityFamilyServiceImpl(ActivityFamilyRepository activityFamilyRepository, ActivityFamilySearchRepository activityFamilySearchRepository) {
        this.activityFamilyRepository = activityFamilyRepository;
        this.activityFamilySearchRepository = activityFamilySearchRepository;
    }

    /**
     * Save a activityFamily.
     *
     * @param activityFamily the entity to save
     * @return the persisted entity
     */
    @Override
    public ActivityFamily save(ActivityFamily activityFamily) {
        log.debug("Request to save ActivityFamily : {}", activityFamily);        ActivityFamily result = activityFamilyRepository.save(activityFamily);
        activityFamilySearchRepository.save(result);
        return result;
    }

    /**
     * Get all the activityFamilies.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ActivityFamily> findAll(Pageable pageable) {
        log.debug("Request to get all ActivityFamilies");
        return activityFamilyRepository.findAll(pageable);
    }


    /**
     * Get one activityFamily by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ActivityFamily> findOne(Long id) {
        log.debug("Request to get ActivityFamily : {}", id);
        return activityFamilyRepository.findById(id);
    }

    /**
     * Delete the activityFamily by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ActivityFamily : {}", id);
        activityFamilyRepository.deleteById(id);
        activityFamilySearchRepository.deleteById(id);
    }

    /**
     * Search for the activityFamily corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ActivityFamily> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ActivityFamilies for query {}", query);
        try {
            query = URLDecoder.decode(query, "UTF-8");
        } catch(UnsupportedEncodingException uee) {
            log.warn(uee.getMessage());
            return null;
        }
        return activityFamilySearchRepository.search(queryStringQuery(query), pageable);    }
}
