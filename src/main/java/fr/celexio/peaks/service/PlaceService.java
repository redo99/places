package fr.celexio.peaks.service;

import fr.celexio.peaks.domain.Place;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Place.
 */
public interface PlaceService {

    /**
     * Save a place.
     *
     * @param place the entity to save
     * @return the persisted entity
     */
    Place save(Place place);

    /**
     * Get all the places.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Place> findAll(Pageable pageable);

    /**
     * Get all the Place with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    Page<Place> findAllWithEagerRelationships(Pageable pageable);
    
    /**
     * Get the "id" place.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Place> findOne(Long id);

    /**
     * Delete the "id" place.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the place corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Place> search(String query, Pageable pageable);
}
