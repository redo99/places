package fr.celexio.peaks.service;

import fr.celexio.peaks.domain.SocialMedia;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing SocialMedia.
 */
public interface SocialMediaService {

    /**
     * Save a socialMedia.
     *
     * @param socialMedia the entity to save
     * @return the persisted entity
     */
    SocialMedia save(SocialMedia socialMedia);

    /**
     * Get all the socialMedias.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<SocialMedia> findAll(Pageable pageable);


    /**
     * Get the "id" socialMedia.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<SocialMedia> findOne(Long id);

    /**
     * Delete the "id" socialMedia.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the socialMedia corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<SocialMedia> search(String query, Pageable pageable);
}
