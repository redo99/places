package fr.celexio.peaks.service;

import fr.celexio.peaks.domain.PlaceCategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing PlaceCategory.
 */
public interface PlaceCategoryService {

    /**
     * Save a placeCategory.
     *
     * @param placeCategory the entity to save
     * @return the persisted entity
     */
    PlaceCategory save(PlaceCategory placeCategory);

    /**
     * Get all the placeCategories.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<PlaceCategory> findAll(Pageable pageable);


    /**
     * Get the "id" placeCategory.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<PlaceCategory> findOne(Long id);

    /**
     * Delete the "id" placeCategory.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the placeCategory corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<PlaceCategory> search(String query, Pageable pageable);
}
