package fr.celexio.peaks.repository;

import fr.celexio.peaks.domain.SocialMedia;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the SocialMedia entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SocialMediaRepository extends JpaRepository<SocialMedia, Long> {

}
