package fr.celexio.peaks.service.impl;

import fr.celexio.peaks.service.LabelTranslationService;
import fr.celexio.peaks.domain.LabelTranslation;
import fr.celexio.peaks.repository.LabelTranslationRepository;
import fr.celexio.peaks.repository.search.LabelTranslationSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing LabelTranslation.
 */
@Service
@Transactional
public class LabelTranslationServiceImpl implements LabelTranslationService {

    private final Logger log = LoggerFactory.getLogger(LabelTranslationServiceImpl.class);

    private final LabelTranslationRepository labelTranslationRepository;

    private final LabelTranslationSearchRepository labelTranslationSearchRepository;

    public LabelTranslationServiceImpl(LabelTranslationRepository labelTranslationRepository, LabelTranslationSearchRepository labelTranslationSearchRepository) {
        this.labelTranslationRepository = labelTranslationRepository;
        this.labelTranslationSearchRepository = labelTranslationSearchRepository;
    }

    /**
     * Save a labelTranslation.
     *
     * @param labelTranslation the entity to save
     * @return the persisted entity
     */
    @Override
    public LabelTranslation save(LabelTranslation labelTranslation) {
        log.debug("Request to save LabelTranslation : {}", labelTranslation);        LabelTranslation result = labelTranslationRepository.save(labelTranslation);
        labelTranslationSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the labelTranslations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<LabelTranslation> findAll(Pageable pageable) {
        log.debug("Request to get all LabelTranslations");
        return labelTranslationRepository.findAll(pageable);
    }


    /**
     * Get one labelTranslation by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<LabelTranslation> findOne(Long id) {
        log.debug("Request to get LabelTranslation : {}", id);
        return labelTranslationRepository.findById(id);
    }

    /**
     * Delete the labelTranslation by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete LabelTranslation : {}", id);
        labelTranslationRepository.deleteById(id);
        labelTranslationSearchRepository.deleteById(id);
    }

    /**
     * Search for the labelTranslation corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<LabelTranslation> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of LabelTranslations for query {}", query);
        return labelTranslationSearchRepository.search(queryStringQuery(query), pageable);    }
}
