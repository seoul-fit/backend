package com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository;

import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 무더위쉼터 레포지토리
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Repository
public interface CoolingCenterRepository extends JpaRepository<CoolingCenter, Long> {

    /**
     * 위치 기반 무더위쉼터 조회 (반경 내)
     */
    @Query(value = """
        SELECT * FROM cooling_centers cc
        WHERE cc.latitude IS NOT NULL 
        AND cc.longitude IS NOT NULL
        AND (6371 * acos(cos(radians(:latitude)) * cos(radians(cc.latitude)) 
            * cos(radians(cc.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(cc.latitude)))) <= :radiusKm
        ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(cc.latitude)) 
            * cos(radians(cc.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(cc.latitude))))
        """, nativeQuery = true)
    List<CoolingCenter> findByLocationWithinRadius(@Param("latitude") Double latitude, 
                                                  @Param("longitude") Double longitude, 
                                                  @Param("radiusKm") Double radiusKm);

    /**
     * 쉼터명으로 검색
     */
    List<CoolingCenter> findByNameContaining(String name);

    /**
     * 시설구분별 조회
     */
    List<CoolingCenter> findByFacilityType1Containing(String facilityType1);

    /**
     * 수용인원 이상 조회
     */
    @Query("SELECT cc FROM CoolingCenter cc WHERE cc.capacity >= :minCapacity")
    List<CoolingCenter> findByCapacityGreaterThanEqual(@Param("minCapacity") Integer minCapacity);

    /**
     * 바운딩 박스 기반 무더위쉼터 조회 (성능 최적화)
     */
    @Query("SELECT cc FROM CoolingCenter cc WHERE cc.latitude BETWEEN :minLat AND :maxLat " +
           "AND cc.longitude BETWEEN :minLng AND :maxLng " +
           "AND cc.latitude IS NOT NULL AND cc.longitude IS NOT NULL")
    List<CoolingCenter> findByBoundingBox(@Param("minLat") Double minLat, @Param("maxLat") Double maxLat,
                                         @Param("minLng") Double minLng, @Param("maxLng") Double maxLng);

    /**
     * 위치 정보가 있는 무더위쉼터만 조회
     */
    @Query("SELECT cc FROM CoolingCenter cc WHERE cc.latitude IS NOT NULL AND cc.longitude IS NOT NULL")
    List<CoolingCenter> findAllWithLocation();

    /**
     * 최신 년도 시설 조회
     */
    @Query("SELECT cc FROM CoolingCenter cc WHERE cc.facilityYear = (SELECT MAX(c.facilityYear) FROM CoolingCenter c)")
    List<CoolingCenter> findLatestYearFacilities();
}
