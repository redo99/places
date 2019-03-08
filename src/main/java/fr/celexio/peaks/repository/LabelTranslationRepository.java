package fr.celexio.peaks.repository;

import fr.celexio.peaks.domain.LabelTranslation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the LabelTranslation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LabelTranslationRepository extends JpaRepository<LabelTranslation, Long> {

}
