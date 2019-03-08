package fr.celexio.peaks.repository;

import fr.celexio.peaks.domain.MemberCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the MemberCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MemberCategoryRepository extends JpaRepository<MemberCategory, Long> {

}
