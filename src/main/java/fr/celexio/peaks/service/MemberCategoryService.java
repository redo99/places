package fr.celexio.peaks.service;

import fr.celexio.peaks.domain.MemberCategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing MemberCategory.
 */
public interface MemberCategoryService {

    /**
     * Save a memberCategory.
     *
     * @param memberCategory the entity to save
     * @return the persisted entity
     */
    MemberCategory save(MemberCategory memberCategory);

    /**
     * Get all the memberCategories.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<MemberCategory> findAll(Pageable pageable);


    /**
     * Get the "id" memberCategory.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<MemberCategory> findOne(Long id);

    /**
     * Delete the "id" memberCategory.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the memberCategory corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<MemberCategory> search(String query, Pageable pageable);
}
