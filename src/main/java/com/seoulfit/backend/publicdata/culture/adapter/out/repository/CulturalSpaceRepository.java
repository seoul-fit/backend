package com.seoulfit.backend.publicdata.culture.adapter.out.repository;

import com.seoulfit.backend.publicdata.culture.domain.CulturalSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CulturalSpaceRepository extends JpaRepository<CulturalSpace, Long> {

    /**
     * 위치 기반 문화공간 조회 (반경 내)
     * Haversine 공식을 사용하여 두 지점 간의 거리를 계산
     * 
     * @param latitude 중심 위도
     * @param longitude 중심 경도
     * @param radiusKm 검색 반경 (km)
     * @return 반경 내 문화공간 목록 (거리순 정렬)
     */
    @Query(value = """
        SELECT * FROM cultural_spaces cs 
        WHERE cs.latitude IS NOT NULL AND cs.longitude IS NOT NULL 
        AND (6371 * acos(
            LEAST(1.0,
                cos(radians(:latitude)) * cos(radians(cs.latitude)) 
                * cos(radians(cs.longitude) - radians(:longitude)) 
                + sin(radians(:latitude)) * sin(radians(cs.latitude))
            )
        )) <= :radiusKm
        ORDER BY (6371 * acos(
            LEAST(1.0,
                cos(radians(:latitude)) * cos(radians(cs.latitude)) 
                * cos(radians(cs.longitude) - radians(:longitude)) 
                + sin(radians(:latitude)) * sin(radians(cs.latitude))
            )
        ))
        """, nativeQuery = true)
    List<CulturalSpace> findWithInRadius(@Param("latitude") BigDecimal latitude,
                                         @Param("longitude") BigDecimal longitude,
                                         @Param("radiusKm") double radiusKm);

}
