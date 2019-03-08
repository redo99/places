package fr.celexio.peaks.service.impl;

import fr.celexio.peaks.service.LocationService;
import fr.celexio.peaks.domain.Location;
import fr.celexio.peaks.repository.LocationRepository;
import fr.celexio.peaks.repository.search.LocationSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Location.
 */
@Service
@Transactional
public class LocationServiceImpl implements LocationService {

    private final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);

    private final LocationRepository locationRepository;

    private final LocationSearchRepository locationSearchRepository;

    public LocationServiceImpl(LocationRepository locationRepository, LocationSearchRepository locationSearchRepository) {
        this.locationRepository = locationRepository;
        this.locationSearchRepository = locationSearchRepository;
    }

    /**
     * Save a location.
     *
     * @param location the entity to save
     * @return the persisted entity
     */
    @Override
    public Location save(Location location) {
        log.debug("Request to save Location : {}", location);        Location result = locationRepository.save(location);
        locationSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the locations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Location> findAll(Pageable pageable) {
        log.debug("Request to get all Locations");
        return locationRepository.findAll(pageable);
    }


    /**
     * Get one location by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Location> findOne(Long id) {
        log.debug("Request to get Location : {}", id);
        return locationRepository.findById(id);
    }

    /**
     * Delete the location by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Location : {}", id);
        locationRepository.deleteById(id);
        locationSearchRepository.deleteById(id);
    }

    /**
     * Search for the location corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Location> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Locations for query {}", query);
        return locationSearchRepository.search(queryStringQuery(query), pageable);    }
}
