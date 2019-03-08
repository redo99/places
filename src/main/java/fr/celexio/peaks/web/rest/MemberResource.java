package fr.celexio.peaks.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.celexio.peaks.domain.Member;
import fr.celexio.peaks.service.MemberService;
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
 * REST controller for managing Member.
 */
@RestController
@RequestMapping("/api")
public class MemberResource {

    private final Logger log = LoggerFactory.getLogger(MemberResource.class);

    private static final String ENTITY_NAME = "member";

    private final MemberService memberService;

    public MemberResource(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * POST  /members : Create a new member.
     *
     * @param member the member to create
     * @return the ResponseEntity with status 201 (Created) and with body the new member, or with status 400 (Bad Request) if the member has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/members")
    @Timed
    public ResponseEntity<Member> createMember(@RequestBody Member member) throws URISyntaxException {
        log.debug("REST request to save Member : {}", member);
        if (member.getId() != null) {
            throw new BadRequestAlertException("A new member cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Member result = memberService.save(member);
        return ResponseEntity.created(new URI("/api/members/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /members : Updates an existing member.
     *
     * @param member the member to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated member,
     * or with status 400 (Bad Request) if the member is not valid,
     * or with status 500 (Internal Server Error) if the member couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/members")
    @Timed
    public ResponseEntity<Member> updateMember(@RequestBody Member member) throws URISyntaxException {
        log.debug("REST request to update Member : {}", member);
        if (member.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Member result = memberService.save(member);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, member.getId().toString()))
            .body(result);
    }

    /**
     * GET  /members : get all the members.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of members in body
     */
    @GetMapping("/members")
    @Timed
    public ResponseEntity<List<Member>> getAllMembers(Pageable pageable) {
        log.debug("REST request to get a page of Members");
        Page<Member> page = memberService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/members");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /members/:id : get the "id" member.
     *
     * @param id the id of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the member, or with status 404 (Not Found)
     */
    @GetMapping("/members/{id}")
    @Timed
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        log.debug("REST request to get Member : {}", id);
        Optional<Member> member = memberService.findOne(id);
        return ResponseUtil.wrapOrNotFound(member);
    }

    /**
     * DELETE  /members/:id : delete the "id" member.
     *
     * @param id the id of the member to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/members/{id}")
    @Timed
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        log.debug("REST request to delete Member : {}", id);
        memberService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/members?query=:query : search for the member corresponding
     * to the query.
     *
     * @param query the query of the member search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/members")
    @Timed
    public ResponseEntity<List<Member>> searchMembers(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Members for query {}", query);
        Page<Member> page = memberService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/members");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
