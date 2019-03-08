package fr.celexio.peaks.repository;

import fr.celexio.peaks.domain.PlaceCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the PlaceCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlaceCategoryRepository extends JpaRepository<PlaceCategory, Long> {

}
