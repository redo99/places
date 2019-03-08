package fr.celexio.peaks.repository;

import fr.celexio.peaks.domain.Picture;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Picture entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {

}
