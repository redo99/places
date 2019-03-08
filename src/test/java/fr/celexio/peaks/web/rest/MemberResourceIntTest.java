package fr.celexio.peaks.web.rest;

import fr.celexio.peaks.PeaksApp;

import fr.celexio.peaks.domain.Member;
import fr.celexio.peaks.repository.MemberRepository;
import fr.celexio.peaks.repository.search.MemberSearchRepository;
import fr.celexio.peaks.service.MemberService;
import fr.celexio.peaks.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static fr.celexio.peaks.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MemberResource REST controller.
 *
 * @see MemberResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PeaksApp.class)
public class MemberResourceIntTest {

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private MemberService memberService;

    /**
     * This repository is mocked in the fr.celexio.peaks.repository.search test package.
     *
     * @see fr.celexio.peaks.repository.search.MemberSearchRepositoryMockConfiguration
     */
    @Autowired
    private MemberSearchRepository mockMemberSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMemberMockMvc;

    private Member member;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MemberResource memberResource = new MemberResource(memberService);
        this.restMemberMockMvc = MockMvcBuilders.standaloneSetup(memberResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Member createEntity(EntityManager em) {
        Member member = new Member();
        return member;
    }

    @Before
    public void initTest() {
        member = createEntity(em);
    }

    @Test
    @Transactional
    public void createMember() throws Exception {
        int databaseSizeBeforeCreate = memberRepository.findAll().size();

        // Create the Member
        restMemberMockMvc.perform(post("/api/members")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(member)))
            .andExpect(status().isCreated());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeCreate + 1);
        Member testMember = memberList.get(memberList.size() - 1);

        // Validate the Member in Elasticsearch
        verify(mockMemberSearchRepository, times(1)).save(testMember);
    }

    @Test
    @Transactional
    public void createMemberWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = memberRepository.findAll().size();

        // Create the Member with an existing ID
        member.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMemberMockMvc.perform(post("/api/members")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(member)))
            .andExpect(status().isBadRequest());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeCreate);

        // Validate the Member in Elasticsearch
        verify(mockMemberSearchRepository, times(0)).save(member);
    }

    @Test
    @Transactional
    public void getAllMembers() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList
        restMemberMockMvc.perform(get("/api/members?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(member.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getMember() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get the member
        restMemberMockMvc.perform(get("/api/members/{id}", member.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(member.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingMember() throws Exception {
        // Get the member
        restMemberMockMvc.perform(get("/api/members/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMember() throws Exception {
        // Initialize the database
        memberService.save(member);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockMemberSearchRepository);

        int databaseSizeBeforeUpdate = memberRepository.findAll().size();

        // Update the member
        Member updatedMember = memberRepository.findById(member.getId()).get();
        // Disconnect from session so that the updates on updatedMember are not directly saved in db
        em.detach(updatedMember);

        restMemberMockMvc.perform(put("/api/members")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMember)))
            .andExpect(status().isOk());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
        Member testMember = memberList.get(memberList.size() - 1);

        // Validate the Member in Elasticsearch
        verify(mockMemberSearchRepository, times(1)).save(testMember);
    }

    @Test
    @Transactional
    public void updateNonExistingMember() throws Exception {
        int databaseSizeBeforeUpdate = memberRepository.findAll().size();

        // Create the Member

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMemberMockMvc.perform(put("/api/members")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(member)))
            .andExpect(status().isBadRequest());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Member in Elasticsearch
        verify(mockMemberSearchRepository, times(0)).save(member);
    }

    @Test
    @Transactional
    public void deleteMember() throws Exception {
        // Initialize the database
        memberService.save(member);

        int databaseSizeBeforeDelete = memberRepository.findAll().size();

        // Get the member
        restMemberMockMvc.perform(delete("/api/members/{id}", member.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Member in Elasticsearch
        verify(mockMemberSearchRepository, times(1)).deleteById(member.getId());
    }

    @Test
    @Transactional
    public void searchMember() throws Exception {
        // Initialize the database
        memberService.save(member);
        when(mockMemberSearchRepository.search(queryStringQuery("id:" + member.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(member), PageRequest.of(0, 1), 1));
        // Search the member
        restMemberMockMvc.perform(get("/api/_search/members?query=id:" + member.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(member.getId().intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Member.class);
        Member member1 = new Member();
        member1.setId(1L);
        Member member2 = new Member();
        member2.setId(member1.getId());
        assertThat(member1).isEqualTo(member2);
        member2.setId(2L);
        assertThat(member1).isNotEqualTo(member2);
        member1.setId(null);
        assertThat(member1).isNotEqualTo(member2);
    }
}
