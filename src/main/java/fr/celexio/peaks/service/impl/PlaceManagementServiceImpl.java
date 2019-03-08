package fr.celexio.peaks.service.impl;

import fr.celexio.peaks.service.PlaceManagementService;
import fr.celexio.peaks.domain.PlaceManagement;
import fr.celexio.peaks.repository.PlaceManagementRepository;
import fr.celexio.peaks.repository.search.PlaceManagementSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing PlaceManagement.
 */
@Service
@Transactional
public class PlaceManagementServiceImpl implements PlaceManagementService {

    private final Logger log = LoggerFactory.getLogger(PlaceManagementServiceImpl.class);

    private final PlaceManagementRepository placeManagementRepository;

    private final PlaceManagementSearchRepository placeManagementSearchRepository;

    public PlaceManagementServiceImpl(PlaceManagementRepository placeManagementRepository, PlaceManagementSearchRepository placeManagementSearchRepository) {
        this.placeManagementRepository = placeManagementRepository;
        this.placeManagementSearchRepository = placeManagementSearchRepository;
    }

    /**
     * Save a placeManagement.
     *
     * @param placeManagement the entity to save
     * @return the persisted entity
     */
    @Override
    public PlaceManagement save(PlaceManagement placeManagement) {
        log.debug("Request to save PlaceManagement : {}", placeManagement);        PlaceManagement result = placeManagementRepository.save(placeManagement);
        placeManagementSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the placeManagements.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PlaceManagement> findAll(Pageable pageable) {
        log.debug("Request to get all PlaceManagements");
        return placeManagementRepository.findAll(pageable);
    }


    /**
     * Get one placeManagement by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PlaceManagement> findOne(Long id) {
        log.debug("Request to get PlaceManagement : {}", id);
        return placeManagementRepository.findById(id);
    }

    /**
     * Delete the placeManagement by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete PlaceManagement : {}", id);
        placeManagementRepository.deleteById(id);
        placeManagementSearchRepository.deleteById(id);
    }

    /**
     * Search for the placeManagement corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PlaceManagement> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PlaceManagements for query {}", query);
        return placeManagementSearchRepository.search(queryStringQuery(query), pageable);    }
}
