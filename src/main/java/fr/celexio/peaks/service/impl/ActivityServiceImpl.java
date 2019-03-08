package fr.celexio.peaks.service.impl;

import fr.celexio.peaks.service.ActivityService;
import fr.celexio.peaks.domain.Activity;
import fr.celexio.peaks.repository.ActivityRepository;
import fr.celexio.peaks.repository.search.ActivitySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Activity.
 */
@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

    private final Logger log = LoggerFactory.getLogger(ActivityServiceImpl.class);

    private final ActivityRepository activityRepository;

    private final ActivitySearchRepository activitySearchRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository, ActivitySearchRepository activitySearchRepository) {
        this.activityRepository = activityRepository;
        this.activitySearchRepository = activitySearchRepository;
    }

    /**
     * Save a activity.
     *
     * @param activity the entity to save
     * @return the persisted entity
     */
    @Override
    public Activity save(Activity activity) {
        log.debug("Request to save Activity : {}", activity);        Activity result = activityRepository.save(activity);
        activitySearchRepository.save(result);
        return result;
    }

    /**
     * Get all the activities.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Activity> findAll(Pageable pageable) {
        log.debug("Request to get all Activities");
        return activityRepository.findAll(pageable);
    }


    /**
     * Get one activity by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Activity> findOne(Long id) {
        log.debug("Request to get Activity : {}", id);
        return activityRepository.findById(id);
    }

    /**
     * Delete the activity by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Activity : {}", id);
        activityRepository.deleteById(id);
        activitySearchRepository.deleteById(id);
    }

    /**
     * Search for the activity corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Activity> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Activities for query {}", query);
        return activitySearchRepository.search(queryStringQuery(query), pageable);    }
}
