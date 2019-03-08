package fr.celexio.peaks.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.celexio.peaks.domain.MemberStatus;
import fr.celexio.peaks.service.MemberStatusService;
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
 * REST controller for managing MemberStatus.
 */
@RestController
@RequestMapping("/api")
public class MemberStatusResource {

    private final Logger log = LoggerFactory.getLogger(MemberStatusResource.class);

    private static final String ENTITY_NAME = "memberStatus";

    private final MemberStatusService memberStatusService;

    public MemberStatusResource(MemberStatusService memberStatusService) {
        this.memberStatusService = memberStatusService;
    }

    /**
     * POST  /member-statuses : Create a new memberStatus.
     *
     * @param memberStatus the memberStatus to create
     * @return the ResponseEntity with status 201 (Created) and with body the new memberStatus, or with status 400 (Bad Request) if the memberStatus has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/member-statuses")
    @Timed
    public ResponseEntity<MemberStatus> createMemberStatus(@RequestBody MemberStatus memberStatus) throws URISyntaxException {
        log.debug("REST request to save MemberStatus : {}", memberStatus);
        if (memberStatus.getId() != null) {
            throw new BadRequestAlertException("A new memberStatus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MemberStatus result = memberStatusService.save(memberStatus);
        return ResponseEntity.created(new URI("/api/member-statuses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /member-statuses : Updates an existing memberStatus.
     *
     * @param memberStatus the memberStatus to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated memberStatus,
     * or with status 400 (Bad Request) if the memberStatus is not valid,
     * or with status 500 (Internal Server Error) if the memberStatus couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/member-statuses")
    @Timed
    public ResponseEntity<MemberStatus> updateMemberStatus(@RequestBody MemberStatus memberStatus) throws URISyntaxException {
        log.debug("REST request to update MemberStatus : {}", memberStatus);
        if (memberStatus.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        MemberStatus result = memberStatusService.save(memberStatus);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, memberStatus.getId().toString()))
            .body(result);
    }

    /**
     * GET  /member-statuses : get all the memberStatuses.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of memberStatuses in body
     */
    @GetMapping("/member-statuses")
    @Timed
    public ResponseEntity<List<MemberStatus>> getAllMemberStatuses(Pageable pageable) {
        log.debug("REST request to get a page of MemberStatuses");
        Page<MemberStatus> page = memberStatusService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/member-statuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /member-statuses/:id : get the "id" memberStatus.
     *
     * @param id the id of the memberStatus to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the memberStatus, or with status 404 (Not Found)
     */
    @GetMapping("/member-statuses/{id}")
    @Timed
    public ResponseEntity<MemberStatus> getMemberStatus(@PathVariable Long id) {
        log.debug("REST request to get MemberStatus : {}", id);
        Optional<MemberStatus> memberStatus = memberStatusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(memberStatus);
    }

    /**
     * DELETE  /member-statuses/:id : delete the "id" memberStatus.
     *
     * @param id the id of the memberStatus to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/member-statuses/{id}")
    @Timed
    public ResponseEntity<Void> deleteMemberStatus(@PathVariable Long id) {
        log.debug("REST request to delete MemberStatus : {}", id);
        memberStatusService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/member-statuses?query=:query : search for the memberStatus corresponding
     * to the query.
     *
     * @param query the query of the memberStatus search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/member-statuses")
    @Timed
    public ResponseEntity<List<MemberStatus>> searchMemberStatuses(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of MemberStatuses for query {}", query);
        Page<MemberStatus> page = memberStatusService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/member-statuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
