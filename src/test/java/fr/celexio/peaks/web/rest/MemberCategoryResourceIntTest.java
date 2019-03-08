package fr.celexio.peaks.web.rest;

import fr.celexio.peaks.PeaksApp;

import fr.celexio.peaks.domain.MemberCategory;
import fr.celexio.peaks.repository.MemberCategoryRepository;
import fr.celexio.peaks.repository.search.MemberCategorySearchRepository;
import fr.celexio.peaks.service.MemberCategoryService;
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
 * Test class for the MemberCategoryResource REST controller.
 *
 * @see MemberCategoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PeaksApp.class)
public class MemberCategoryResourceIntTest {

    @Autowired
    private MemberCategoryRepository memberCategoryRepository;
    
    @Autowired
    private MemberCategoryService memberCategoryService;

    /**
     * This repository is mocked in the fr.celexio.peaks.repository.search test package.
     *
     * @see fr.celexio.peaks.repository.search.MemberCategorySearchRepositoryMockConfiguration
     */
    @Autowired
    private MemberCategorySearchRepository mockMemberCategorySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMemberCategoryMockMvc;

    private MemberCategory memberCategory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MemberCategoryResource memberCategoryResource = new MemberCategoryResource(memberCategoryService);
        this.restMemberCategoryMockMvc = MockMvcBuilders.standaloneSetup(memberCategoryResource)
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
    public static MemberCategory createEntity(EntityManager em) {
        MemberCategory memberCategory = new MemberCategory();
        return memberCategory;
    }

    @Before
    public void initTest() {
        memberCategory = createEntity(em);
    }

    @Test
    @Transactional
    public void createMemberCategory() throws Exception {
        int databaseSizeBeforeCreate = memberCategoryRepository.findAll().size();

        // Create the MemberCategory
        restMemberCategoryMockMvc.perform(post("/api/member-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(memberCategory)))
            .andExpect(status().isCreated());

        // Validate the MemberCategory in the database
        List<MemberCategory> memberCategoryList = memberCategoryRepository.findAll();
        assertThat(memberCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        MemberCategory testMemberCategory = memberCategoryList.get(memberCategoryList.size() - 1);

        // Validate the MemberCategory in Elasticsearch
        verify(mockMemberCategorySearchRepository, times(1)).save(testMemberCategory);
    }

    @Test
    @Transactional
    public void createMemberCategoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = memberCategoryRepository.findAll().size();

        // Create the MemberCategory with an existing ID
        memberCategory.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMemberCategoryMockMvc.perform(post("/api/member-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(memberCategory)))
            .andExpect(status().isBadRequest());

        // Validate the MemberCategory in the database
        List<MemberCategory> memberCategoryList = memberCategoryRepository.findAll();
        assertThat(memberCategoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the MemberCategory in Elasticsearch
        verify(mockMemberCategorySearchRepository, times(0)).save(memberCategory);
    }

    @Test
    @Transactional
    public void getAllMemberCategories() throws Exception {
        // Initialize the database
        memberCategoryRepository.saveAndFlush(memberCategory);

        // Get all the memberCategoryList
        restMemberCategoryMockMvc.perform(get("/api/member-categories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(memberCategory.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getMemberCategory() throws Exception {
        // Initialize the database
        memberCategoryRepository.saveAndFlush(memberCategory);

        // Get the memberCategory
        restMemberCategoryMockMvc.perform(get("/api/member-categories/{id}", memberCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(memberCategory.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingMemberCategory() throws Exception {
        // Get the memberCategory
        restMemberCategoryMockMvc.perform(get("/api/member-categories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMemberCategory() throws Exception {
        // Initialize the database
        memberCategoryService.save(memberCategory);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockMemberCategorySearchRepository);

        int databaseSizeBeforeUpdate = memberCategoryRepository.findAll().size();

        // Update the memberCategory
        MemberCategory updatedMemberCategory = memberCategoryRepository.findById(memberCategory.getId()).get();
        // Disconnect from session so that the updates on updatedMemberCategory are not directly saved in db
        em.detach(updatedMemberCategory);

        restMemberCategoryMockMvc.perform(put("/api/member-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMemberCategory)))
            .andExpect(status().isOk());

        // Validate the MemberCategory in the database
        List<MemberCategory> memberCategoryList = memberCategoryRepository.findAll();
        assertThat(memberCategoryList).hasSize(databaseSizeBeforeUpdate);
        MemberCategory testMemberCategory = memberCategoryList.get(memberCategoryList.size() - 1);

        // Validate the MemberCategory in Elasticsearch
        verify(mockMemberCategorySearchRepository, times(1)).save(testMemberCategory);
    }

    @Test
    @Transactional
    public void updateNonExistingMemberCategory() throws Exception {
        int databaseSizeBeforeUpdate = memberCategoryRepository.findAll().size();

        // Create the MemberCategory

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMemberCategoryMockMvc.perform(put("/api/member-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(memberCategory)))
            .andExpect(status().isBadRequest());

        // Validate the MemberCategory in the database
        List<MemberCategory> memberCategoryList = memberCategoryRepository.findAll();
        assertThat(memberCategoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the MemberCategory in Elasticsearch
        verify(mockMemberCategorySearchRepository, times(0)).save(memberCategory);
    }

    @Test
    @Transactional
    public void deleteMemberCategory() throws Exception {
        // Initialize the database
        memberCategoryService.save(memberCategory);

        int databaseSizeBeforeDelete = memberCategoryRepository.findAll().size();

        // Get the memberCategory
        restMemberCategoryMockMvc.perform(delete("/api/member-categories/{id}", memberCategory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<MemberCategory> memberCategoryList = memberCategoryRepository.findAll();
        assertThat(memberCategoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the MemberCategory in Elasticsearch
        verify(mockMemberCategorySearchRepository, times(1)).deleteById(memberCategory.getId());
    }

    @Test
    @Transactional
    public void searchMemberCategory() throws Exception {
        // Initialize the database
        memberCategoryService.save(memberCategory);
        when(mockMemberCategorySearchRepository.search(queryStringQuery("id:" + memberCategory.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(memberCategory), PageRequest.of(0, 1), 1));
        // Search the memberCategory
        restMemberCategoryMockMvc.perform(get("/api/_search/member-categories?query=id:" + memberCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(memberCategory.getId().intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MemberCategory.class);
        MemberCategory memberCategory1 = new MemberCategory();
        memberCategory1.setId(1L);
        MemberCategory memberCategory2 = new MemberCategory();
        memberCategory2.setId(memberCategory1.getId());
        assertThat(memberCategory1).isEqualTo(memberCategory2);
        memberCategory2.setId(2L);
        assertThat(memberCategory1).isNotEqualTo(memberCategory2);
        memberCategory1.setId(null);
        assertThat(memberCategory1).isNotEqualTo(memberCategory2);
    }
}
