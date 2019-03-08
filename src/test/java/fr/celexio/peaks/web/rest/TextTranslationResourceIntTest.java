package fr.celexio.peaks.web.rest;

import fr.celexio.peaks.PeaksApp;

import fr.celexio.peaks.domain.TextTranslation;
import fr.celexio.peaks.repository.TextTranslationRepository;
import fr.celexio.peaks.repository.search.TextTranslationSearchRepository;
import fr.celexio.peaks.service.TextTranslationService;
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
import org.springframework.util.Base64Utils;

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
 * Test class for the TextTranslationResource REST controller.
 *
 * @see TextTranslationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PeaksApp.class)
public class TextTranslationResourceIntTest {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_EN_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_EN_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_FR_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_FR_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_AR_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_AR_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_ES_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_ES_VALUE = "BBBBBBBBBB";

    @Autowired
    private TextTranslationRepository textTranslationRepository;
    
    @Autowired
    private TextTranslationService textTranslationService;

    /**
     * This repository is mocked in the fr.celexio.peaks.repository.search test package.
     *
     * @see fr.celexio.peaks.repository.search.TextTranslationSearchRepositoryMockConfiguration
     */
    @Autowired
    private TextTranslationSearchRepository mockTextTranslationSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTextTranslationMockMvc;

    private TextTranslation textTranslation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TextTranslationResource textTranslationResource = new TextTranslationResource(textTranslationService);
        this.restTextTranslationMockMvc = MockMvcBuilders.standaloneSetup(textTranslationResource)
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
    public static TextTranslation createEntity(EntityManager em) {
        TextTranslation textTranslation = new TextTranslation()
            .key(DEFAULT_KEY)
            .enValue(DEFAULT_EN_VALUE)
            .frValue(DEFAULT_FR_VALUE)
            .arValue(DEFAULT_AR_VALUE)
            .esValue(DEFAULT_ES_VALUE);
        return textTranslation;
    }

    @Before
    public void initTest() {
        textTranslation = createEntity(em);
    }

    @Test
    @Transactional
    public void createTextTranslation() throws Exception {
        int databaseSizeBeforeCreate = textTranslationRepository.findAll().size();

        // Create the TextTranslation
        restTextTranslationMockMvc.perform(post("/api/text-translations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(textTranslation)))
            .andExpect(status().isCreated());

        // Validate the TextTranslation in the database
        List<TextTranslation> textTranslationList = textTranslationRepository.findAll();
        assertThat(textTranslationList).hasSize(databaseSizeBeforeCreate + 1);
        TextTranslation testTextTranslation = textTranslationList.get(textTranslationList.size() - 1);
        assertThat(testTextTranslation.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testTextTranslation.getEnValue()).isEqualTo(DEFAULT_EN_VALUE);
        assertThat(testTextTranslation.getFrValue()).isEqualTo(DEFAULT_FR_VALUE);
        assertThat(testTextTranslation.getArValue()).isEqualTo(DEFAULT_AR_VALUE);
        assertThat(testTextTranslation.getEsValue()).isEqualTo(DEFAULT_ES_VALUE);

        // Validate the TextTranslation in Elasticsearch
        verify(mockTextTranslationSearchRepository, times(1)).save(testTextTranslation);
    }

    @Test
    @Transactional
    public void createTextTranslationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = textTranslationRepository.findAll().size();

        // Create the TextTranslation with an existing ID
        textTranslation.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTextTranslationMockMvc.perform(post("/api/text-translations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(textTranslation)))
            .andExpect(status().isBadRequest());

        // Validate the TextTranslation in the database
        List<TextTranslation> textTranslationList = textTranslationRepository.findAll();
        assertThat(textTranslationList).hasSize(databaseSizeBeforeCreate);

        // Validate the TextTranslation in Elasticsearch
        verify(mockTextTranslationSearchRepository, times(0)).save(textTranslation);
    }

    @Test
    @Transactional
    public void checkKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = textTranslationRepository.findAll().size();
        // set the field null
        textTranslation.setKey(null);

        // Create the TextTranslation, which fails.

        restTextTranslationMockMvc.perform(post("/api/text-translations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(textTranslation)))
            .andExpect(status().isBadRequest());

        List<TextTranslation> textTranslationList = textTranslationRepository.findAll();
        assertThat(textTranslationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTextTranslations() throws Exception {
        // Initialize the database
        textTranslationRepository.saveAndFlush(textTranslation);

        // Get all the textTranslationList
        restTextTranslationMockMvc.perform(get("/api/text-translations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(textTranslation.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY.toString())))
            .andExpect(jsonPath("$.[*].enValue").value(hasItem(DEFAULT_EN_VALUE.toString())))
            .andExpect(jsonPath("$.[*].frValue").value(hasItem(DEFAULT_FR_VALUE.toString())))
            .andExpect(jsonPath("$.[*].arValue").value(hasItem(DEFAULT_AR_VALUE.toString())))
            .andExpect(jsonPath("$.[*].esValue").value(hasItem(DEFAULT_ES_VALUE.toString())));
    }
    
    @Test
    @Transactional
    public void getTextTranslation() throws Exception {
        // Initialize the database
        textTranslationRepository.saveAndFlush(textTranslation);

        // Get the textTranslation
        restTextTranslationMockMvc.perform(get("/api/text-translations/{id}", textTranslation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(textTranslation.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY.toString()))
            .andExpect(jsonPath("$.enValue").value(DEFAULT_EN_VALUE.toString()))
            .andExpect(jsonPath("$.frValue").value(DEFAULT_FR_VALUE.toString()))
            .andExpect(jsonPath("$.arValue").value(DEFAULT_AR_VALUE.toString()))
            .andExpect(jsonPath("$.esValue").value(DEFAULT_ES_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTextTranslation() throws Exception {
        // Get the textTranslation
        restTextTranslationMockMvc.perform(get("/api/text-translations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTextTranslation() throws Exception {
        // Initialize the database
        textTranslationService.save(textTranslation);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockTextTranslationSearchRepository);

        int databaseSizeBeforeUpdate = textTranslationRepository.findAll().size();

        // Update the textTranslation
        TextTranslation updatedTextTranslation = textTranslationRepository.findById(textTranslation.getId()).get();
        // Disconnect from session so that the updates on updatedTextTranslation are not directly saved in db
        em.detach(updatedTextTranslation);
        updatedTextTranslation
            .key(UPDATED_KEY)
            .enValue(UPDATED_EN_VALUE)
            .frValue(UPDATED_FR_VALUE)
            .arValue(UPDATED_AR_VALUE)
            .esValue(UPDATED_ES_VALUE);

        restTextTranslationMockMvc.perform(put("/api/text-translations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTextTranslation)))
            .andExpect(status().isOk());

        // Validate the TextTranslation in the database
        List<TextTranslation> textTranslationList = textTranslationRepository.findAll();
        assertThat(textTranslationList).hasSize(databaseSizeBeforeUpdate);
        TextTranslation testTextTranslation = textTranslationList.get(textTranslationList.size() - 1);
        assertThat(testTextTranslation.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testTextTranslation.getEnValue()).isEqualTo(UPDATED_EN_VALUE);
        assertThat(testTextTranslation.getFrValue()).isEqualTo(UPDATED_FR_VALUE);
        assertThat(testTextTranslation.getArValue()).isEqualTo(UPDATED_AR_VALUE);
        assertThat(testTextTranslation.getEsValue()).isEqualTo(UPDATED_ES_VALUE);

        // Validate the TextTranslation in Elasticsearch
        verify(mockTextTranslationSearchRepository, times(1)).save(testTextTranslation);
    }

    @Test
    @Transactional
    public void updateNonExistingTextTranslation() throws Exception {
        int databaseSizeBeforeUpdate = textTranslationRepository.findAll().size();

        // Create the TextTranslation

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTextTranslationMockMvc.perform(put("/api/text-translations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(textTranslation)))
            .andExpect(status().isBadRequest());

        // Validate the TextTranslation in the database
        List<TextTranslation> textTranslationList = textTranslationRepository.findAll();
        assertThat(textTranslationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TextTranslation in Elasticsearch
        verify(mockTextTranslationSearchRepository, times(0)).save(textTranslation);
    }

    @Test
    @Transactional
    public void deleteTextTranslation() throws Exception {
        // Initialize the database
        textTranslationService.save(textTranslation);

        int databaseSizeBeforeDelete = textTranslationRepository.findAll().size();

        // Get the textTranslation
        restTextTranslationMockMvc.perform(delete("/api/text-translations/{id}", textTranslation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<TextTranslation> textTranslationList = textTranslationRepository.findAll();
        assertThat(textTranslationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the TextTranslation in Elasticsearch
        verify(mockTextTranslationSearchRepository, times(1)).deleteById(textTranslation.getId());
    }

    @Test
    @Transactional
    public void searchTextTranslation() throws Exception {
        // Initialize the database
        textTranslationService.save(textTranslation);
        when(mockTextTranslationSearchRepository.search(queryStringQuery("id:" + textTranslation.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(textTranslation), PageRequest.of(0, 1), 1));
        // Search the textTranslation
        restTextTranslationMockMvc.perform(get("/api/_search/text-translations?query=id:" + textTranslation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(textTranslation.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY.toString())))
            .andExpect(jsonPath("$.[*].enValue").value(hasItem(DEFAULT_EN_VALUE.toString())))
            .andExpect(jsonPath("$.[*].frValue").value(hasItem(DEFAULT_FR_VALUE.toString())))
            .andExpect(jsonPath("$.[*].arValue").value(hasItem(DEFAULT_AR_VALUE.toString())))
            .andExpect(jsonPath("$.[*].esValue").value(hasItem(DEFAULT_ES_VALUE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TextTranslation.class);
        TextTranslation textTranslation1 = new TextTranslation();
        textTranslation1.setId(1L);
        TextTranslation textTranslation2 = new TextTranslation();
        textTranslation2.setId(textTranslation1.getId());
        assertThat(textTranslation1).isEqualTo(textTranslation2);
        textTranslation2.setId(2L);
        assertThat(textTranslation1).isNotEqualTo(textTranslation2);
        textTranslation1.setId(null);
        assertThat(textTranslation1).isNotEqualTo(textTranslation2);
    }
}
