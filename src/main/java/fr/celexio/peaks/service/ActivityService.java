package fr.celexio.peaks.service;

import fr.celexio.peaks.domain.Activity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Activity.
 */
public interface ActivityService {

    /**
     * Save a activity.
     *
     * @param activity the entity to save
     * @return the persisted entity
     */
    Activity save(Activity activity);

    /**
     * Get all the activities.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Activity> findAll(Pageable pageable);


    /**
     * Get the "id" activity.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Activity> findOne(Long id);

    /**
     * Delete the "id" activity.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the activity corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Activity> search(String query, Pageable pageable);
}
