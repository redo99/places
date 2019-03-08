package fr.celexio.peaks.service.impl;

import fr.celexio.peaks.service.PictureService;
import fr.celexio.peaks.domain.Picture;
import fr.celexio.peaks.repository.PictureRepository;
import fr.celexio.peaks.repository.search.PictureSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Picture.
 */
@Service
@Transactional
public class PictureServiceImpl implements PictureService {

    private final Logger log = LoggerFactory.getLogger(PictureServiceImpl.class);

    private final PictureRepository pictureRepository;

    private final PictureSearchRepository pictureSearchRepository;

    public PictureServiceImpl(PictureRepository pictureRepository, PictureSearchRepository pictureSearchRepository) {
        this.pictureRepository = pictureRepository;
        this.pictureSearchRepository = pictureSearchRepository;
    }

    /**
     * Save a picture.
     *
     * @param picture the entity to save
     * @return the persisted entity
     */
    @Override
    public Picture save(Picture picture) {
        log.debug("Request to save Picture : {}", picture);        Picture result = pictureRepository.save(picture);
        pictureSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the pictures.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Picture> findAll(Pageable pageable) {
        log.debug("Request to get all Pictures");
        return pictureRepository.findAll(pageable);
    }


    /**
     * Get one picture by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Picture> findOne(Long id) {
        log.debug("Request to get Picture : {}", id);
        return pictureRepository.findById(id);
    }

    /**
     * Delete the picture by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Picture : {}", id);
        pictureRepository.deleteById(id);
        pictureSearchRepository.deleteById(id);
    }

    /**
     * Search for the picture corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Picture> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Pictures for query {}", query);
        return pictureSearchRepository.search(queryStringQuery(query), pageable);    }
}
