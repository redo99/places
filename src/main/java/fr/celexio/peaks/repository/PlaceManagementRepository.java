package fr.celexio.peaks.repository;

import fr.celexio.peaks.domain.PlaceManagement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the PlaceManagement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlaceManagementRepository extends JpaRepository<PlaceManagement, Long> {

}
