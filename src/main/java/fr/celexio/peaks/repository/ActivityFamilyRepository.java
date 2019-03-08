package fr.celexio.peaks.repository;

import fr.celexio.peaks.domain.ActivityFamily;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ActivityFamily entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActivityFamilyRepository extends JpaRepository<ActivityFamily, Long> {

}
