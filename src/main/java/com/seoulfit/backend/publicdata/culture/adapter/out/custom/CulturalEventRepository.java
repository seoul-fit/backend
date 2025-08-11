package com.seoulfit.backend.publicdata.culture.adapter.out.custom;

import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CulturalEventRepository extends JpaRepository<CulturalEvent, Long> {

    Optional<CulturalEvent> findByExternalId(String externalId);

    boolean existsByExternalId(String externalId);

    @Query("""
        SELECT ce FROM CulturalEvent ce 
        WHERE ce.district = :district
        AND (ce.startDate <= :currentDate AND ce.endDate >= :currentDate)
        ORDER BY ce.startDate ASC
        """)
    List<CulturalEvent> findOngoingEventsByDistrict(@Param("district") String district,
                                                    @Param("currentDate") LocalDate currentDate);

    @Query("""
        SELECT ce FROM CulturalEvent ce 
        WHERE ce.codeName IN :categories
        AND (ce.startDate <= :currentDate AND ce.endDate >= :currentDate)
        ORDER BY ce.startDate ASC
        """)
    List<CulturalEvent> findOngoingEventsByCategories(@Param("categories") List<String> categories,
                                                      @Param("currentDate") LocalDate currentDate);

    @Query("""
        SELECT ce FROM CulturalEvent ce 
        WHERE ce.startDate >= :startDate 
        AND ce.startDate <= :endDate
        ORDER BY ce.startDate ASC
        """)
    List<CulturalEvent> findEventsByDateRange(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT ce FROM CulturalEvent ce 
        WHERE ce.isFree = '무료'
        AND (ce.startDate <= :currentDate AND ce.endDate >= :currentDate)
        ORDER BY ce.startDate ASC
        """)
    List<CulturalEvent> findOngoingFreeEvents(@Param("currentDate") LocalDate currentDate);

    List<CulturalEvent> findByTitleContainingIgnoreCase(String keyword);

    @Query("SELECT DISTINCT ce.codeName FROM CulturalEvent ce ORDER BY ce.codeName")
    List<String> findAllCategories();

    @Query("SELECT DISTINCT ce.district FROM CulturalEvent ce ORDER BY ce.district")
    List<String> findAllDistricts();

    @Query("""
        SELECT ce FROM CulturalEvent ce 
        WHERE ce.latitude IS NOT NULL AND ce.longitude IS NOT NULL 
        AND (6371 * acos(cos(radians(:latitude)) * cos(radians(ce.latitude)) 
            * cos(radians(ce.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(ce.latitude)))) <= :radiusKm
        ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(ce.latitude)) 
            * cos(radians(ce.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(ce.latitude))))
        """)
    List<CulturalEvent> findWithInRadius(@Param("latitude") BigDecimal latitude,
                                         @Param("longitude") BigDecimal longitude,
                                         @Param("radiusKm") double radiusKm);
}
