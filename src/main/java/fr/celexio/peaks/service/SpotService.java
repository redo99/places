package fr.celexio.peaks.service;

import fr.celexio.peaks.domain.Spot;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Spot.
 */
public interface SpotService {

    /**
     * Save a spot.
     *
     * @param spot the entity to save
     * @return the persisted entity
     */
    Spot save(Spot spot);

    /**
     * Get all the spots.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Spot> findAll(Pageable pageable);


    /**
     * Get the "id" spot.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Spot> findOne(Long id);

    /**
     * Delete the "id" spot.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the spot corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Spot> search(String query, Pageable pageable);
}
