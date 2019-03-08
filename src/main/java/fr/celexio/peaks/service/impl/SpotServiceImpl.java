package fr.celexio.peaks.service.impl;

import fr.celexio.peaks.service.SpotService;
import fr.celexio.peaks.domain.Spot;
import fr.celexio.peaks.repository.SpotRepository;
import fr.celexio.peaks.repository.search.SpotSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Spot.
 */
@Service
@Transactional
public class SpotServiceImpl implements SpotService {

    private final Logger log = LoggerFactory.getLogger(SpotServiceImpl.class);

    private final SpotRepository spotRepository;

    private final SpotSearchRepository spotSearchRepository;

    public SpotServiceImpl(SpotRepository spotRepository, SpotSearchRepository spotSearchRepository) {
        this.spotRepository = spotRepository;
        this.spotSearchRepository = spotSearchRepository;
    }

    /**
     * Save a spot.
     *
     * @param spot the entity to save
     * @return the persisted entity
     */
    @Override
    public Spot save(Spot spot) {
        log.debug("Request to save Spot : {}", spot);        Spot result = spotRepository.save(spot);
        spotSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the spots.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Spot> findAll(Pageable pageable) {
        log.debug("Request to get all Spots");
        return spotRepository.findAll(pageable);
    }


    /**
     * Get one spot by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Spot> findOne(Long id) {
        log.debug("Request to get Spot : {}", id);
        return spotRepository.findById(id);
    }

    /**
     * Delete the spot by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Spot : {}", id);
        spotRepository.deleteById(id);
        spotSearchRepository.deleteById(id);
    }

    /**
     * Search for the spot corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Spot> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Spots for query {}", query);
        return spotSearchRepository.search(queryStringQuery(query), pageable);    }
}
