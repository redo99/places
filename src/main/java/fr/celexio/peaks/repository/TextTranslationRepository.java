package fr.celexio.peaks.repository;

import fr.celexio.peaks.domain.TextTranslation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the TextTranslation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TextTranslationRepository extends JpaRepository<TextTranslation, Long> {

}
