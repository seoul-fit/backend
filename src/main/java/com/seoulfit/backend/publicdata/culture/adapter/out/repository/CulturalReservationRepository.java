package com.seoulfit.backend.publicdata.culture.adapter.out.repository;

import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CulturalReservationRepository extends JpaRepository<CulturalReservation, Long> {

    @Query(value = """
            SELECT * FROM cultural_reservation cr
            WHERE cr.x_coord IS NOT NULL AND cr.y_coord IS NOT NULL 
            AND (6371 * acos(cos(radians(:y_coord)) * cos(radians(cr.y_coord)) 
                * cos(radians(cr.x_coord) - radians(:x_coord)) 
                + sin(radians(:y_coord)) * sin(radians(cr.y_coord)))) <= :radiusKm
            ORDER BY (6371 * acos(cos(radians(:y_coord)) * cos(radians(cr.y_coord)) 
                * cos(radians(cr.x_coord) - radians(:x_coord)) 
                + sin(radians(:y_coord)) * sin(radians(cr.y_coord))))
            """, nativeQuery = true)
    List<CulturalReservation> findWithInRadius(@Param("x_coord") BigDecimal x_coord,
                                               @Param("y_coord") BigDecimal y_coord,
                                               @Param("radiusKm") double radiusKm);

}
