package fr.celexio.peaks.web.rest;

import fr.celexio.peaks.PeaksApp;

import fr.celexio.peaks.domain.PlaceManagement;
import fr.celexio.peaks.repository.PlaceManagementRepository;
import fr.celexio.peaks.repository.search.PlaceManagementSearchRepository;
import fr.celexio.peaks.service.PlaceManagementService;
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
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Test class for the PlaceManagementResource REST controller.
 *
 * @see PlaceManagementResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PeaksApp.class)
public class PlaceManagementResourceIntTest {

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private PlaceManagementRepository placeManagementRepository;
    
    @Autowired
    private PlaceManagementService placeManagementService;

    /**
     * This repository is mocked in the fr.celexio.peaks.repository.search test package.
     *
     * @see fr.celexio.peaks.repository.search.PlaceManagementSearchRepositoryMockConfiguration
     */
    @Autowired
    private PlaceManagementSearchRepository mockPlaceManagementSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPlaceManagementMockMvc;

    private PlaceManagement placeManagement;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PlaceManagementResource placeManagementResource = new PlaceManagementResource(placeManagementService);
        this.restPlaceManagementMockMvc = MockMvcBuilders.standaloneSetup(placeManagementResource)
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
    public static PlaceManagement createEntity(EntityManager em) {
        PlaceManagement placeManagement = new PlaceManagement()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
        return placeManagement;
    }

    @Before
    public void initTest() {
        placeManagement = createEntity(em);
    }

    @Test
    @Transactional
    public void createPlaceManagement() throws Exception {
        int databaseSizeBeforeCreate = placeManagementRepository.findAll().size();

        // Create the PlaceManagement
        restPlaceManagementMockMvc.perform(post("/api/place-managements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeManagement)))
            .andExpect(status().isCreated());

        // Validate the PlaceManagement in the database
        List<PlaceManagement> placeManagementList = placeManagementRepository.findAll();
        assertThat(placeManagementList).hasSize(databaseSizeBeforeCreate + 1);
        PlaceManagement testPlaceManagement = placeManagementList.get(placeManagementList.size() - 1);
        assertThat(testPlaceManagement.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testPlaceManagement.getEndDate()).isEqualTo(DEFAULT_END_DATE);

        // Validate the PlaceManagement in Elasticsearch
        verify(mockPlaceManagementSearchRepository, times(1)).save(testPlaceManagement);
    }

    @Test
    @Transactional
    public void createPlaceManagementWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = placeManagementRepository.findAll().size();

        // Create the PlaceManagement with an existing ID
        placeManagement.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlaceManagementMockMvc.perform(post("/api/place-managements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeManagement)))
            .andExpect(status().isBadRequest());

        // Validate the PlaceManagement in the database
        List<PlaceManagement> placeManagementList = placeManagementRepository.findAll();
        assertThat(placeManagementList).hasSize(databaseSizeBeforeCreate);

        // Validate the PlaceManagement in Elasticsearch
        verify(mockPlaceManagementSearchRepository, times(0)).save(placeManagement);
    }

    @Test
    @Transactional
    public void getAllPlaceManagements() throws Exception {
        // Initialize the database
        placeManagementRepository.saveAndFlush(placeManagement);

        // Get all the placeManagementList
        restPlaceManagementMockMvc.perform(get("/api/place-managements?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(placeManagement.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getPlaceManagement() throws Exception {
        // Initialize the database
        placeManagementRepository.saveAndFlush(placeManagement);

        // Get the placeManagement
        restPlaceManagementMockMvc.perform(get("/api/place-managements/{id}", placeManagement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(placeManagement.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPlaceManagement() throws Exception {
        // Get the placeManagement
        restPlaceManagementMockMvc.perform(get("/api/place-managements/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePlaceManagement() throws Exception {
        // Initialize the database
        placeManagementService.save(placeManagement);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockPlaceManagementSearchRepository);

        int databaseSizeBeforeUpdate = placeManagementRepository.findAll().size();

        // Update the placeManagement
        PlaceManagement updatedPlaceManagement = placeManagementRepository.findById(placeManagement.getId()).get();
        // Disconnect from session so that the updates on updatedPlaceManagement are not directly saved in db
        em.detach(updatedPlaceManagement);
        updatedPlaceManagement
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        restPlaceManagementMockMvc.perform(put("/api/place-managements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPlaceManagement)))
            .andExpect(status().isOk());

        // Validate the PlaceManagement in the database
        List<PlaceManagement> placeManagementList = placeManagementRepository.findAll();
        assertThat(placeManagementList).hasSize(databaseSizeBeforeUpdate);
        PlaceManagement testPlaceManagement = placeManagementList.get(placeManagementList.size() - 1);
        assertThat(testPlaceManagement.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testPlaceManagement.getEndDate()).isEqualTo(UPDATED_END_DATE);

        // Validate the PlaceManagement in Elasticsearch
        verify(mockPlaceManagementSearchRepository, times(1)).save(testPlaceManagement);
    }

    @Test
    @Transactional
    public void updateNonExistingPlaceManagement() throws Exception {
        int databaseSizeBeforeUpdate = placeManagementRepository.findAll().size();

        // Create the PlaceManagement

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlaceManagementMockMvc.perform(put("/api/place-managements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeManagement)))
            .andExpect(status().isBadRequest());

        // Validate the PlaceManagement in the database
        List<PlaceManagement> placeManagementList = placeManagementRepository.findAll();
        assertThat(placeManagementList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PlaceManagement in Elasticsearch
        verify(mockPlaceManagementSearchRepository, times(0)).save(placeManagement);
    }

    @Test
    @Transactional
    public void deletePlaceManagement() throws Exception {
        // Initialize the database
        placeManagementService.save(placeManagement);

        int databaseSizeBeforeDelete = placeManagementRepository.findAll().size();

        // Get the placeManagement
        restPlaceManagementMockMvc.perform(delete("/api/place-managements/{id}", placeManagement.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<PlaceManagement> placeManagementList = placeManagementRepository.findAll();
        assertThat(placeManagementList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PlaceManagement in Elasticsearch
        verify(mockPlaceManagementSearchRepository, times(1)).deleteById(placeManagement.getId());
    }

    @Test
    @Transactional
    public void searchPlaceManagement() throws Exception {
        // Initialize the database
        placeManagementService.save(placeManagement);
        when(mockPlaceManagementSearchRepository.search(queryStringQuery("id:" + placeManagement.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(placeManagement), PageRequest.of(0, 1), 1));
        // Search the placeManagement
        restPlaceManagementMockMvc.perform(get("/api/_search/place-managements?query=id:" + placeManagement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(placeManagement.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlaceManagement.class);
        PlaceManagement placeManagement1 = new PlaceManagement();
        placeManagement1.setId(1L);
        PlaceManagement placeManagement2 = new PlaceManagement();
        placeManagement2.setId(placeManagement1.getId());
        assertThat(placeManagement1).isEqualTo(placeManagement2);
        placeManagement2.setId(2L);
        assertThat(placeManagement1).isNotEqualTo(placeManagement2);
        placeManagement1.setId(null);
        assertThat(placeManagement1).isNotEqualTo(placeManagement2);
    }
}
