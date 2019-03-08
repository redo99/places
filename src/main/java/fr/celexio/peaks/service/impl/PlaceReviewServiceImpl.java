package fr.celexio.peaks.service.impl;

import fr.celexio.peaks.service.PlaceReviewService;
import fr.celexio.peaks.domain.PlaceReview;
import fr.celexio.peaks.repository.PlaceReviewRepository;
import fr.celexio.peaks.repository.search.PlaceReviewSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing PlaceReview.
 */
@Service
@Transactional
public class PlaceReviewServiceImpl implements PlaceReviewService {

    private final Logger log = LoggerFactory.getLogger(PlaceReviewServiceImpl.class);

    private final PlaceReviewRepository placeReviewRepository;

    private final PlaceReviewSearchRepository placeReviewSearchRepository;

    public PlaceReviewServiceImpl(PlaceReviewRepository placeReviewRepository, PlaceReviewSearchRepository placeReviewSearchRepository) {
        this.placeReviewRepository = placeReviewRepository;
        this.placeReviewSearchRepository = placeReviewSearchRepository;
    }

    /**
     * Save a placeReview.
     *
     * @param placeReview the entity to save
     * @return the persisted entity
     */
    @Override
    public PlaceReview save(PlaceReview placeReview) {
        log.debug("Request to save PlaceReview : {}", placeReview);        PlaceReview result = placeReviewRepository.save(placeReview);
        placeReviewSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the placeReviews.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PlaceReview> findAll(Pageable pageable) {
        log.debug("Request to get all PlaceReviews");
        return placeReviewRepository.findAll(pageable);
    }


    /**
     * Get one placeReview by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PlaceReview> findOne(Long id) {
        log.debug("Request to get PlaceReview : {}", id);
        return placeReviewRepository.findById(id);
    }

    /**
     * Delete the placeReview by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete PlaceReview : {}", id);
        placeReviewRepository.deleteById(id);
        placeReviewSearchRepository.deleteById(id);
    }

    /**
     * Search for the placeReview corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PlaceReview> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PlaceReviews for query {}", query);
        return placeReviewSearchRepository.search(queryStringQuery(query), pageable);    }
}
