package fr.celexio.peaks.service.impl;

import fr.celexio.peaks.service.TextTranslationService;
import fr.celexio.peaks.domain.TextTranslation;
import fr.celexio.peaks.repository.TextTranslationRepository;
import fr.celexio.peaks.repository.search.TextTranslationSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing TextTranslation.
 */
@Service
@Transactional
public class TextTranslationServiceImpl implements TextTranslationService {

    private final Logger log = LoggerFactory.getLogger(TextTranslationServiceImpl.class);

    private final TextTranslationRepository textTranslationRepository;

    private final TextTranslationSearchRepository textTranslationSearchRepository;

    public TextTranslationServiceImpl(TextTranslationRepository textTranslationRepository, TextTranslationSearchRepository textTranslationSearchRepository) {
        this.textTranslationRepository = textTranslationRepository;
        this.textTranslationSearchRepository = textTranslationSearchRepository;
    }

    /**
     * Save a textTranslation.
     *
     * @param textTranslation the entity to save
     * @return the persisted entity
     */
    @Override
    public TextTranslation save(TextTranslation textTranslation) {
        log.debug("Request to save TextTranslation : {}", textTranslation);        TextTranslation result = textTranslationRepository.save(textTranslation);
        textTranslationSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the textTranslations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TextTranslation> findAll(Pageable pageable) {
        log.debug("Request to get all TextTranslations");
        return textTranslationRepository.findAll(pageable);
    }


    /**
     * Get one textTranslation by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TextTranslation> findOne(Long id) {
        log.debug("Request to get TextTranslation : {}", id);
        return textTranslationRepository.findById(id);
    }

    /**
     * Delete the textTranslation by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete TextTranslation : {}", id);
        textTranslationRepository.deleteById(id);
        textTranslationSearchRepository.deleteById(id);
    }

    /**
     * Search for the textTranslation corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TextTranslation> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TextTranslations for query {}", query);
        return textTranslationSearchRepository.search(queryStringQuery(query), pageable);    }
}
