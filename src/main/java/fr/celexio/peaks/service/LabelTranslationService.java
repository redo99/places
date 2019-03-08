package fr.celexio.peaks.service;

import fr.celexio.peaks.domain.LabelTranslation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing LabelTranslation.
 */
public interface LabelTranslationService {

    /**
     * Save a labelTranslation.
     *
     * @param labelTranslation the entity to save
     * @return the persisted entity
     */
    LabelTranslation save(LabelTranslation labelTranslation);

    /**
     * Get all the labelTranslations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<LabelTranslation> findAll(Pageable pageable);


    /**
     * Get the "id" labelTranslation.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<LabelTranslation> findOne(Long id);

    /**
     * Delete the "id" labelTranslation.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the labelTranslation corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<LabelTranslation> search(String query, Pageable pageable);
}
