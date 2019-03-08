package fr.celexio.peaks.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of PlaceReviewSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PlaceReviewSearchRepositoryMockConfiguration {

    @MockBean
    private PlaceReviewSearchRepository mockPlaceReviewSearchRepository;

}
