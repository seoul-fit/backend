package com.seoulfit.backend.publicdata.sports.infrastructure;

import com.seoulfit.backend.publicdata.sports.domain.Sports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 체육시설 리포지토리
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Repository
public interface SportsRepository extends JpaRepository<Sports, Long> {

    /**
     * 시설명으로 체육시설 조회
     */
    Optional<Sports> findByFacilityName(String facilityName);

    /**
     * 시설유형으로 체육시설 목록 조회
     */
    List<Sports> findByFacilityType(String facilityType);

    /**
     * 자치구로 체육시설 목록 조회
     */
    List<Sports> findByDistrict(String district);

    /**
     * 시설명에 키워드가 포함된 체육시설 목록 조회
     */
    List<Sports> findByFacilityNameContainingIgnoreCase(String keyword);

    /**
     * 특정 반경 내의 체육시설 조회 (Haversine 공식 사용)
     * 
     * @param latitude 중심 위도
     * @param longitude 중심 경도
     * @param radiusKm 반경 (km)
     * @return 반경 내 체육시설 목록
     */
    @Query(value = """
        SELECT s.* FROM sports_reservation_facilities s 
        WHERE s.latitude IS NOT NULL 
        AND s.longitude IS NOT NULL
        AND (6371 * acos(cos(radians(:latitude)) 
            * cos(radians(s.latitude)) 
            * cos(radians(s.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) 
            * sin(radians(s.latitude)))) <= :radiusKm
        ORDER BY (6371 * acos(cos(radians(:latitude)) 
            * cos(radians(s.latitude)) 
            * cos(radians(s.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) 
            * sin(radians(s.latitude))))
        """, nativeQuery = true)
    List<Sports> findByLocationWithinRadius(@Param("latitude") double latitude, 
                                           @Param("longitude") double longitude, 
                                           @Param("radiusKm") double radiusKm);

    /**
     * 위경도 정보가 없는 체육시설 조회
     */
    List<Sports> findByLatitudeIsNullOrLongitudeIsNull();

    /**
     * 시설유형과 자치구로 체육시설 목록 조회
     */
    List<Sports> findByFacilityTypeAndDistrict(String facilityType, String district);

    /**
     * 전체 체육시설 수 조회
     */
    @Query("SELECT COUNT(s) FROM Sports s")
    long countAllSports();

    /**
     * 위경도 정보가 있는 체육시설 수 조회
     */
    @Query("SELECT COUNT(s) FROM Sports s WHERE s.latitude IS NOT NULL AND s.longitude IS NOT NULL")
    long countSportsWithLocation();
}
