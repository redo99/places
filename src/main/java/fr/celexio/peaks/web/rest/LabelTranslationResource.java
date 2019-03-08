package fr.celexio.peaks.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.celexio.peaks.domain.LabelTranslation;
import fr.celexio.peaks.service.LabelTranslationService;
import fr.celexio.peaks.web.rest.errors.BadRequestAlertException;
import fr.celexio.peaks.web.rest.util.HeaderUtil;
import fr.celexio.peaks.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing LabelTranslation.
 */
@RestController
@RequestMapping("/api")
public class LabelTranslationResource {

    private final Logger log = LoggerFactory.getLogger(LabelTranslationResource.class);

    private static final String ENTITY_NAME = "labelTranslation";

    private final LabelTranslationService labelTranslationService;

    public LabelTranslationResource(LabelTranslationService labelTranslationService) {
        this.labelTranslationService = labelTranslationService;
    }

    /**
     * POST  /label-translations : Create a new labelTranslation.
     *
     * @param labelTranslation the labelTranslation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new labelTranslation, or with status 400 (Bad Request) if the labelTranslation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/label-translations")
    @Timed
    public ResponseEntity<LabelTranslation> createLabelTranslation(@Valid @RequestBody LabelTranslation labelTranslation) throws URISyntaxException {
        log.debug("REST request to save LabelTranslation : {}", labelTranslation);
        if (labelTranslation.getId() != null) {
            throw new BadRequestAlertException("A new labelTranslation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LabelTranslation result = labelTranslationService.save(labelTranslation);
        return ResponseEntity.created(new URI("/api/label-translations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /label-translations : Updates an existing labelTranslation.
     *
     * @param labelTranslation the labelTranslation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated labelTranslation,
     * or with status 400 (Bad Request) if the labelTranslation is not valid,
     * or with status 500 (Internal Server Error) if the labelTranslation couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/label-translations")
    @Timed
    public ResponseEntity<LabelTranslation> updateLabelTranslation(@Valid @RequestBody LabelTranslation labelTranslation) throws URISyntaxException {
        log.debug("REST request to update LabelTranslation : {}", labelTranslation);
        if (labelTranslation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LabelTranslation result = labelTranslationService.save(labelTranslation);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, labelTranslation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /label-translations : get all the labelTranslations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of labelTranslations in body
     */
    @GetMapping("/label-translations")
    @Timed
    public ResponseEntity<List<LabelTranslation>> getAllLabelTranslations(Pageable pageable) {
        log.debug("REST request to get a page of LabelTranslations");
        Page<LabelTranslation> page = labelTranslationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/label-translations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /label-translations/:id : get the "id" labelTranslation.
     *
     * @param id the id of the labelTranslation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the labelTranslation, or with status 404 (Not Found)
     */
    @GetMapping("/label-translations/{id}")
    @Timed
    public ResponseEntity<LabelTranslation> getLabelTranslation(@PathVariable Long id) {
        log.debug("REST request to get LabelTranslation : {}", id);
        Optional<LabelTranslation> labelTranslation = labelTranslationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(labelTranslation);
    }

    /**
     * DELETE  /label-translations/:id : delete the "id" labelTranslation.
     *
     * @param id the id of the labelTranslation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/label-translations/{id}")
    @Timed
    public ResponseEntity<Void> deleteLabelTranslation(@PathVariable Long id) {
        log.debug("REST request to delete LabelTranslation : {}", id);
        labelTranslationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/label-translations?query=:query : search for the labelTranslation corresponding
     * to the query.
     *
     * @param query the query of the labelTranslation search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/label-translations")
    @Timed
    public ResponseEntity<List<LabelTranslation>> searchLabelTranslations(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of LabelTranslations for query {}", query);
        Page<LabelTranslation> page = labelTranslationService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/label-translations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
