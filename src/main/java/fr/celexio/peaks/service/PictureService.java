package fr.celexio.peaks.service;

import fr.celexio.peaks.domain.Picture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Picture.
 */
public interface PictureService {

    /**
     * Save a picture.
     *
     * @param picture the entity to save
     * @return the persisted entity
     */
    Picture save(Picture picture);

    /**
     * Get all the pictures.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Picture> findAll(Pageable pageable);


    /**
     * Get the "id" picture.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Picture> findOne(Long id);

    /**
     * Delete the "id" picture.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the picture corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Picture> search(String query, Pageable pageable);
}
