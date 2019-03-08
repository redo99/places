package fr.celexio.peaks.web.rest;

import fr.celexio.peaks.PeaksApp;

import fr.celexio.peaks.domain.PlaceCategory;
import fr.celexio.peaks.repository.PlaceCategoryRepository;
import fr.celexio.peaks.repository.search.PlaceCategorySearchRepository;
import fr.celexio.peaks.service.PlaceCategoryService;
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
 * Test class for the PlaceCategoryResource REST controller.
 *
 * @see PlaceCategoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PeaksApp.class)
public class PlaceCategoryResourceIntTest {

    @Autowired
    private PlaceCategoryRepository placeCategoryRepository;
    
    @Autowired
    private PlaceCategoryService placeCategoryService;

    /**
     * This repository is mocked in the fr.celexio.peaks.repository.search test package.
     *
     * @see fr.celexio.peaks.repository.search.PlaceCategorySearchRepositoryMockConfiguration
     */
    @Autowired
    private PlaceCategorySearchRepository mockPlaceCategorySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPlaceCategoryMockMvc;

    private PlaceCategory placeCategory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PlaceCategoryResource placeCategoryResource = new PlaceCategoryResource(placeCategoryService);
        this.restPlaceCategoryMockMvc = MockMvcBuilders.standaloneSetup(placeCategoryResource)
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
    public static PlaceCategory createEntity(EntityManager em) {
        PlaceCategory placeCategory = new PlaceCategory();
        return placeCategory;
    }

    @Before
    public void initTest() {
        placeCategory = createEntity(em);
    }

    @Test
    @Transactional
    public void createPlaceCategory() throws Exception {
        int databaseSizeBeforeCreate = placeCategoryRepository.findAll().size();

        // Create the PlaceCategory
        restPlaceCategoryMockMvc.perform(post("/api/place-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeCategory)))
            .andExpect(status().isCreated());

        // Validate the PlaceCategory in the database
        List<PlaceCategory> placeCategoryList = placeCategoryRepository.findAll();
        assertThat(placeCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        PlaceCategory testPlaceCategory = placeCategoryList.get(placeCategoryList.size() - 1);

        // Validate the PlaceCategory in Elasticsearch
        verify(mockPlaceCategorySearchRepository, times(1)).save(testPlaceCategory);
    }

    @Test
    @Transactional
    public void createPlaceCategoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = placeCategoryRepository.findAll().size();

        // Create the PlaceCategory with an existing ID
        placeCategory.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlaceCategoryMockMvc.perform(post("/api/place-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeCategory)))
            .andExpect(status().isBadRequest());

        // Validate the PlaceCategory in the database
        List<PlaceCategory> placeCategoryList = placeCategoryRepository.findAll();
        assertThat(placeCategoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the PlaceCategory in Elasticsearch
        verify(mockPlaceCategorySearchRepository, times(0)).save(placeCategory);
    }

    @Test
    @Transactional
    public void getAllPlaceCategories() throws Exception {
        // Initialize the database
        placeCategoryRepository.saveAndFlush(placeCategory);

        // Get all the placeCategoryList
        restPlaceCategoryMockMvc.perform(get("/api/place-categories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(placeCategory.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getPlaceCategory() throws Exception {
        // Initialize the database
        placeCategoryRepository.saveAndFlush(placeCategory);

        // Get the placeCategory
        restPlaceCategoryMockMvc.perform(get("/api/place-categories/{id}", placeCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(placeCategory.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingPlaceCategory() throws Exception {
        // Get the placeCategory
        restPlaceCategoryMockMvc.perform(get("/api/place-categories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePlaceCategory() throws Exception {
        // Initialize the database
        placeCategoryService.save(placeCategory);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockPlaceCategorySearchRepository);

        int databaseSizeBeforeUpdate = placeCategoryRepository.findAll().size();

        // Update the placeCategory
        PlaceCategory updatedPlaceCategory = placeCategoryRepository.findById(placeCategory.getId()).get();
        // Disconnect from session so that the updates on updatedPlaceCategory are not directly saved in db
        em.detach(updatedPlaceCategory);

        restPlaceCategoryMockMvc.perform(put("/api/place-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPlaceCategory)))
            .andExpect(status().isOk());

        // Validate the PlaceCategory in the database
        List<PlaceCategory> placeCategoryList = placeCategoryRepository.findAll();
        assertThat(placeCategoryList).hasSize(databaseSizeBeforeUpdate);
        PlaceCategory testPlaceCategory = placeCategoryList.get(placeCategoryList.size() - 1);

        // Validate the PlaceCategory in Elasticsearch
        verify(mockPlaceCategorySearchRepository, times(1)).save(testPlaceCategory);
    }

    @Test
    @Transactional
    public void updateNonExistingPlaceCategory() throws Exception {
        int databaseSizeBeforeUpdate = placeCategoryRepository.findAll().size();

        // Create the PlaceCategory

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlaceCategoryMockMvc.perform(put("/api/place-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeCategory)))
            .andExpect(status().isBadRequest());

        // Validate the PlaceCategory in the database
        List<PlaceCategory> placeCategoryList = placeCategoryRepository.findAll();
        assertThat(placeCategoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PlaceCategory in Elasticsearch
        verify(mockPlaceCategorySearchRepository, times(0)).save(placeCategory);
    }

    @Test
    @Transactional
    public void deletePlaceCategory() throws Exception {
        // Initialize the database
        placeCategoryService.save(placeCategory);

        int databaseSizeBeforeDelete = placeCategoryRepository.findAll().size();

        // Get the placeCategory
        restPlaceCategoryMockMvc.perform(delete("/api/place-categories/{id}", placeCategory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<PlaceCategory> placeCategoryList = placeCategoryRepository.findAll();
        assertThat(placeCategoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PlaceCategory in Elasticsearch
        verify(mockPlaceCategorySearchRepository, times(1)).deleteById(placeCategory.getId());
    }

    @Test
    @Transactional
    public void searchPlaceCategory() throws Exception {
        // Initialize the database
        placeCategoryService.save(placeCategory);
        when(mockPlaceCategorySearchRepository.search(queryStringQuery("id:" + placeCategory.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(placeCategory), PageRequest.of(0, 1), 1));
        // Search the placeCategory
        restPlaceCategoryMockMvc.perform(get("/api/_search/place-categories?query=id:" + placeCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(placeCategory.getId().intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlaceCategory.class);
        PlaceCategory placeCategory1 = new PlaceCategory();
        placeCategory1.setId(1L);
        PlaceCategory placeCategory2 = new PlaceCategory();
        placeCategory2.setId(placeCategory1.getId());
        assertThat(placeCategory1).isEqualTo(placeCategory2);
        placeCategory2.setId(2L);
        assertThat(placeCategory1).isNotEqualTo(placeCategory2);
        placeCategory1.setId(null);
        assertThat(placeCategory1).isNotEqualTo(placeCategory2);
    }
}
