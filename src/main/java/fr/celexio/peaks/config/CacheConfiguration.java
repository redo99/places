package fr.celexio.peaks.config;

import java.time.Duration;

import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;

import io.github.jhipster.config.jcache.BeanClassLoaderAwareJCacheRegionFactory;
import io.github.jhipster.config.JHipsterProperties;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        BeanClassLoaderAwareJCacheRegionFactory.setBeanClassLoader(this.getClass().getClassLoader());
        JHipsterProperties.Cache.Ehcache ehcache =
            jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build());
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            cm.createCache(fr.celexio.peaks.repository.UserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.repository.UserRepository.USERS_BY_EMAIL_CACHE, jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.User.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.Authority.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.User.class.getName() + ".authorities", jcacheConfiguration);
            // jhipster-needle-ehcache-add-entry
            cm.createCache(fr.celexio.peaks.domain.Activity.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.ActivityFamily.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.ActivityFamily.class.getName() + ".activities", jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.LabelTranslation.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.Location.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.Member.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.MemberCategory.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.MemberStatus.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.Picture.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.Place.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.Place.class.getName() + ".videos", jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.Place.class.getName() + ".activities", jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.Place.class.getName() + ".pictures", jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.Place.class.getName() + ".reviews", jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.Place.class.getName() + ".categories", jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.Place.class.getName() + ".managements", jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.PlaceCategory.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.PlaceManagement.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.PlaceReview.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.SocialMedia.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.Spot.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.TextTranslation.class.getName(), jcacheConfiguration);
            cm.createCache(fr.celexio.peaks.domain.Video.class.getName(), jcacheConfiguration);
        };
    }
}
