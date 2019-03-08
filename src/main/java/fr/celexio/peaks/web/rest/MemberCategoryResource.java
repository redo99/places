package fr.celexio.peaks.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.celexio.peaks.domain.MemberCategory;
import fr.celexio.peaks.service.MemberCategoryService;
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
 * REST controller for managing MemberCategory.
 */
@RestController
@RequestMapping("/api")
public class MemberCategoryResource {

    private final Logger log = LoggerFactory.getLogger(MemberCategoryResource.class);

    private static final String ENTITY_NAME = "memberCategory";

    private final MemberCategoryService memberCategoryService;

    public MemberCategoryResource(MemberCategoryService memberCategoryService) {
        this.memberCategoryService = memberCategoryService;
    }

    /**
     * POST  /member-categories : Create a new memberCategory.
     *
     * @param memberCategory the memberCategory to create
     * @return the ResponseEntity with status 201 (Created) and with body the new memberCategory, or with status 400 (Bad Request) if the memberCategory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/member-categories")
    @Timed
    public ResponseEntity<MemberCategory> createMemberCategory(@RequestBody MemberCategory memberCategory) throws URISyntaxException {
        log.debug("REST request to save MemberCategory : {}", memberCategory);
        if (memberCategory.getId() != null) {
            throw new BadRequestAlertException("A new memberCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MemberCategory result = memberCategoryService.save(memberCategory);
        return ResponseEntity.created(new URI("/api/member-categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /member-categories : Updates an existing memberCategory.
     *
     * @param memberCategory the memberCategory to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated memberCategory,
     * or with status 400 (Bad Request) if the memberCategory is not valid,
     * or with status 500 (Internal Server Error) if the memberCategory couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/member-categories")
    @Timed
    public ResponseEntity<MemberCategory> updateMemberCategory(@RequestBody MemberCategory memberCategory) throws URISyntaxException {
        log.debug("REST request to update MemberCategory : {}", memberCategory);
        if (memberCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        MemberCategory result = memberCategoryService.save(memberCategory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, memberCategory.getId().toString()))
            .body(result);
    }

    /**
     * GET  /member-categories : get all the memberCategories.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of memberCategories in body
     */
    @GetMapping("/member-categories")
    @Timed
    public ResponseEntity<List<MemberCategory>> getAllMemberCategories(Pageable pageable) {
        log.debug("REST request to get a page of MemberCategories");
        Page<MemberCategory> page = memberCategoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/member-categories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /member-categories/:id : get the "id" memberCategory.
     *
     * @param id the id of the memberCategory to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the memberCategory, or with status 404 (Not Found)
     */
    @GetMapping("/member-categories/{id}")
    @Timed
    public ResponseEntity<MemberCategory> getMemberCategory(@PathVariable Long id) {
        log.debug("REST request to get MemberCategory : {}", id);
        Optional<MemberCategory> memberCategory = memberCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(memberCategory);
    }

    /**
     * DELETE  /member-categories/:id : delete the "id" memberCategory.
     *
     * @param id the id of the memberCategory to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/member-categories/{id}")
    @Timed
    public ResponseEntity<Void> deleteMemberCategory(@PathVariable Long id) {
        log.debug("REST request to delete MemberCategory : {}", id);
        memberCategoryService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/member-categories?query=:query : search for the memberCategory corresponding
     * to the query.
     *
     * @param query the query of the memberCategory search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/member-categories")
    @Timed
    public ResponseEntity<List<MemberCategory>> searchMemberCategories(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of MemberCategories for query {}", query);
        Page<MemberCategory> page = memberCategoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/member-categories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
