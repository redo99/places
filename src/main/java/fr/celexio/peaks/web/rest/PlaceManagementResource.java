package fr.celexio.peaks.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.celexio.peaks.domain.PlaceManagement;
import fr.celexio.peaks.service.PlaceManagementService;
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

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing PlaceManagement.
 */
@RestController
@RequestMapping("/api")
public class PlaceManagementResource {

    private final Logger log = LoggerFactory.getLogger(PlaceManagementResource.class);

    private static final String ENTITY_NAME = "placeManagement";

    private final PlaceManagementService placeManagementService;

    public PlaceManagementResource(PlaceManagementService placeManagementService) {
        this.placeManagementService = placeManagementService;
    }

    /**
     * POST  /place-managements : Create a new placeManagement.
     *
     * @param placeManagement the placeManagement to create
     * @return the ResponseEntity with status 201 (Created) and with body the new placeManagement, or with status 400 (Bad Request) if the placeManagement has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/place-managements")
    @Timed
    public ResponseEntity<PlaceManagement> createPlaceManagement(@RequestBody PlaceManagement placeManagement) throws URISyntaxException {
        log.debug("REST request to save PlaceManagement : {}", placeManagement);
        if (placeManagement.getId() != null) {
            throw new BadRequestAlertException("A new placeManagement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PlaceManagement result = placeManagementService.save(placeManagement);
        return ResponseEntity.created(new URI("/api/place-managements/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /place-managements : Updates an existing placeManagement.
     *
     * @param placeManagement the placeManagement to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated placeManagement,
     * or with status 400 (Bad Request) if the placeManagement is not valid,
     * or with status 500 (Internal Server Error) if the placeManagement couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/place-managements")
    @Timed
    public ResponseEntity<PlaceManagement> updatePlaceManagement(@RequestBody PlaceManagement placeManagement) throws URISyntaxException {
        log.debug("REST request to update PlaceManagement : {}", placeManagement);
        if (placeManagement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PlaceManagement result = placeManagementService.save(placeManagement);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, placeManagement.getId().toString()))
            .body(result);
    }

    /**
     * GET  /place-managements : get all the placeManagements.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of placeManagements in body
     */
    @GetMapping("/place-managements")
    @Timed
    public ResponseEntity<List<PlaceManagement>> getAllPlaceManagements(Pageable pageable) {
        log.debug("REST request to get a page of PlaceManagements");
        Page<PlaceManagement> page = placeManagementService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/place-managements");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /place-managements/:id : get the "id" placeManagement.
     *
     * @param id the id of the placeManagement to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the placeManagement, or with status 404 (Not Found)
     */
    @GetMapping("/place-managements/{id}")
    @Timed
    public ResponseEntity<PlaceManagement> getPlaceManagement(@PathVariable Long id) {
        log.debug("REST request to get PlaceManagement : {}", id);
        Optional<PlaceManagement> placeManagement = placeManagementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(placeManagement);
    }

    /**
     * DELETE  /place-managements/:id : delete the "id" placeManagement.
     *
     * @param id the id of the placeManagement to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/place-managements/{id}")
    @Timed
    public ResponseEntity<Void> deletePlaceManagement(@PathVariable Long id) {
        log.debug("REST request to delete PlaceManagement : {}", id);
        placeManagementService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/place-managements?query=:query : search for the placeManagement corresponding
     * to the query.
     *
     * @param query the query of the placeManagement search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/place-managements")
    @Timed
    public ResponseEntity<List<PlaceManagement>> searchPlaceManagements(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of PlaceManagements for query {}", query);
        Page<PlaceManagement> page = placeManagementService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/place-managements");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
