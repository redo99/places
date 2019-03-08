package fr.celexio.peaks.service.impl;

import fr.celexio.peaks.domain.ActivityFamily;
import fr.celexio.peaks.repository.ActivityFamilyRepository;
import fr.celexio.peaks.repository.search.ActivityFamilySearchRepository;
import fr.celexio.peaks.service.ActivityFamilyService;
import org.elasticsearch.common.inject.Inject;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Any;

import java.util.Optional;

public class ActivityFamilyServiceImplTest {

    @Mock
    ActivityFamilyRepository activityFamilyRepository;

    @Mock
    ActivityFamilySearchRepository activityFamilySearchRepository;

    ActivityFamilyService activityFamilyService;

    @BeforeClass
    public static void initClass() {
        System.out.println("ActivityFamilyServiceImplTest.initClass()");
    }

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        activityFamilyService = new ActivityFamilyServiceImpl(activityFamilyRepository, activityFamilySearchRepository);

    }

    @Test
    public void test() {
        ActivityFamily activityFamily = new ActivityFamily();
        Mockito.when(activityFamilyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(activityFamily));
        activityFamilyService.findOne(1L);
        Assert.assertFalse(activityFamily.equals(null));
    }
}
