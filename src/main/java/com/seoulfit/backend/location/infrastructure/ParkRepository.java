package com.seoulfit.backend.location.infrastructure;

import com.seoulfit.backend.location.domain.Park;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 공원 레포지토리
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Repository
public interface ParkRepository extends JpaRepository<Park, Long> {

    /**
     * 위치 기반 공원 조회 (반경 내)
     */
    @Query(value = """
        SELECT * FROM parks p
        WHERE p.latitude IS NOT NULL 
        AND p.longitude IS NOT NULL
        AND (6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) 
            * cos(radians(p.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(p.latitude)))) <= :radiusKm
        ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) 
            * cos(radians(p.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(p.latitude))))
        """, nativeQuery = true)
    List<Park> findByLocationWithinRadius(@Param("latitude") Double latitude, 
                                         @Param("longitude") Double longitude, 
                                         @Param("radiusKm") Double radiusKm);

    /**
     * 공원명으로 검색
     */
    List<Park> findByNameContaining(String name);

    /**
     * 지역별 공원 조회
     */
    List<Park> findByZoneContaining(String zone);

    /**
     * 바운딩 박스 기반 공원 조회 (성능 최적화)
     */
    @Query("SELECT p FROM Park p WHERE p.latitude BETWEEN :minLat AND :maxLat " +
           "AND p.longitude BETWEEN :minLng AND :maxLng " +
           "AND p.latitude IS NOT NULL AND p.longitude IS NOT NULL")
    List<Park> findByBoundingBox(@Param("minLat") Double minLat, @Param("maxLat") Double maxLat,
                                @Param("minLng") Double minLng, @Param("maxLng") Double maxLng);

    /**
     * 위치 정보가 있는 공원만 조회
     */
    @Query("SELECT p FROM Park p WHERE p.latitude IS NOT NULL AND p.longitude IS NOT NULL")
    List<Park> findAllWithLocation();
}
