package fr.celexio.peaks.web.rest;

import fr.celexio.peaks.PeaksApp;

import fr.celexio.peaks.domain.PlaceReview;
import fr.celexio.peaks.repository.PlaceReviewRepository;
import fr.celexio.peaks.repository.search.PlaceReviewSearchRepository;
import fr.celexio.peaks.service.PlaceReviewService;
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
 * Test class for the PlaceReviewResource REST controller.
 *
 * @see PlaceReviewResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PeaksApp.class)
public class PlaceReviewResourceIntTest {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Integer DEFAULT_SCORE = 5;
    private static final Integer UPDATED_SCORE = 4;

    @Autowired
    private PlaceReviewRepository placeReviewRepository;
    
    @Autowired
    private PlaceReviewService placeReviewService;

    /**
     * This repository is mocked in the fr.celexio.peaks.repository.search test package.
     *
     * @see fr.celexio.peaks.repository.search.PlaceReviewSearchRepositoryMockConfiguration
     */
    @Autowired
    private PlaceReviewSearchRepository mockPlaceReviewSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPlaceReviewMockMvc;

    private PlaceReview placeReview;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PlaceReviewResource placeReviewResource = new PlaceReviewResource(placeReviewService);
        this.restPlaceReviewMockMvc = MockMvcBuilders.standaloneSetup(placeReviewResource)
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
    public static PlaceReview createEntity(EntityManager em) {
        PlaceReview placeReview = new PlaceReview()
            .content(DEFAULT_CONTENT)
            .score(DEFAULT_SCORE);
        return placeReview;
    }

    @Before
    public void initTest() {
        placeReview = createEntity(em);
    }

    @Test
    @Transactional
    public void createPlaceReview() throws Exception {
        int databaseSizeBeforeCreate = placeReviewRepository.findAll().size();

        // Create the PlaceReview
        restPlaceReviewMockMvc.perform(post("/api/place-reviews")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeReview)))
            .andExpect(status().isCreated());

        // Validate the PlaceReview in the database
        List<PlaceReview> placeReviewList = placeReviewRepository.findAll();
        assertThat(placeReviewList).hasSize(databaseSizeBeforeCreate + 1);
        PlaceReview testPlaceReview = placeReviewList.get(placeReviewList.size() - 1);
        assertThat(testPlaceReview.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testPlaceReview.getScore()).isEqualTo(DEFAULT_SCORE);

        // Validate the PlaceReview in Elasticsearch
        verify(mockPlaceReviewSearchRepository, times(1)).save(testPlaceReview);
    }

    @Test
    @Transactional
    public void createPlaceReviewWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = placeReviewRepository.findAll().size();

        // Create the PlaceReview with an existing ID
        placeReview.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlaceReviewMockMvc.perform(post("/api/place-reviews")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeReview)))
            .andExpect(status().isBadRequest());

        // Validate the PlaceReview in the database
        List<PlaceReview> placeReviewList = placeReviewRepository.findAll();
        assertThat(placeReviewList).hasSize(databaseSizeBeforeCreate);

        // Validate the PlaceReview in Elasticsearch
        verify(mockPlaceReviewSearchRepository, times(0)).save(placeReview);
    }

    @Test
    @Transactional
    public void checkScoreIsRequired() throws Exception {
        int databaseSizeBeforeTest = placeReviewRepository.findAll().size();
        // set the field null
        placeReview.setScore(null);

        // Create the PlaceReview, which fails.

        restPlaceReviewMockMvc.perform(post("/api/place-reviews")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeReview)))
            .andExpect(status().isBadRequest());

        List<PlaceReview> placeReviewList = placeReviewRepository.findAll();
        assertThat(placeReviewList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPlaceReviews() throws Exception {
        // Initialize the database
        placeReviewRepository.saveAndFlush(placeReview);

        // Get all the placeReviewList
        restPlaceReviewMockMvc.perform(get("/api/place-reviews?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(placeReview.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)));
    }
    
    @Test
    @Transactional
    public void getPlaceReview() throws Exception {
        // Initialize the database
        placeReviewRepository.saveAndFlush(placeReview);

        // Get the placeReview
        restPlaceReviewMockMvc.perform(get("/api/place-reviews/{id}", placeReview.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(placeReview.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.score").value(DEFAULT_SCORE));
    }

    @Test
    @Transactional
    public void getNonExistingPlaceReview() throws Exception {
        // Get the placeReview
        restPlaceReviewMockMvc.perform(get("/api/place-reviews/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePlaceReview() throws Exception {
        // Initialize the database
        placeReviewService.save(placeReview);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockPlaceReviewSearchRepository);

        int databaseSizeBeforeUpdate = placeReviewRepository.findAll().size();

        // Update the placeReview
        PlaceReview updatedPlaceReview = placeReviewRepository.findById(placeReview.getId()).get();
        // Disconnect from session so that the updates on updatedPlaceReview are not directly saved in db
        em.detach(updatedPlaceReview);
        updatedPlaceReview
            .content(UPDATED_CONTENT)
            .score(UPDATED_SCORE);

        restPlaceReviewMockMvc.perform(put("/api/place-reviews")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPlaceReview)))
            .andExpect(status().isOk());

        // Validate the PlaceReview in the database
        List<PlaceReview> placeReviewList = placeReviewRepository.findAll();
        assertThat(placeReviewList).hasSize(databaseSizeBeforeUpdate);
        PlaceReview testPlaceReview = placeReviewList.get(placeReviewList.size() - 1);
        assertThat(testPlaceReview.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testPlaceReview.getScore()).isEqualTo(UPDATED_SCORE);

        // Validate the PlaceReview in Elasticsearch
        verify(mockPlaceReviewSearchRepository, times(1)).save(testPlaceReview);
    }

    @Test
    @Transactional
    public void updateNonExistingPlaceReview() throws Exception {
        int databaseSizeBeforeUpdate = placeReviewRepository.findAll().size();

        // Create the PlaceReview

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlaceReviewMockMvc.perform(put("/api/place-reviews")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeReview)))
            .andExpect(status().isBadRequest());

        // Validate the PlaceReview in the database
        List<PlaceReview> placeReviewList = placeReviewRepository.findAll();
        assertThat(placeReviewList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PlaceReview in Elasticsearch
        verify(mockPlaceReviewSearchRepository, times(0)).save(placeReview);
    }

    @Test
    @Transactional
    public void deletePlaceReview() throws Exception {
        // Initialize the database
        placeReviewService.save(placeReview);

        int databaseSizeBeforeDelete = placeReviewRepository.findAll().size();

        // Get the placeReview
        restPlaceReviewMockMvc.perform(delete("/api/place-reviews/{id}", placeReview.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<PlaceReview> placeReviewList = placeReviewRepository.findAll();
        assertThat(placeReviewList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PlaceReview in Elasticsearch
        verify(mockPlaceReviewSearchRepository, times(1)).deleteById(placeReview.getId());
    }

    @Test
    @Transactional
    public void searchPlaceReview() throws Exception {
        // Initialize the database
        placeReviewService.save(placeReview);
        when(mockPlaceReviewSearchRepository.search(queryStringQuery("id:" + placeReview.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(placeReview), PageRequest.of(0, 1), 1));
        // Search the placeReview
        restPlaceReviewMockMvc.perform(get("/api/_search/place-reviews?query=id:" + placeReview.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(placeReview.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlaceReview.class);
        PlaceReview placeReview1 = new PlaceReview();
        placeReview1.setId(1L);
        PlaceReview placeReview2 = new PlaceReview();
        placeReview2.setId(placeReview1.getId());
        assertThat(placeReview1).isEqualTo(placeReview2);
        placeReview2.setId(2L);
        assertThat(placeReview1).isNotEqualTo(placeReview2);
        placeReview1.setId(null);
        assertThat(placeReview1).isNotEqualTo(placeReview2);
    }
}
