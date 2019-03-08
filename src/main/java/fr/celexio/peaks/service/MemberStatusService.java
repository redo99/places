package fr.celexio.peaks.service;

import fr.celexio.peaks.domain.MemberStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing MemberStatus.
 */
public interface MemberStatusService {

    /**
     * Save a memberStatus.
     *
     * @param memberStatus the entity to save
     * @return the persisted entity
     */
    MemberStatus save(MemberStatus memberStatus);

    /**
     * Get all the memberStatuses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<MemberStatus> findAll(Pageable pageable);


    /**
     * Get the "id" memberStatus.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<MemberStatus> findOne(Long id);

    /**
     * Delete the "id" memberStatus.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the memberStatus corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<MemberStatus> search(String query, Pageable pageable);
}
