package fr.celexio.peaks.repository;

import fr.celexio.peaks.domain.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Place entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query(value = "select distinct places from Place places left join fetch places.categories left join fetch places.activities left join fetch places.managements",
        countQuery = "select count(distinct places) from Place places")
    Page<Place> findAllWithEagerRelationships(Pageable pageable);

    @Query(value = "select distinct places from Place places left join fetch places.categories left join fetch places.activities left join fetch places.managements")
    List<Place> findAllWithEagerRelationships();

    @Query("select places from Place places left join fetch places.categories left join fetch places.activities left join fetch places.managements where places.id =:id")
    Optional<Place> findOneWithEagerRelationships(@Param("id") Long id);

}
