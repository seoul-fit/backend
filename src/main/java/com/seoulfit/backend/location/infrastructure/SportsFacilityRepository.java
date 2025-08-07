package com.seoulfit.backend.location.infrastructure;

import com.seoulfit.backend.location.domain.SportsFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 체육시설 레포지토리
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Repository
public interface SportsFacilityRepository extends JpaRepository<SportsFacility, Long> {

    /**
     * 위치 기반 체육시설 조회 (반경 내)
     */
    @Query(value = """
        SELECT * FROM sports_facilities sf
        WHERE sf.latitude IS NOT NULL 
        AND sf.longitude IS NOT NULL
        AND (6371 * acos(cos(radians(:latitude)) * cos(radians(sf.latitude)) 
            * cos(radians(sf.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(sf.latitude)))) <= :radiusKm
        ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(sf.latitude)) 
            * cos(radians(sf.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(sf.latitude))))
        """, nativeQuery = true)
    List<SportsFacility> findByLocationWithinRadius(@Param("latitude") Double latitude, 
                                                   @Param("longitude") Double longitude, 
                                                   @Param("radiusKm") Double radiusKm);

    /**
     * 시설명으로 검색
     */
    List<SportsFacility> findByNameContaining(String name);

    /**
     * 자치구별 체육시설 조회
     */
    List<SportsFacility> findByDistrictContaining(String district);

    /**
     * 시설유형별 조회
     */
    List<SportsFacility> findByFacilityTypeContaining(String facilityType);

    /**
     * 운영 중인 시설만 조회
     */
    @Query("SELECT sf FROM SportsFacility sf WHERE sf.operationStatus LIKE '%운영%'")
    List<SportsFacility> findOperatingFacilities();

    /**
     * 바운딩 박스 기반 체육시설 조회 (성능 최적화)
     */
    @Query("SELECT sf FROM SportsFacility sf WHERE sf.latitude BETWEEN :minLat AND :maxLat " +
           "AND sf.longitude BETWEEN :minLng AND :maxLng " +
           "AND sf.latitude IS NOT NULL AND sf.longitude IS NOT NULL")
    List<SportsFacility> findByBoundingBox(@Param("minLat") Double minLat, @Param("maxLat") Double maxLat,
                                          @Param("minLng") Double minLng, @Param("maxLng") Double maxLng);

    /**
     * 위치 정보가 있는 체육시설만 조회
     */
    @Query("SELECT sf FROM SportsFacility sf WHERE sf.latitude IS NOT NULL AND sf.longitude IS NOT NULL")
    List<SportsFacility> findAllWithLocation();
}
