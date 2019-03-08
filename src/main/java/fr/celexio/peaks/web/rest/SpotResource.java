package fr.celexio.peaks.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.celexio.peaks.domain.Spot;
import fr.celexio.peaks.service.SpotService;
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
 * REST controller for managing Spot.
 */
@RestController
@RequestMapping("/api")
public class SpotResource {

    private final Logger log = LoggerFactory.getLogger(SpotResource.class);

    private static final String ENTITY_NAME = "spot";

    private final SpotService spotService;

    public SpotResource(SpotService spotService) {
        this.spotService = spotService;
    }

    /**
     * POST  /spots : Create a new spot.
     *
     * @param spot the spot to create
     * @return the ResponseEntity with status 201 (Created) and with body the new spot, or with status 400 (Bad Request) if the spot has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/spots")
    @Timed
    public ResponseEntity<Spot> createSpot(@Valid @RequestBody Spot spot) throws URISyntaxException {
        log.debug("REST request to save Spot : {}", spot);
        if (spot.getId() != null) {
            throw new BadRequestAlertException("A new spot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Spot result = spotService.save(spot);
        return ResponseEntity.created(new URI("/api/spots/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /spots : Updates an existing spot.
     *
     * @param spot the spot to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated spot,
     * or with status 400 (Bad Request) if the spot is not valid,
     * or with status 500 (Internal Server Error) if the spot couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/spots")
    @Timed
    public ResponseEntity<Spot> updateSpot(@Valid @RequestBody Spot spot) throws URISyntaxException {
        log.debug("REST request to update Spot : {}", spot);
        if (spot.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Spot result = spotService.save(spot);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, spot.getId().toString()))
            .body(result);
    }

    /**
     * GET  /spots : get all the spots.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of spots in body
     */
    @GetMapping("/spots")
    @Timed
    public ResponseEntity<List<Spot>> getAllSpots(Pageable pageable) {
        log.debug("REST request to get a page of Spots");
        Page<Spot> page = spotService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/spots");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /spots/:id : get the "id" spot.
     *
     * @param id the id of the spot to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the spot, or with status 404 (Not Found)
     */
    @GetMapping("/spots/{id}")
    @Timed
    public ResponseEntity<Spot> getSpot(@PathVariable Long id) {
        log.debug("REST request to get Spot : {}", id);
        Optional<Spot> spot = spotService.findOne(id);
        return ResponseUtil.wrapOrNotFound(spot);
    }

    /**
     * DELETE  /spots/:id : delete the "id" spot.
     *
     * @param id the id of the spot to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/spots/{id}")
    @Timed
    public ResponseEntity<Void> deleteSpot(@PathVariable Long id) {
        log.debug("REST request to delete Spot : {}", id);
        spotService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/spots?query=:query : search for the spot corresponding
     * to the query.
     *
     * @param query the query of the spot search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/spots")
    @Timed
    public ResponseEntity<List<Spot>> searchSpots(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Spots for query {}", query);
        Page<Spot> page = spotService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/spots");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
