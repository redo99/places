package fr.celexio.peaks.service.impl;

import fr.celexio.peaks.service.SocialMediaService;
import fr.celexio.peaks.domain.SocialMedia;
import fr.celexio.peaks.repository.SocialMediaRepository;
import fr.celexio.peaks.repository.search.SocialMediaSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing SocialMedia.
 */
@Service
@Transactional
public class SocialMediaServiceImpl implements SocialMediaService {

    private final Logger log = LoggerFactory.getLogger(SocialMediaServiceImpl.class);

    private final SocialMediaRepository socialMediaRepository;

    private final SocialMediaSearchRepository socialMediaSearchRepository;

    public SocialMediaServiceImpl(SocialMediaRepository socialMediaRepository, SocialMediaSearchRepository socialMediaSearchRepository) {
        this.socialMediaRepository = socialMediaRepository;
        this.socialMediaSearchRepository = socialMediaSearchRepository;
    }

    /**
     * Save a socialMedia.
     *
     * @param socialMedia the entity to save
     * @return the persisted entity
     */
    @Override
    public SocialMedia save(SocialMedia socialMedia) {
        log.debug("Request to save SocialMedia : {}", socialMedia);        SocialMedia result = socialMediaRepository.save(socialMedia);
        socialMediaSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the socialMedias.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SocialMedia> findAll(Pageable pageable) {
        log.debug("Request to get all SocialMedias");
        return socialMediaRepository.findAll(pageable);
    }


    /**
     * Get one socialMedia by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<SocialMedia> findOne(Long id) {
        log.debug("Request to get SocialMedia : {}", id);
        return socialMediaRepository.findById(id);
    }

    /**
     * Delete the socialMedia by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete SocialMedia : {}", id);
        socialMediaRepository.deleteById(id);
        socialMediaSearchRepository.deleteById(id);
    }

    /**
     * Search for the socialMedia corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SocialMedia> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SocialMedias for query {}", query);
        return socialMediaSearchRepository.search(queryStringQuery(query), pageable);    }
}
