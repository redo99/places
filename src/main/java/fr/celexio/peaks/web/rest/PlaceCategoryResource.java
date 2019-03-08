package fr.celexio.peaks.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.celexio.peaks.domain.PlaceCategory;
import fr.celexio.peaks.service.PlaceCategoryService;
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
 * REST controller for managing PlaceCategory.
 */
@RestController
@RequestMapping("/api")
public class PlaceCategoryResource {

    private final Logger log = LoggerFactory.getLogger(PlaceCategoryResource.class);

    private static final String ENTITY_NAME = "placeCategory";

    private final PlaceCategoryService placeCategoryService;

    public PlaceCategoryResource(PlaceCategoryService placeCategoryService) {
        this.placeCategoryService = placeCategoryService;
    }

    /**
     * POST  /place-categories : Create a new placeCategory.
     *
     * @param placeCategory the placeCategory to create
     * @return the ResponseEntity with status 201 (Created) and with body the new placeCategory, or with status 400 (Bad Request) if the placeCategory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/place-categories")
    @Timed
    public ResponseEntity<PlaceCategory> createPlaceCategory(@RequestBody PlaceCategory placeCategory) throws URISyntaxException {
        log.debug("REST request to save PlaceCategory : {}", placeCategory);
        if (placeCategory.getId() != null) {
            throw new BadRequestAlertException("A new placeCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PlaceCategory result = placeCategoryService.save(placeCategory);
        return ResponseEntity.created(new URI("/api/place-categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /place-categories : Updates an existing placeCategory.
     *
     * @param placeCategory the placeCategory to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated placeCategory,
     * or with status 400 (Bad Request) if the placeCategory is not valid,
     * or with status 500 (Internal Server Error) if the placeCategory couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/place-categories")
    @Timed
    public ResponseEntity<PlaceCategory> updatePlaceCategory(@RequestBody PlaceCategory placeCategory) throws URISyntaxException {
        log.debug("REST request to update PlaceCategory : {}", placeCategory);
        if (placeCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PlaceCategory result = placeCategoryService.save(placeCategory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, placeCategory.getId().toString()))
            .body(result);
    }

    /**
     * GET  /place-categories : get all the placeCategories.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of placeCategories in body
     */
    @GetMapping("/place-categories")
    @Timed
    public ResponseEntity<List<PlaceCategory>> getAllPlaceCategories(Pageable pageable) {
        log.debug("REST request to get a page of PlaceCategories");
        Page<PlaceCategory> page = placeCategoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/place-categories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /place-categories/:id : get the "id" placeCategory.
     *
     * @param id the id of the placeCategory to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the placeCategory, or with status 404 (Not Found)
     */
    @GetMapping("/place-categories/{id}")
    @Timed
    public ResponseEntity<PlaceCategory> getPlaceCategory(@PathVariable Long id) {
        log.debug("REST request to get PlaceCategory : {}", id);
        Optional<PlaceCategory> placeCategory = placeCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(placeCategory);
    }

    /**
     * DELETE  /place-categories/:id : delete the "id" placeCategory.
     *
     * @param id the id of the placeCategory to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/place-categories/{id}")
    @Timed
    public ResponseEntity<Void> deletePlaceCategory(@PathVariable Long id) {
        log.debug("REST request to delete PlaceCategory : {}", id);
        placeCategoryService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/place-categories?query=:query : search for the placeCategory corresponding
     * to the query.
     *
     * @param query the query of the placeCategory search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/place-categories")
    @Timed
    public ResponseEntity<List<PlaceCategory>> searchPlaceCategories(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of PlaceCategories for query {}", query);
        Page<PlaceCategory> page = placeCategoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/place-categories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
