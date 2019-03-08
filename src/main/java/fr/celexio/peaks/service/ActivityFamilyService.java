package fr.celexio.peaks.service;

import fr.celexio.peaks.domain.ActivityFamily;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

/**
 * Service Interface for managing ActivityFamily.
 */
public interface ActivityFamilyService {

    /**
     * Save a activityFamily.
     *
     * @param activityFamily the entity to save
     * @return the persisted entity
     */
    ActivityFamily save(ActivityFamily activityFamily);

    /**
     * Get all the activityFamilies.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ActivityFamily> findAll(Pageable pageable);


    /**
     * Get the "id" activityFamily.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<ActivityFamily> findOne(Long id);

    /**
     * Delete the "id" activityFamily.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the activityFamily corresponding to the query.
     *
     * @param query the query of the search
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ActivityFamily> search(String query, Pageable pageable);
}
