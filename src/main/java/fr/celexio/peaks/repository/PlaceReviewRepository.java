package fr.celexio.peaks.repository;

import fr.celexio.peaks.domain.PlaceReview;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the PlaceReview entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {

}
