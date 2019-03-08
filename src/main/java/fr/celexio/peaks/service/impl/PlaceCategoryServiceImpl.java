package fr.celexio.peaks.service.impl;

import fr.celexio.peaks.service.PlaceCategoryService;
import fr.celexio.peaks.domain.PlaceCategory;
import fr.celexio.peaks.repository.PlaceCategoryRepository;
import fr.celexio.peaks.repository.search.PlaceCategorySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing PlaceCategory.
 */
@Service
@Transactional
public class PlaceCategoryServiceImpl implements PlaceCategoryService {

    private final Logger log = LoggerFactory.getLogger(PlaceCategoryServiceImpl.class);

    private final PlaceCategoryRepository placeCategoryRepository;

    private final PlaceCategorySearchRepository placeCategorySearchRepository;

    public PlaceCategoryServiceImpl(PlaceCategoryRepository placeCategoryRepository, PlaceCategorySearchRepository placeCategorySearchRepository) {
        this.placeCategoryRepository = placeCategoryRepository;
        this.placeCategorySearchRepository = placeCategorySearchRepository;
    }

    /**
     * Save a placeCategory.
     *
     * @param placeCategory the entity to save
     * @return the persisted entity
     */
    @Override
    public PlaceCategory save(PlaceCategory placeCategory) {
        log.debug("Request to save PlaceCategory : {}", placeCategory);        PlaceCategory result = placeCategoryRepository.save(placeCategory);
        placeCategorySearchRepository.save(result);
        return result;
    }

    /**
     * Get all the placeCategories.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PlaceCategory> findAll(Pageable pageable) {
        log.debug("Request to get all PlaceCategories");
        return placeCategoryRepository.findAll(pageable);
    }


    /**
     * Get one placeCategory by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PlaceCategory> findOne(Long id) {
        log.debug("Request to get PlaceCategory : {}", id);
        return placeCategoryRepository.findById(id);
    }

    /**
     * Delete the placeCategory by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete PlaceCategory : {}", id);
        placeCategoryRepository.deleteById(id);
        placeCategorySearchRepository.deleteById(id);
    }

    /**
     * Search for the placeCategory corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PlaceCategory> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PlaceCategories for query {}", query);
        return placeCategorySearchRepository.search(queryStringQuery(query), pageable);    }
}
