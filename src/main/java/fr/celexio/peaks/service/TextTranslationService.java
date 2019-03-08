package fr.celexio.peaks.service;

import fr.celexio.peaks.domain.TextTranslation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing TextTranslation.
 */
public interface TextTranslationService {

    /**
     * Save a textTranslation.
     *
     * @param textTranslation the entity to save
     * @return the persisted entity
     */
    TextTranslation save(TextTranslation textTranslation);

    /**
     * Get all the textTranslations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<TextTranslation> findAll(Pageable pageable);


    /**
     * Get the "id" textTranslation.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<TextTranslation> findOne(Long id);

    /**
     * Delete the "id" textTranslation.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the textTranslation corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<TextTranslation> search(String query, Pageable pageable);
}
