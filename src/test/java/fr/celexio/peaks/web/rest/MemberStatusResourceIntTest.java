package fr.celexio.peaks.web.rest;

import fr.celexio.peaks.PeaksApp;

import fr.celexio.peaks.domain.MemberStatus;
import fr.celexio.peaks.repository.MemberStatusRepository;
import fr.celexio.peaks.repository.search.MemberStatusSearchRepository;
import fr.celexio.peaks.service.MemberStatusService;
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
 * Test class for the MemberStatusResource REST controller.
 *
 * @see MemberStatusResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PeaksApp.class)
public class MemberStatusResourceIntTest {

    @Autowired
    private MemberStatusRepository memberStatusRepository;
    
    @Autowired
    private MemberStatusService memberStatusService;

    /**
     * This repository is mocked in the fr.celexio.peaks.repository.search test package.
     *
     * @see fr.celexio.peaks.repository.search.MemberStatusSearchRepositoryMockConfiguration
     */
    @Autowired
    private MemberStatusSearchRepository mockMemberStatusSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMemberStatusMockMvc;

    private MemberStatus memberStatus;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MemberStatusResource memberStatusResource = new MemberStatusResource(memberStatusService);
        this.restMemberStatusMockMvc = MockMvcBuilders.standaloneSetup(memberStatusResource)
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
    public static MemberStatus createEntity(EntityManager em) {
        MemberStatus memberStatus = new MemberStatus();
        return memberStatus;
    }

    @Before
    public void initTest() {
        memberStatus = createEntity(em);
    }

    @Test
    @Transactional
    public void createMemberStatus() throws Exception {
        int databaseSizeBeforeCreate = memberStatusRepository.findAll().size();

        // Create the MemberStatus
        restMemberStatusMockMvc.perform(post("/api/member-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(memberStatus)))
            .andExpect(status().isCreated());

        // Validate the MemberStatus in the database
        List<MemberStatus> memberStatusList = memberStatusRepository.findAll();
        assertThat(memberStatusList).hasSize(databaseSizeBeforeCreate + 1);
        MemberStatus testMemberStatus = memberStatusList.get(memberStatusList.size() - 1);

        // Validate the MemberStatus in Elasticsearch
        verify(mockMemberStatusSearchRepository, times(1)).save(testMemberStatus);
    }

    @Test
    @Transactional
    public void createMemberStatusWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = memberStatusRepository.findAll().size();

        // Create the MemberStatus with an existing ID
        memberStatus.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMemberStatusMockMvc.perform(post("/api/member-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(memberStatus)))
            .andExpect(status().isBadRequest());

        // Validate the MemberStatus in the database
        List<MemberStatus> memberStatusList = memberStatusRepository.findAll();
        assertThat(memberStatusList).hasSize(databaseSizeBeforeCreate);

        // Validate the MemberStatus in Elasticsearch
        verify(mockMemberStatusSearchRepository, times(0)).save(memberStatus);
    }

    @Test
    @Transactional
    public void getAllMemberStatuses() throws Exception {
        // Initialize the database
        memberStatusRepository.saveAndFlush(memberStatus);

        // Get all the memberStatusList
        restMemberStatusMockMvc.perform(get("/api/member-statuses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(memberStatus.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getMemberStatus() throws Exception {
        // Initialize the database
        memberStatusRepository.saveAndFlush(memberStatus);

        // Get the memberStatus
        restMemberStatusMockMvc.perform(get("/api/member-statuses/{id}", memberStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(memberStatus.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingMemberStatus() throws Exception {
        // Get the memberStatus
        restMemberStatusMockMvc.perform(get("/api/member-statuses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMemberStatus() throws Exception {
        // Initialize the database
        memberStatusService.save(memberStatus);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockMemberStatusSearchRepository);

        int databaseSizeBeforeUpdate = memberStatusRepository.findAll().size();

        // Update the memberStatus
        MemberStatus updatedMemberStatus = memberStatusRepository.findById(memberStatus.getId()).get();
        // Disconnect from session so that the updates on updatedMemberStatus are not directly saved in db
        em.detach(updatedMemberStatus);

        restMemberStatusMockMvc.perform(put("/api/member-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMemberStatus)))
            .andExpect(status().isOk());

        // Validate the MemberStatus in the database
        List<MemberStatus> memberStatusList = memberStatusRepository.findAll();
        assertThat(memberStatusList).hasSize(databaseSizeBeforeUpdate);
        MemberStatus testMemberStatus = memberStatusList.get(memberStatusList.size() - 1);

        // Validate the MemberStatus in Elasticsearch
        verify(mockMemberStatusSearchRepository, times(1)).save(testMemberStatus);
    }

    @Test
    @Transactional
    public void updateNonExistingMemberStatus() throws Exception {
        int databaseSizeBeforeUpdate = memberStatusRepository.findAll().size();

        // Create the MemberStatus

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMemberStatusMockMvc.perform(put("/api/member-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(memberStatus)))
            .andExpect(status().isBadRequest());

        // Validate the MemberStatus in the database
        List<MemberStatus> memberStatusList = memberStatusRepository.findAll();
        assertThat(memberStatusList).hasSize(databaseSizeBeforeUpdate);

        // Validate the MemberStatus in Elasticsearch
        verify(mockMemberStatusSearchRepository, times(0)).save(memberStatus);
    }

    @Test
    @Transactional
    public void deleteMemberStatus() throws Exception {
        // Initialize the database
        memberStatusService.save(memberStatus);

        int databaseSizeBeforeDelete = memberStatusRepository.findAll().size();

        // Get the memberStatus
        restMemberStatusMockMvc.perform(delete("/api/member-statuses/{id}", memberStatus.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<MemberStatus> memberStatusList = memberStatusRepository.findAll();
        assertThat(memberStatusList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the MemberStatus in Elasticsearch
        verify(mockMemberStatusSearchRepository, times(1)).deleteById(memberStatus.getId());
    }

    @Test
    @Transactional
    public void searchMemberStatus() throws Exception {
        // Initialize the database
        memberStatusService.save(memberStatus);
        when(mockMemberStatusSearchRepository.search(queryStringQuery("id:" + memberStatus.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(memberStatus), PageRequest.of(0, 1), 1));
        // Search the memberStatus
        restMemberStatusMockMvc.perform(get("/api/_search/member-statuses?query=id:" + memberStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(memberStatus.getId().intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MemberStatus.class);
        MemberStatus memberStatus1 = new MemberStatus();
        memberStatus1.setId(1L);
        MemberStatus memberStatus2 = new MemberStatus();
        memberStatus2.setId(memberStatus1.getId());
        assertThat(memberStatus1).isEqualTo(memberStatus2);
        memberStatus2.setId(2L);
        assertThat(memberStatus1).isNotEqualTo(memberStatus2);
        memberStatus1.setId(null);
        assertThat(memberStatus1).isNotEqualTo(memberStatus2);
    }
}
