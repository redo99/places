package fr.celexio.peaks.repository;

import fr.celexio.peaks.domain.MemberStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the MemberStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MemberStatusRepository extends JpaRepository<MemberStatus, Long> {

}
