package fr.celexio.peaks.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of SpotSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class SpotSearchRepositoryMockConfiguration {

    @MockBean
    private SpotSearchRepository mockSpotSearchRepository;

}
