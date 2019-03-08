package fr.celexio.peaks.service;

import fr.celexio.peaks.domain.PlaceReview;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing PlaceReview.
 */
public interface PlaceReviewService {

    /**
     * Save a placeReview.
     *
     * @param placeReview the entity to save
     * @return the persisted entity
     */
    PlaceReview save(PlaceReview placeReview);

    /**
     * Get all the placeReviews.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<PlaceReview> findAll(Pageable pageable);


    /**
     * Get the "id" placeReview.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<PlaceReview> findOne(Long id);

    /**
     * Delete the "id" placeReview.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the placeReview corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<PlaceReview> search(String query, Pageable pageable);
}
