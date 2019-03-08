package fr.celexio.peaks.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.celexio.peaks.domain.TextTranslation;
import fr.celexio.peaks.service.TextTranslationService;
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
 * REST controller for managing TextTranslation.
 */
@RestController
@RequestMapping("/api")
public class TextTranslationResource {

    private final Logger log = LoggerFactory.getLogger(TextTranslationResource.class);

    private static final String ENTITY_NAME = "textTranslation";

    private final TextTranslationService textTranslationService;

    public TextTranslationResource(TextTranslationService textTranslationService) {
        this.textTranslationService = textTranslationService;
    }

    /**
     * POST  /text-translations : Create a new textTranslation.
     *
     * @param textTranslation the textTranslation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new textTranslation, or with status 400 (Bad Request) if the textTranslation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/text-translations")
    @Timed
    public ResponseEntity<TextTranslation> createTextTranslation(@Valid @RequestBody TextTranslation textTranslation) throws URISyntaxException {
        log.debug("REST request to save TextTranslation : {}", textTranslation);
        if (textTranslation.getId() != null) {
            throw new BadRequestAlertException("A new textTranslation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TextTranslation result = textTranslationService.save(textTranslation);
        return ResponseEntity.created(new URI("/api/text-translations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /text-translations : Updates an existing textTranslation.
     *
     * @param textTranslation the textTranslation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated textTranslation,
     * or with status 400 (Bad Request) if the textTranslation is not valid,
     * or with status 500 (Internal Server Error) if the textTranslation couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/text-translations")
    @Timed
    public ResponseEntity<TextTranslation> updateTextTranslation(@Valid @RequestBody TextTranslation textTranslation) throws URISyntaxException {
        log.debug("REST request to update TextTranslation : {}", textTranslation);
        if (textTranslation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TextTranslation result = textTranslationService.save(textTranslation);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, textTranslation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /text-translations : get all the textTranslations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of textTranslations in body
     */
    @GetMapping("/text-translations")
    @Timed
    public ResponseEntity<List<TextTranslation>> getAllTextTranslations(Pageable pageable) {
        log.debug("REST request to get a page of TextTranslations");
        Page<TextTranslation> page = textTranslationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/text-translations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /text-translations/:id : get the "id" textTranslation.
     *
     * @param id the id of the textTranslation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the textTranslation, or with status 404 (Not Found)
     */
    @GetMapping("/text-translations/{id}")
    @Timed
    public ResponseEntity<TextTranslation> getTextTranslation(@PathVariable Long id) {
        log.debug("REST request to get TextTranslation : {}", id);
        Optional<TextTranslation> textTranslation = textTranslationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(textTranslation);
    }

    /**
     * DELETE  /text-translations/:id : delete the "id" textTranslation.
     *
     * @param id the id of the textTranslation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/text-translations/{id}")
    @Timed
    public ResponseEntity<Void> deleteTextTranslation(@PathVariable Long id) {
        log.debug("REST request to delete TextTranslation : {}", id);
        textTranslationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/text-translations?query=:query : search for the textTranslation corresponding
     * to the query.
     *
     * @param query the query of the textTranslation search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/text-translations")
    @Timed
    public ResponseEntity<List<TextTranslation>> searchTextTranslations(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of TextTranslations for query {}", query);
        Page<TextTranslation> page = textTranslationService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/text-translations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
