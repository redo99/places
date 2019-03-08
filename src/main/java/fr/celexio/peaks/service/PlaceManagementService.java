package fr.celexio.peaks.service;

import fr.celexio.peaks.domain.PlaceManagement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing PlaceManagement.
 */
public interface PlaceManagementService {

    /**
     * Save a placeManagement.
     *
     * @param placeManagement the entity to save
     * @return the persisted entity
     */
    PlaceManagement save(PlaceManagement placeManagement);

    /**
     * Get all the placeManagements.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<PlaceManagement> findAll(Pageable pageable);


    /**
     * Get the "id" placeManagement.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<PlaceManagement> findOne(Long id);

    /**
     * Delete the "id" placeManagement.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the placeManagement corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<PlaceManagement> search(String query, Pageable pageable);
}
