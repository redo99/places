package fr.celexio.peaks.web.rest;

import fr.celexio.peaks.PeaksApp;

import fr.celexio.peaks.domain.LabelTranslation;
import fr.celexio.peaks.repository.LabelTranslationRepository;
import fr.celexio.peaks.repository.search.LabelTranslationSearchRepository;
import fr.celexio.peaks.service.LabelTranslationService;
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
 * Test class for the LabelTranslationResource REST controller.
 *
 * @see LabelTranslationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PeaksApp.class)
public class LabelTranslationResourceIntTest {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_EN_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_EN_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_FR_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_FR_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_AR_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_AR_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_SP_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_SP_VALUE = "BBBBBBBBBB";

    @Autowired
    private LabelTranslationRepository labelTranslationRepository;
    
    @Autowired
    private LabelTranslationService labelTranslationService;

    /**
     * This repository is mocked in the fr.celexio.peaks.repository.search test package.
     *
     * @see fr.celexio.peaks.repository.search.LabelTranslationSearchRepositoryMockConfiguration
     */
    @Autowired
    private LabelTranslationSearchRepository mockLabelTranslationSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restLabelTranslationMockMvc;

    private LabelTranslation labelTranslation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LabelTranslationResource labelTranslationResource = new LabelTranslationResource(labelTranslationService);
        this.restLabelTranslationMockMvc = MockMvcBuilders.standaloneSetup(labelTranslationResource)
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
    public static LabelTranslation createEntity(EntityManager em) {
        LabelTranslation labelTranslation = new LabelTranslation()
            .key(DEFAULT_KEY)
            .enValue(DEFAULT_EN_VALUE)
            .frValue(DEFAULT_FR_VALUE)
            .arValue(DEFAULT_AR_VALUE)
            .spValue(DEFAULT_SP_VALUE);
        return labelTranslation;
    }

    @Before
    public void initTest() {
        labelTranslation = createEntity(em);
    }

    @Test
    @Transactional
    public void createLabelTranslation() throws Exception {
        int databaseSizeBeforeCreate = labelTranslationRepository.findAll().size();

        // Create the LabelTranslation
        restLabelTranslationMockMvc.perform(post("/api/label-translations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(labelTranslation)))
            .andExpect(status().isCreated());

        // Validate the LabelTranslation in the database
        List<LabelTranslation> labelTranslationList = labelTranslationRepository.findAll();
        assertThat(labelTranslationList).hasSize(databaseSizeBeforeCreate + 1);
        LabelTranslation testLabelTranslation = labelTranslationList.get(labelTranslationList.size() - 1);
        assertThat(testLabelTranslation.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testLabelTranslation.getEnValue()).isEqualTo(DEFAULT_EN_VALUE);
        assertThat(testLabelTranslation.getFrValue()).isEqualTo(DEFAULT_FR_VALUE);
        assertThat(testLabelTranslation.getArValue()).isEqualTo(DEFAULT_AR_VALUE);
        assertThat(testLabelTranslation.getSpValue()).isEqualTo(DEFAULT_SP_VALUE);

        // Validate the LabelTranslation in Elasticsearch
        verify(mockLabelTranslationSearchRepository, times(1)).save(testLabelTranslation);
    }

    @Test
    @Transactional
    public void createLabelTranslationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = labelTranslationRepository.findAll().size();

        // Create the LabelTranslation with an existing ID
        labelTranslation.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLabelTranslationMockMvc.perform(post("/api/label-translations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(labelTranslation)))
            .andExpect(status().isBadRequest());

        // Validate the LabelTranslation in the database
        List<LabelTranslation> labelTranslationList = labelTranslationRepository.findAll();
        assertThat(labelTranslationList).hasSize(databaseSizeBeforeCreate);

        // Validate the LabelTranslation in Elasticsearch
        verify(mockLabelTranslationSearchRepository, times(0)).save(labelTranslation);
    }

    @Test
    @Transactional
    public void checkKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = labelTranslationRepository.findAll().size();
        // set the field null
        labelTranslation.setKey(null);

        // Create the LabelTranslation, which fails.

        restLabelTranslationMockMvc.perform(post("/api/label-translations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(labelTranslation)))
            .andExpect(status().isBadRequest());

        List<LabelTranslation> labelTranslationList = labelTranslationRepository.findAll();
        assertThat(labelTranslationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLabelTranslations() throws Exception {
        // Initialize the database
        labelTranslationRepository.saveAndFlush(labelTranslation);

        // Get all the labelTranslationList
        restLabelTranslationMockMvc.perform(get("/api/label-translations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(labelTranslation.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY.toString())))
            .andExpect(jsonPath("$.[*].enValue").value(hasItem(DEFAULT_EN_VALUE.toString())))
            .andExpect(jsonPath("$.[*].frValue").value(hasItem(DEFAULT_FR_VALUE.toString())))
            .andExpect(jsonPath("$.[*].arValue").value(hasItem(DEFAULT_AR_VALUE.toString())))
            .andExpect(jsonPath("$.[*].spValue").value(hasItem(DEFAULT_SP_VALUE.toString())));
    }
    
    @Test
    @Transactional
    public void getLabelTranslation() throws Exception {
        // Initialize the database
        labelTranslationRepository.saveAndFlush(labelTranslation);

        // Get the labelTranslation
        restLabelTranslationMockMvc.perform(get("/api/label-translations/{id}", labelTranslation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(labelTranslation.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY.toString()))
            .andExpect(jsonPath("$.enValue").value(DEFAULT_EN_VALUE.toString()))
            .andExpect(jsonPath("$.frValue").value(DEFAULT_FR_VALUE.toString()))
            .andExpect(jsonPath("$.arValue").value(DEFAULT_AR_VALUE.toString()))
            .andExpect(jsonPath("$.spValue").value(DEFAULT_SP_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLabelTranslation() throws Exception {
        // Get the labelTranslation
        restLabelTranslationMockMvc.perform(get("/api/label-translations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLabelTranslation() throws Exception {
        // Initialize the database
        labelTranslationService.save(labelTranslation);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockLabelTranslationSearchRepository);

        int databaseSizeBeforeUpdate = labelTranslationRepository.findAll().size();

        // Update the labelTranslation
        LabelTranslation updatedLabelTranslation = labelTranslationRepository.findById(labelTranslation.getId()).get();
        // Disconnect from session so that the updates on updatedLabelTranslation are not directly saved in db
        em.detach(updatedLabelTranslation);
        updatedLabelTranslation
            .key(UPDATED_KEY)
            .enValue(UPDATED_EN_VALUE)
            .frValue(UPDATED_FR_VALUE)
            .arValue(UPDATED_AR_VALUE)
            .spValue(UPDATED_SP_VALUE);

        restLabelTranslationMockMvc.perform(put("/api/label-translations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLabelTranslation)))
            .andExpect(status().isOk());

        // Validate the LabelTranslation in the database
        List<LabelTranslation> labelTranslationList = labelTranslationRepository.findAll();
        assertThat(labelTranslationList).hasSize(databaseSizeBeforeUpdate);
        LabelTranslation testLabelTranslation = labelTranslationList.get(labelTranslationList.size() - 1);
        assertThat(testLabelTranslation.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testLabelTranslation.getEnValue()).isEqualTo(UPDATED_EN_VALUE);
        assertThat(testLabelTranslation.getFrValue()).isEqualTo(UPDATED_FR_VALUE);
        assertThat(testLabelTranslation.getArValue()).isEqualTo(UPDATED_AR_VALUE);
        assertThat(testLabelTranslation.getSpValue()).isEqualTo(UPDATED_SP_VALUE);

        // Validate the LabelTranslation in Elasticsearch
        verify(mockLabelTranslationSearchRepository, times(1)).save(testLabelTranslation);
    }

    @Test
    @Transactional
    public void updateNonExistingLabelTranslation() throws Exception {
        int databaseSizeBeforeUpdate = labelTranslationRepository.findAll().size();

        // Create the LabelTranslation

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLabelTranslationMockMvc.perform(put("/api/label-translations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(labelTranslation)))
            .andExpect(status().isBadRequest());

        // Validate the LabelTranslation in the database
        List<LabelTranslation> labelTranslationList = labelTranslationRepository.findAll();
        assertThat(labelTranslationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the LabelTranslation in Elasticsearch
        verify(mockLabelTranslationSearchRepository, times(0)).save(labelTranslation);
    }

    @Test
    @Transactional
    public void deleteLabelTranslation() throws Exception {
        // Initialize the database
        labelTranslationService.save(labelTranslation);

        int databaseSizeBeforeDelete = labelTranslationRepository.findAll().size();

        // Get the labelTranslation
        restLabelTranslationMockMvc.perform(delete("/api/label-translations/{id}", labelTranslation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<LabelTranslation> labelTranslationList = labelTranslationRepository.findAll();
        assertThat(labelTranslationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the LabelTranslation in Elasticsearch
        verify(mockLabelTranslationSearchRepository, times(1)).deleteById(labelTranslation.getId());
    }

    @Test
    @Transactional
    public void searchLabelTranslation() throws Exception {
        // Initialize the database
        labelTranslationService.save(labelTranslation);
        when(mockLabelTranslationSearchRepository.search(queryStringQuery("id:" + labelTranslation.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(labelTranslation), PageRequest.of(0, 1), 1));
        // Search the labelTranslation
        restLabelTranslationMockMvc.perform(get("/api/_search/label-translations?query=id:" + labelTranslation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(labelTranslation.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY.toString())))
            .andExpect(jsonPath("$.[*].enValue").value(hasItem(DEFAULT_EN_VALUE.toString())))
            .andExpect(jsonPath("$.[*].frValue").value(hasItem(DEFAULT_FR_VALUE.toString())))
            .andExpect(jsonPath("$.[*].arValue").value(hasItem(DEFAULT_AR_VALUE.toString())))
            .andExpect(jsonPath("$.[*].spValue").value(hasItem(DEFAULT_SP_VALUE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LabelTranslation.class);
        LabelTranslation labelTranslation1 = new LabelTranslation();
        labelTranslation1.setId(1L);
        LabelTranslation labelTranslation2 = new LabelTranslation();
        labelTranslation2.setId(labelTranslation1.getId());
        assertThat(labelTranslation1).isEqualTo(labelTranslation2);
        labelTranslation2.setId(2L);
        assertThat(labelTranslation1).isNotEqualTo(labelTranslation2);
        labelTranslation1.setId(null);
        assertThat(labelTranslation1).isNotEqualTo(labelTranslation2);
    }
}
