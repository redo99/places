package fr.celexio.peaks.web.rest;

import fr.celexio.peaks.PeaksApp;

import fr.celexio.peaks.domain.Spot;
import fr.celexio.peaks.repository.SpotRepository;
import fr.celexio.peaks.repository.search.SpotSearchRepository;
import fr.celexio.peaks.service.SpotService;
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
 * Test class for the SpotResource REST controller.
 *
 * @see SpotResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PeaksApp.class)
public class SpotResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_VERIFIED = false;
    private static final Boolean UPDATED_VERIFIED = true;

    @Autowired
    private SpotRepository spotRepository;
    
    @Autowired
    private SpotService spotService;

    /**
     * This repository is mocked in the fr.celexio.peaks.repository.search test package.
     *
     * @see fr.celexio.peaks.repository.search.SpotSearchRepositoryMockConfiguration
     */
    @Autowired
    private SpotSearchRepository mockSpotSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSpotMockMvc;

    private Spot spot;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SpotResource spotResource = new SpotResource(spotService);
        this.restSpotMockMvc = MockMvcBuilders.standaloneSetup(spotResource)
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
    public static Spot createEntity(EntityManager em) {
        Spot spot = new Spot()
            .name(DEFAULT_NAME)
            .verified(DEFAULT_VERIFIED);
        return spot;
    }

    @Before
    public void initTest() {
        spot = createEntity(em);
    }

    @Test
    @Transactional
    public void createSpot() throws Exception {
        int databaseSizeBeforeCreate = spotRepository.findAll().size();

        // Create the Spot
        restSpotMockMvc.perform(post("/api/spots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(spot)))
            .andExpect(status().isCreated());

        // Validate the Spot in the database
        List<Spot> spotList = spotRepository.findAll();
        assertThat(spotList).hasSize(databaseSizeBeforeCreate + 1);
        Spot testSpot = spotList.get(spotList.size() - 1);
        assertThat(testSpot.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSpot.isVerified()).isEqualTo(DEFAULT_VERIFIED);

        // Validate the Spot in Elasticsearch
        verify(mockSpotSearchRepository, times(1)).save(testSpot);
    }

    @Test
    @Transactional
    public void createSpotWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = spotRepository.findAll().size();

        // Create the Spot with an existing ID
        spot.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSpotMockMvc.perform(post("/api/spots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(spot)))
            .andExpect(status().isBadRequest());

        // Validate the Spot in the database
        List<Spot> spotList = spotRepository.findAll();
        assertThat(spotList).hasSize(databaseSizeBeforeCreate);

        // Validate the Spot in Elasticsearch
        verify(mockSpotSearchRepository, times(0)).save(spot);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = spotRepository.findAll().size();
        // set the field null
        spot.setName(null);

        // Create the Spot, which fails.

        restSpotMockMvc.perform(post("/api/spots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(spot)))
            .andExpect(status().isBadRequest());

        List<Spot> spotList = spotRepository.findAll();
        assertThat(spotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSpots() throws Exception {
        // Initialize the database
        spotRepository.saveAndFlush(spot);

        // Get all the spotList
        restSpotMockMvc.perform(get("/api/spots?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spot.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].verified").value(hasItem(DEFAULT_VERIFIED.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getSpot() throws Exception {
        // Initialize the database
        spotRepository.saveAndFlush(spot);

        // Get the spot
        restSpotMockMvc.perform(get("/api/spots/{id}", spot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(spot.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.verified").value(DEFAULT_VERIFIED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingSpot() throws Exception {
        // Get the spot
        restSpotMockMvc.perform(get("/api/spots/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSpot() throws Exception {
        // Initialize the database
        spotService.save(spot);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockSpotSearchRepository);

        int databaseSizeBeforeUpdate = spotRepository.findAll().size();

        // Update the spot
        Spot updatedSpot = spotRepository.findById(spot.getId()).get();
        // Disconnect from session so that the updates on updatedSpot are not directly saved in db
        em.detach(updatedSpot);
        updatedSpot
            .name(UPDATED_NAME)
            .verified(UPDATED_VERIFIED);

        restSpotMockMvc.perform(put("/api/spots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSpot)))
            .andExpect(status().isOk());

        // Validate the Spot in the database
        List<Spot> spotList = spotRepository.findAll();
        assertThat(spotList).hasSize(databaseSizeBeforeUpdate);
        Spot testSpot = spotList.get(spotList.size() - 1);
        assertThat(testSpot.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSpot.isVerified()).isEqualTo(UPDATED_VERIFIED);

        // Validate the Spot in Elasticsearch
        verify(mockSpotSearchRepository, times(1)).save(testSpot);
    }

    @Test
    @Transactional
    public void updateNonExistingSpot() throws Exception {
        int databaseSizeBeforeUpdate = spotRepository.findAll().size();

        // Create the Spot

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpotMockMvc.perform(put("/api/spots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(spot)))
            .andExpect(status().isBadRequest());

        // Validate the Spot in the database
        List<Spot> spotList = spotRepository.findAll();
        assertThat(spotList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Spot in Elasticsearch
        verify(mockSpotSearchRepository, times(0)).save(spot);
    }

    @Test
    @Transactional
    public void deleteSpot() throws Exception {
        // Initialize the database
        spotService.save(spot);

        int databaseSizeBeforeDelete = spotRepository.findAll().size();

        // Get the spot
        restSpotMockMvc.perform(delete("/api/spots/{id}", spot.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Spot> spotList = spotRepository.findAll();
        assertThat(spotList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Spot in Elasticsearch
        verify(mockSpotSearchRepository, times(1)).deleteById(spot.getId());
    }

    @Test
    @Transactional
    public void searchSpot() throws Exception {
        // Initialize the database
        spotService.save(spot);
        when(mockSpotSearchRepository.search(queryStringQuery("id:" + spot.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(spot), PageRequest.of(0, 1), 1));
        // Search the spot
        restSpotMockMvc.perform(get("/api/_search/spots?query=id:" + spot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spot.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].verified").value(hasItem(DEFAULT_VERIFIED.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Spot.class);
        Spot spot1 = new Spot();
        spot1.setId(1L);
        Spot spot2 = new Spot();
        spot2.setId(spot1.getId());
        assertThat(spot1).isEqualTo(spot2);
        spot2.setId(2L);
        assertThat(spot1).isNotEqualTo(spot2);
        spot1.setId(null);
        assertThat(spot1).isNotEqualTo(spot2);
    }
}
