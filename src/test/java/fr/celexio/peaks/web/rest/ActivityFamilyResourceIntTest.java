package fr.celexio.peaks.web.rest;

import fr.celexio.peaks.PeaksApp;

import fr.celexio.peaks.domain.ActivityFamily;
import fr.celexio.peaks.repository.ActivityFamilyRepository;
import fr.celexio.peaks.repository.search.ActivityFamilySearchRepository;
import fr.celexio.peaks.service.ActivityFamilyService;
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
 * Test class for the ActivityFamilyResource REST controller.
 *
 * @see ActivityFamilyResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PeaksApp.class)
public class ActivityFamilyResourceIntTest {

    @Autowired
    private ActivityFamilyRepository activityFamilyRepository;
    
    @Autowired
    private ActivityFamilyService activityFamilyService;

    /**
     * This repository is mocked in the fr.celexio.peaks.repository.search test package.
     *
     * @see fr.celexio.peaks.repository.search.ActivityFamilySearchRepositoryMockConfiguration
     */
    @Autowired
    private ActivityFamilySearchRepository mockActivityFamilySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restActivityFamilyMockMvc;

    private ActivityFamily activityFamily;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ActivityFamilyResource activityFamilyResource = new ActivityFamilyResource(activityFamilyService);
        this.restActivityFamilyMockMvc = MockMvcBuilders.standaloneSetup(activityFamilyResource)
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
    public static ActivityFamily createEntity(EntityManager em) {
        ActivityFamily activityFamily = new ActivityFamily();
        return activityFamily;
    }

    @Before
    public void initTest() {
        activityFamily = createEntity(em);
    }

    @Test
    @Transactional
    public void createActivityFamily() throws Exception {
        int databaseSizeBeforeCreate = activityFamilyRepository.findAll().size();

        // Create the ActivityFamily
        restActivityFamilyMockMvc.perform(post("/api/activity-families")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityFamily)))
            .andExpect(status().isCreated());

        // Validate the ActivityFamily in the database
        List<ActivityFamily> activityFamilyList = activityFamilyRepository.findAll();
        assertThat(activityFamilyList).hasSize(databaseSizeBeforeCreate + 1);
        ActivityFamily testActivityFamily = activityFamilyList.get(activityFamilyList.size() - 1);

        // Validate the ActivityFamily in Elasticsearch
        verify(mockActivityFamilySearchRepository, times(1)).save(testActivityFamily);
    }

    @Test
    @Transactional
    public void createActivityFamilyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = activityFamilyRepository.findAll().size();

        // Create the ActivityFamily with an existing ID
        activityFamily.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restActivityFamilyMockMvc.perform(post("/api/activity-families")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityFamily)))
            .andExpect(status().isBadRequest());

        // Validate the ActivityFamily in the database
        List<ActivityFamily> activityFamilyList = activityFamilyRepository.findAll();
        assertThat(activityFamilyList).hasSize(databaseSizeBeforeCreate);

        // Validate the ActivityFamily in Elasticsearch
        verify(mockActivityFamilySearchRepository, times(0)).save(activityFamily);
    }

    @Test
    @Transactional
    public void getAllActivityFamilies() throws Exception {
        // Initialize the database
        activityFamilyRepository.saveAndFlush(activityFamily);

        // Get all the activityFamilyList
        restActivityFamilyMockMvc.perform(get("/api/activity-families?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activityFamily.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getActivityFamily() throws Exception {
        // Initialize the database
        activityFamilyRepository.saveAndFlush(activityFamily);

        // Get the activityFamily
        restActivityFamilyMockMvc.perform(get("/api/activity-families/{id}", activityFamily.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(activityFamily.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingActivityFamily() throws Exception {
        // Get the activityFamily
        restActivityFamilyMockMvc.perform(get("/api/activity-families/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateActivityFamily() throws Exception {
        // Initialize the database
        activityFamilyService.save(activityFamily);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockActivityFamilySearchRepository);

        int databaseSizeBeforeUpdate = activityFamilyRepository.findAll().size();

        // Update the activityFamily
        ActivityFamily updatedActivityFamily = activityFamilyRepository.findById(activityFamily.getId()).get();
        // Disconnect from session so that the updates on updatedActivityFamily are not directly saved in db
        em.detach(updatedActivityFamily);

        restActivityFamilyMockMvc.perform(put("/api/activity-families")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedActivityFamily)))
            .andExpect(status().isOk());

        // Validate the ActivityFamily in the database
        List<ActivityFamily> activityFamilyList = activityFamilyRepository.findAll();
        assertThat(activityFamilyList).hasSize(databaseSizeBeforeUpdate);
        ActivityFamily testActivityFamily = activityFamilyList.get(activityFamilyList.size() - 1);

        // Validate the ActivityFamily in Elasticsearch
        verify(mockActivityFamilySearchRepository, times(1)).save(testActivityFamily);
    }

    @Test
    @Transactional
    public void updateNonExistingActivityFamily() throws Exception {
        int databaseSizeBeforeUpdate = activityFamilyRepository.findAll().size();

        // Create the ActivityFamily

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActivityFamilyMockMvc.perform(put("/api/activity-families")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityFamily)))
            .andExpect(status().isBadRequest());

        // Validate the ActivityFamily in the database
        List<ActivityFamily> activityFamilyList = activityFamilyRepository.findAll();
        assertThat(activityFamilyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActivityFamily in Elasticsearch
        verify(mockActivityFamilySearchRepository, times(0)).save(activityFamily);
    }

    @Test
    @Transactional
    public void deleteActivityFamily() throws Exception {
        // Initialize the database
        activityFamilyService.save(activityFamily);

        int databaseSizeBeforeDelete = activityFamilyRepository.findAll().size();

        // Get the activityFamily
        restActivityFamilyMockMvc.perform(delete("/api/activity-families/{id}", activityFamily.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ActivityFamily> activityFamilyList = activityFamilyRepository.findAll();
        assertThat(activityFamilyList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ActivityFamily in Elasticsearch
        verify(mockActivityFamilySearchRepository, times(1)).deleteById(activityFamily.getId());
    }

    @Test
    @Transactional
    public void searchActivityFamily() throws Exception {
        // Initialize the database
        activityFamilyService.save(activityFamily);
        when(mockActivityFamilySearchRepository.search(queryStringQuery("id:" + activityFamily.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(activityFamily), PageRequest.of(0, 1), 1));
        // Search the activityFamily
        restActivityFamilyMockMvc.perform(get("/api/_search/activity-families?query=id:" + activityFamily.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activityFamily.getId().intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ActivityFamily.class);
        ActivityFamily activityFamily1 = new ActivityFamily();
        activityFamily1.setId(1L);
        ActivityFamily activityFamily2 = new ActivityFamily();
        activityFamily2.setId(activityFamily1.getId());
        assertThat(activityFamily1).isEqualTo(activityFamily2);
        activityFamily2.setId(2L);
        assertThat(activityFamily1).isNotEqualTo(activityFamily2);
        activityFamily1.setId(null);
        assertThat(activityFamily1).isNotEqualTo(activityFamily2);
    }
}
