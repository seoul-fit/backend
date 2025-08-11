package com.seoulfit.backend.publicdata.culture.adapter.out.repository;

import com.seoulfit.backend.publicdata.culture.domain.CulturalSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CulturalSpaceRepository extends JpaRepository<CulturalSpace, Long> {

    @Query("""
        SELECT cs FROM CulturalSpace cs 
        WHERE cs.latitude IS NOT NULL AND cs.longitude IS NOT NULL 
        AND (6371 * acos(cos(radians(:latitude)) * cos(radians(cs.latitude)) 
            * cos(radians(cs.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(cs.latitude)))) <= :radiusKm
        ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(cs.latitude)) 
            * cos(radians(cs.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(cs.latitude))))
        """)
    List<CulturalSpace> findWithInRadius(@Param("latitude") BigDecimal latitude,
                                         @Param("longitude") BigDecimal longitude,
                                         @Param("radiusKm") double radiusKm);

}
