package fr.celexio.peaks.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.celexio.peaks.domain.ActivityFamily;
import fr.celexio.peaks.service.ActivityFamilyService;
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
 * REST controller for managing ActivityFamily.
 */
@RestController
@RequestMapping("/api")
public class ActivityFamilyResource {

    private final Logger log = LoggerFactory.getLogger(ActivityFamilyResource.class);

    private static final String ENTITY_NAME = "activityFamily";

    private final ActivityFamilyService activityFamilyService;

    public ActivityFamilyResource(ActivityFamilyService activityFamilyService) {
        this.activityFamilyService = activityFamilyService;
    }

    /**
     * POST  /activity-families : Create a new activityFamily.
     *
     * @param activityFamily the activityFamily to create
     * @return the ResponseEntity with status 201 (Created) and with body the new activityFamily, or with status 400 (Bad Request) if the activityFamily has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/activity-families")
    @Timed
    public ResponseEntity<ActivityFamily> createActivityFamily(@RequestBody ActivityFamily activityFamily) throws URISyntaxException {
        log.debug("REST request to save ActivityFamily : {}", activityFamily);
        if (activityFamily.getId() != null) {
            throw new BadRequestAlertException("A new activityFamily cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ActivityFamily result = activityFamilyService.save(activityFamily);
        return ResponseEntity.created(new URI("/api/activity-families/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /activity-families : Updates an existing activityFamily.
     *
     * @param activityFamily the activityFamily to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated activityFamily,
     * or with status 400 (Bad Request) if the activityFamily is not valid,
     * or with status 500 (Internal Server Error) if the activityFamily couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/activity-families")
    @Timed
    public ResponseEntity<ActivityFamily> updateActivityFamily(@RequestBody ActivityFamily activityFamily) throws URISyntaxException {
        log.debug("REST request to update ActivityFamily : {}", activityFamily);
        if (activityFamily.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ActivityFamily result = activityFamilyService.save(activityFamily);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, activityFamily.getId().toString()))
            .body(result);
    }

    /**
     * GET  /activity-families : get all the activityFamilies.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of activityFamilies in body
     */
    @GetMapping("/activity-families")
    @Timed
    public ResponseEntity<List<ActivityFamily>> getAllActivityFamilies(Pageable pageable) {
        log.debug("REST request to get a page of ActivityFamilies");
        Page<ActivityFamily> page = activityFamilyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/activity-families");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /activity-families/:id : get the "id" activityFamily.
     *
     * @param id the id of the activityFamily to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the activityFamily, or with status 404 (Not Found)
     */
    @GetMapping("/activity-families/{id}")
    @Timed
    public ResponseEntity<ActivityFamily> getActivityFamily(@PathVariable Long id) {
        log.debug("REST request to get ActivityFamily : {}", id);
        Optional<ActivityFamily> activityFamily = activityFamilyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(activityFamily);
    }

    /**
     * DELETE  /activity-families/:id : delete the "id" activityFamily.
     *
     * @param id the id of the activityFamily to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/activity-families/{id}")
    @Timed
    public ResponseEntity<Void> deleteActivityFamily(@PathVariable Long id) {
        log.debug("REST request to delete ActivityFamily : {}", id);
        activityFamilyService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/activity-families?query=:query : search for the activityFamily corresponding
     * to the query.
     *
     * @param query the query of the activityFamily search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/activity-families")
    @Timed
    public ResponseEntity<List<ActivityFamily>> searchActivityFamilies(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of ActivityFamilies for query {}", query);
        Page<ActivityFamily> page = activityFamilyService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/activity-families");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
