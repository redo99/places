package fr.celexio.peaks.web.rest;

import fr.celexio.peaks.PeaksApp;

import fr.celexio.peaks.domain.Place;
import fr.celexio.peaks.repository.PlaceRepository;
import fr.celexio.peaks.repository.search.PlaceSearchRepository;
import fr.celexio.peaks.service.PlaceService;
import fr.celexio.peaks.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import java.util.ArrayList;
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
 * Test class for the PlaceResource REST controller.
 *
 * @see PlaceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PeaksApp.class)
public class PlaceResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_VERIFIED = false;
    private static final Boolean UPDATED_VERIFIED = true;

    private static final Boolean DEFAULT_PUBLISHED = false;
    private static final Boolean UPDATED_PUBLISHED = true;

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_WEBSITE = "AAAAAAAAAA";
    private static final String UPDATED_WEBSITE = "BBBBBBBBBB";

    private static final String DEFAULT_GOOGLE_PLACE_ID = "AAAAAAAAAA";
    private static final String UPDATED_GOOGLE_PLACE_ID = "BBBBBBBBBB";

    @Autowired
    private PlaceRepository placeRepository;

    @Mock
    private PlaceRepository placeRepositoryMock;
    

    @Mock
    private PlaceService placeServiceMock;

    @Autowired
    private PlaceService placeService;

    /**
     * This repository is mocked in the fr.celexio.peaks.repository.search test package.
     *
     * @see fr.celexio.peaks.repository.search.PlaceSearchRepositoryMockConfiguration
     */
    @Autowired
    private PlaceSearchRepository mockPlaceSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPlaceMockMvc;

    private Place place;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PlaceResource placeResource = new PlaceResource(placeService);
        this.restPlaceMockMvc = MockMvcBuilders.standaloneSetup(placeResource)
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
    public static Place createEntity(EntityManager em) {
        Place place = new Place()
            .name(DEFAULT_NAME)
            .verified(DEFAULT_VERIFIED)
            .published(DEFAULT_PUBLISHED)
            .phone(DEFAULT_PHONE)
            .email(DEFAULT_EMAIL)
            .website(DEFAULT_WEBSITE)
            .googlePlaceId(DEFAULT_GOOGLE_PLACE_ID);
        return place;
    }

    @Before
    public void initTest() {
        place = createEntity(em);
    }

    @Test
    @Transactional
    public void createPlace() throws Exception {
        int databaseSizeBeforeCreate = placeRepository.findAll().size();

        // Create the Place
        restPlaceMockMvc.perform(post("/api/places")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(place)))
            .andExpect(status().isCreated());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeCreate + 1);
        Place testPlace = placeList.get(placeList.size() - 1);
        assertThat(testPlace.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPlace.isVerified()).isEqualTo(DEFAULT_VERIFIED);
        assertThat(testPlace.isPublished()).isEqualTo(DEFAULT_PUBLISHED);
        assertThat(testPlace.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testPlace.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testPlace.getWebsite()).isEqualTo(DEFAULT_WEBSITE);
        assertThat(testPlace.getGooglePlaceId()).isEqualTo(DEFAULT_GOOGLE_PLACE_ID);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(1)).save(testPlace);
    }

    @Test
    @Transactional
    public void createPlaceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = placeRepository.findAll().size();

        // Create the Place with an existing ID
        place.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlaceMockMvc.perform(post("/api/places")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(place)))
            .andExpect(status().isBadRequest());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(0)).save(place);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = placeRepository.findAll().size();
        // set the field null
        place.setName(null);

        // Create the Place, which fails.

        restPlaceMockMvc.perform(post("/api/places")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(place)))
            .andExpect(status().isBadRequest());

        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPlaces() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList
        restPlaceMockMvc.perform(get("/api/places?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(place.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].verified").value(hasItem(DEFAULT_VERIFIED.booleanValue())))
            .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED.booleanValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].website").value(hasItem(DEFAULT_WEBSITE.toString())))
            .andExpect(jsonPath("$.[*].googlePlaceId").value(hasItem(DEFAULT_GOOGLE_PLACE_ID.toString())));
    }
    
    public void getAllPlacesWithEagerRelationshipsIsEnabled() throws Exception {
        PlaceResource placeResource = new PlaceResource(placeServiceMock);
        when(placeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restPlaceMockMvc = MockMvcBuilders.standaloneSetup(placeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restPlaceMockMvc.perform(get("/api/places?eagerload=true"))
        .andExpect(status().isOk());

        verify(placeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    public void getAllPlacesWithEagerRelationshipsIsNotEnabled() throws Exception {
        PlaceResource placeResource = new PlaceResource(placeServiceMock);
            when(placeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restPlaceMockMvc = MockMvcBuilders.standaloneSetup(placeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restPlaceMockMvc.perform(get("/api/places?eagerload=true"))
        .andExpect(status().isOk());

            verify(placeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getPlace() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get the place
        restPlaceMockMvc.perform(get("/api/places/{id}", place.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(place.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.verified").value(DEFAULT_VERIFIED.booleanValue()))
            .andExpect(jsonPath("$.published").value(DEFAULT_PUBLISHED.booleanValue()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.website").value(DEFAULT_WEBSITE.toString()))
            .andExpect(jsonPath("$.googlePlaceId").value(DEFAULT_GOOGLE_PLACE_ID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPlace() throws Exception {
        // Get the place
        restPlaceMockMvc.perform(get("/api/places/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePlace() throws Exception {
        // Initialize the database
        placeService.save(place);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockPlaceSearchRepository);

        int databaseSizeBeforeUpdate = placeRepository.findAll().size();

        // Update the place
        Place updatedPlace = placeRepository.findById(place.getId()).get();
        // Disconnect from session so that the updates on updatedPlace are not directly saved in db
        em.detach(updatedPlace);
        updatedPlace
            .name(UPDATED_NAME)
            .verified(UPDATED_VERIFIED)
            .published(UPDATED_PUBLISHED)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL)
            .website(UPDATED_WEBSITE)
            .googlePlaceId(UPDATED_GOOGLE_PLACE_ID);

        restPlaceMockMvc.perform(put("/api/places")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPlace)))
            .andExpect(status().isOk());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate);
        Place testPlace = placeList.get(placeList.size() - 1);
        assertThat(testPlace.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlace.isVerified()).isEqualTo(UPDATED_VERIFIED);
        assertThat(testPlace.isPublished()).isEqualTo(UPDATED_PUBLISHED);
        assertThat(testPlace.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testPlace.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testPlace.getWebsite()).isEqualTo(UPDATED_WEBSITE);
        assertThat(testPlace.getGooglePlaceId()).isEqualTo(UPDATED_GOOGLE_PLACE_ID);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(1)).save(testPlace);
    }

    @Test
    @Transactional
    public void updateNonExistingPlace() throws Exception {
        int databaseSizeBeforeUpdate = placeRepository.findAll().size();

        // Create the Place

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlaceMockMvc.perform(put("/api/places")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(place)))
            .andExpect(status().isBadRequest());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(0)).save(place);
    }

    @Test
    @Transactional
    public void deletePlace() throws Exception {
        // Initialize the database
        placeService.save(place);

        int databaseSizeBeforeDelete = placeRepository.findAll().size();

        // Get the place
        restPlaceMockMvc.perform(delete("/api/places/{id}", place.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(1)).deleteById(place.getId());
    }

    @Test
    @Transactional
    public void searchPlace() throws Exception {
        // Initialize the database
        placeService.save(place);
        when(mockPlaceSearchRepository.search(queryStringQuery("id:" + place.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(place), PageRequest.of(0, 1), 1));
        // Search the place
        restPlaceMockMvc.perform(get("/api/_search/places?query=id:" + place.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(place.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].verified").value(hasItem(DEFAULT_VERIFIED.booleanValue())))
            .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED.booleanValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].website").value(hasItem(DEFAULT_WEBSITE.toString())))
            .andExpect(jsonPath("$.[*].googlePlaceId").value(hasItem(DEFAULT_GOOGLE_PLACE_ID.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Place.class);
        Place place1 = new Place();
        place1.setId(1L);
        Place place2 = new Place();
        place2.setId(place1.getId());
        assertThat(place1).isEqualTo(place2);
        place2.setId(2L);
        assertThat(place1).isNotEqualTo(place2);
        place1.setId(null);
        assertThat(place1).isNotEqualTo(place2);
    }
}
