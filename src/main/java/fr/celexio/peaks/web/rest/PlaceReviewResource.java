package fr.celexio.peaks.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.celexio.peaks.domain.PlaceReview;
import fr.celexio.peaks.service.PlaceReviewService;
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
 * REST controller for managing PlaceReview.
 */
@RestController
@RequestMapping("/api")
public class PlaceReviewResource {

    private final Logger log = LoggerFactory.getLogger(PlaceReviewResource.class);

    private static final String ENTITY_NAME = "placeReview";

    private final PlaceReviewService placeReviewService;

    public PlaceReviewResource(PlaceReviewService placeReviewService) {
        this.placeReviewService = placeReviewService;
    }

    /**
     * POST  /place-reviews : Create a new placeReview.
     *
     * @param placeReview the placeReview to create
     * @return the ResponseEntity with status 201 (Created) and with body the new placeReview, or with status 400 (Bad Request) if the placeReview has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/place-reviews")
    @Timed
    public ResponseEntity<PlaceReview> createPlaceReview(@Valid @RequestBody PlaceReview placeReview) throws URISyntaxException {
        log.debug("REST request to save PlaceReview : {}", placeReview);
        if (placeReview.getId() != null) {
            throw new BadRequestAlertException("A new placeReview cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PlaceReview result = placeReviewService.save(placeReview);
        return ResponseEntity.created(new URI("/api/place-reviews/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /place-reviews : Updates an existing placeReview.
     *
     * @param placeReview the placeReview to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated placeReview,
     * or with status 400 (Bad Request) if the placeReview is not valid,
     * or with status 500 (Internal Server Error) if the placeReview couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/place-reviews")
    @Timed
    public ResponseEntity<PlaceReview> updatePlaceReview(@Valid @RequestBody PlaceReview placeReview) throws URISyntaxException {
        log.debug("REST request to update PlaceReview : {}", placeReview);
        if (placeReview.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PlaceReview result = placeReviewService.save(placeReview);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, placeReview.getId().toString()))
            .body(result);
    }

    /**
     * GET  /place-reviews : get all the placeReviews.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of placeReviews in body
     */
    @GetMapping("/place-reviews")
    @Timed
    public ResponseEntity<List<PlaceReview>> getAllPlaceReviews(Pageable pageable) {
        log.debug("REST request to get a page of PlaceReviews");
        Page<PlaceReview> page = placeReviewService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/place-reviews");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /place-reviews/:id : get the "id" placeReview.
     *
     * @param id the id of the placeReview to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the placeReview, or with status 404 (Not Found)
     */
    @GetMapping("/place-reviews/{id}")
    @Timed
    public ResponseEntity<PlaceReview> getPlaceReview(@PathVariable Long id) {
        log.debug("REST request to get PlaceReview : {}", id);
        Optional<PlaceReview> placeReview = placeReviewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(placeReview);
    }

    /**
     * DELETE  /place-reviews/:id : delete the "id" placeReview.
     *
     * @param id the id of the placeReview to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/place-reviews/{id}")
    @Timed
    public ResponseEntity<Void> deletePlaceReview(@PathVariable Long id) {
        log.debug("REST request to delete PlaceReview : {}", id);
        placeReviewService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/place-reviews?query=:query : search for the placeReview corresponding
     * to the query.
     *
     * @param query the query of the placeReview search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/place-reviews")
    @Timed
    public ResponseEntity<List<PlaceReview>> searchPlaceReviews(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of PlaceReviews for query {}", query);
        Page<PlaceReview> page = placeReviewService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/place-reviews");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
