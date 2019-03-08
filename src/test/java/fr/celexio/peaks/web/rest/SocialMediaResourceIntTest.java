package fr.celexio.peaks.web.rest;

import fr.celexio.peaks.PeaksApp;

import fr.celexio.peaks.domain.SocialMedia;
import fr.celexio.peaks.repository.SocialMediaRepository;
import fr.celexio.peaks.repository.search.SocialMediaSearchRepository;
import fr.celexio.peaks.service.SocialMediaService;
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
 * Test class for the SocialMediaResource REST controller.
 *
 * @see SocialMediaResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PeaksApp.class)
public class SocialMediaResourceIntTest {

    private static final String DEFAULT_FACEBOOK_LINK = "AAAAAAAAAA";
    private static final String UPDATED_FACEBOOK_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_GOOGLE_PLUS_LINK = "AAAAAAAAAA";
    private static final String UPDATED_GOOGLE_PLUS_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_TWITTER_LINK = "AAAAAAAAAA";
    private static final String UPDATED_TWITTER_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_INSTAGRAM_LINK = "AAAAAAAAAA";
    private static final String UPDATED_INSTAGRAM_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_PINTEREST_LINK = "AAAAAAAAAA";
    private static final String UPDATED_PINTEREST_LINK = "BBBBBBBBBB";

    @Autowired
    private SocialMediaRepository socialMediaRepository;
    
    @Autowired
    private SocialMediaService socialMediaService;

    /**
     * This repository is mocked in the fr.celexio.peaks.repository.search test package.
     *
     * @see fr.celexio.peaks.repository.search.SocialMediaSearchRepositoryMockConfiguration
     */
    @Autowired
    private SocialMediaSearchRepository mockSocialMediaSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSocialMediaMockMvc;

    private SocialMedia socialMedia;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SocialMediaResource socialMediaResource = new SocialMediaResource(socialMediaService);
        this.restSocialMediaMockMvc = MockMvcBuilders.standaloneSetup(socialMediaResource)
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
    public static SocialMedia createEntity(EntityManager em) {
        SocialMedia socialMedia = new SocialMedia()
            .facebookLink(DEFAULT_FACEBOOK_LINK)
            .googlePlusLink(DEFAULT_GOOGLE_PLUS_LINK)
            .twitterLink(DEFAULT_TWITTER_LINK)
            .instagramLink(DEFAULT_INSTAGRAM_LINK)
            .pinterestLink(DEFAULT_PINTEREST_LINK);
        return socialMedia;
    }

    @Before
    public void initTest() {
        socialMedia = createEntity(em);
    }

    @Test
    @Transactional
    public void createSocialMedia() throws Exception {
        int databaseSizeBeforeCreate = socialMediaRepository.findAll().size();

        // Create the SocialMedia
        restSocialMediaMockMvc.perform(post("/api/social-medias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(socialMedia)))
            .andExpect(status().isCreated());

        // Validate the SocialMedia in the database
        List<SocialMedia> socialMediaList = socialMediaRepository.findAll();
        assertThat(socialMediaList).hasSize(databaseSizeBeforeCreate + 1);
        SocialMedia testSocialMedia = socialMediaList.get(socialMediaList.size() - 1);
        assertThat(testSocialMedia.getFacebookLink()).isEqualTo(DEFAULT_FACEBOOK_LINK);
        assertThat(testSocialMedia.getGooglePlusLink()).isEqualTo(DEFAULT_GOOGLE_PLUS_LINK);
        assertThat(testSocialMedia.getTwitterLink()).isEqualTo(DEFAULT_TWITTER_LINK);
        assertThat(testSocialMedia.getInstagramLink()).isEqualTo(DEFAULT_INSTAGRAM_LINK);
        assertThat(testSocialMedia.getPinterestLink()).isEqualTo(DEFAULT_PINTEREST_LINK);

        // Validate the SocialMedia in Elasticsearch
        verify(mockSocialMediaSearchRepository, times(1)).save(testSocialMedia);
    }

    @Test
    @Transactional
    public void createSocialMediaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = socialMediaRepository.findAll().size();

        // Create the SocialMedia with an existing ID
        socialMedia.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSocialMediaMockMvc.perform(post("/api/social-medias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(socialMedia)))
            .andExpect(status().isBadRequest());

        // Validate the SocialMedia in the database
        List<SocialMedia> socialMediaList = socialMediaRepository.findAll();
        assertThat(socialMediaList).hasSize(databaseSizeBeforeCreate);

        // Validate the SocialMedia in Elasticsearch
        verify(mockSocialMediaSearchRepository, times(0)).save(socialMedia);
    }

    @Test
    @Transactional
    public void getAllSocialMedias() throws Exception {
        // Initialize the database
        socialMediaRepository.saveAndFlush(socialMedia);

        // Get all the socialMediaList
        restSocialMediaMockMvc.perform(get("/api/social-medias?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(socialMedia.getId().intValue())))
            .andExpect(jsonPath("$.[*].facebookLink").value(hasItem(DEFAULT_FACEBOOK_LINK.toString())))
            .andExpect(jsonPath("$.[*].googlePlusLink").value(hasItem(DEFAULT_GOOGLE_PLUS_LINK.toString())))
            .andExpect(jsonPath("$.[*].twitterLink").value(hasItem(DEFAULT_TWITTER_LINK.toString())))
            .andExpect(jsonPath("$.[*].instagramLink").value(hasItem(DEFAULT_INSTAGRAM_LINK.toString())))
            .andExpect(jsonPath("$.[*].pinterestLink").value(hasItem(DEFAULT_PINTEREST_LINK.toString())));
    }
    
    @Test
    @Transactional
    public void getSocialMedia() throws Exception {
        // Initialize the database
        socialMediaRepository.saveAndFlush(socialMedia);

        // Get the socialMedia
        restSocialMediaMockMvc.perform(get("/api/social-medias/{id}", socialMedia.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(socialMedia.getId().intValue()))
            .andExpect(jsonPath("$.facebookLink").value(DEFAULT_FACEBOOK_LINK.toString()))
            .andExpect(jsonPath("$.googlePlusLink").value(DEFAULT_GOOGLE_PLUS_LINK.toString()))
            .andExpect(jsonPath("$.twitterLink").value(DEFAULT_TWITTER_LINK.toString()))
            .andExpect(jsonPath("$.instagramLink").value(DEFAULT_INSTAGRAM_LINK.toString()))
            .andExpect(jsonPath("$.pinterestLink").value(DEFAULT_PINTEREST_LINK.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSocialMedia() throws Exception {
        // Get the socialMedia
        restSocialMediaMockMvc.perform(get("/api/social-medias/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSocialMedia() throws Exception {
        // Initialize the database
        socialMediaService.save(socialMedia);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockSocialMediaSearchRepository);

        int databaseSizeBeforeUpdate = socialMediaRepository.findAll().size();

        // Update the socialMedia
        SocialMedia updatedSocialMedia = socialMediaRepository.findById(socialMedia.getId()).get();
        // Disconnect from session so that the updates on updatedSocialMedia are not directly saved in db
        em.detach(updatedSocialMedia);
        updatedSocialMedia
            .facebookLink(UPDATED_FACEBOOK_LINK)
            .googlePlusLink(UPDATED_GOOGLE_PLUS_LINK)
            .twitterLink(UPDATED_TWITTER_LINK)
            .instagramLink(UPDATED_INSTAGRAM_LINK)
            .pinterestLink(UPDATED_PINTEREST_LINK);

        restSocialMediaMockMvc.perform(put("/api/social-medias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSocialMedia)))
            .andExpect(status().isOk());

        // Validate the SocialMedia in the database
        List<SocialMedia> socialMediaList = socialMediaRepository.findAll();
        assertThat(socialMediaList).hasSize(databaseSizeBeforeUpdate);
        SocialMedia testSocialMedia = socialMediaList.get(socialMediaList.size() - 1);
        assertThat(testSocialMedia.getFacebookLink()).isEqualTo(UPDATED_FACEBOOK_LINK);
        assertThat(testSocialMedia.getGooglePlusLink()).isEqualTo(UPDATED_GOOGLE_PLUS_LINK);
        assertThat(testSocialMedia.getTwitterLink()).isEqualTo(UPDATED_TWITTER_LINK);
        assertThat(testSocialMedia.getInstagramLink()).isEqualTo(UPDATED_INSTAGRAM_LINK);
        assertThat(testSocialMedia.getPinterestLink()).isEqualTo(UPDATED_PINTEREST_LINK);

        // Validate the SocialMedia in Elasticsearch
        verify(mockSocialMediaSearchRepository, times(1)).save(testSocialMedia);
    }

    @Test
    @Transactional
    public void updateNonExistingSocialMedia() throws Exception {
        int databaseSizeBeforeUpdate = socialMediaRepository.findAll().size();

        // Create the SocialMedia

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSocialMediaMockMvc.perform(put("/api/social-medias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(socialMedia)))
            .andExpect(status().isBadRequest());

        // Validate the SocialMedia in the database
        List<SocialMedia> socialMediaList = socialMediaRepository.findAll();
        assertThat(socialMediaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SocialMedia in Elasticsearch
        verify(mockSocialMediaSearchRepository, times(0)).save(socialMedia);
    }

    @Test
    @Transactional
    public void deleteSocialMedia() throws Exception {
        // Initialize the database
        socialMediaService.save(socialMedia);

        int databaseSizeBeforeDelete = socialMediaRepository.findAll().size();

        // Get the socialMedia
        restSocialMediaMockMvc.perform(delete("/api/social-medias/{id}", socialMedia.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<SocialMedia> socialMediaList = socialMediaRepository.findAll();
        assertThat(socialMediaList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the SocialMedia in Elasticsearch
        verify(mockSocialMediaSearchRepository, times(1)).deleteById(socialMedia.getId());
    }

    @Test
    @Transactional
    public void searchSocialMedia() throws Exception {
        // Initialize the database
        socialMediaService.save(socialMedia);
        when(mockSocialMediaSearchRepository.search(queryStringQuery("id:" + socialMedia.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(socialMedia), PageRequest.of(0, 1), 1));
        // Search the socialMedia
        restSocialMediaMockMvc.perform(get("/api/_search/social-medias?query=id:" + socialMedia.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(socialMedia.getId().intValue())))
            .andExpect(jsonPath("$.[*].facebookLink").value(hasItem(DEFAULT_FACEBOOK_LINK.toString())))
            .andExpect(jsonPath("$.[*].googlePlusLink").value(hasItem(DEFAULT_GOOGLE_PLUS_LINK.toString())))
            .andExpect(jsonPath("$.[*].twitterLink").value(hasItem(DEFAULT_TWITTER_LINK.toString())))
            .andExpect(jsonPath("$.[*].instagramLink").value(hasItem(DEFAULT_INSTAGRAM_LINK.toString())))
            .andExpect(jsonPath("$.[*].pinterestLink").value(hasItem(DEFAULT_PINTEREST_LINK.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SocialMedia.class);
        SocialMedia socialMedia1 = new SocialMedia();
        socialMedia1.setId(1L);
        SocialMedia socialMedia2 = new SocialMedia();
        socialMedia2.setId(socialMedia1.getId());
        assertThat(socialMedia1).isEqualTo(socialMedia2);
        socialMedia2.setId(2L);
        assertThat(socialMedia1).isNotEqualTo(socialMedia2);
        socialMedia1.setId(null);
        assertThat(socialMedia1).isNotEqualTo(socialMedia2);
    }
}
