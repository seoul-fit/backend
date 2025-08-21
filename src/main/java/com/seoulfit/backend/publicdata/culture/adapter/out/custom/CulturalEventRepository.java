package com.seoulfit.backend.publicdata.culture.adapter.out.custom;

import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 문화행사 리포지토리
 * <p>
 * 문화행사 데이터에 대한 데이터베이스 접근을 담당하는 리포지토리입니다.
 * JPA를 사용하여 문화행사 엔티티에 대한 CRUD 작업과
 * 특별한 조회 기능을 제공합니다.
 * </p>
 * 
 * @author Seoul Fit
 * @since 1.0.0
 * @see CulturalEvent
 */
public interface CulturalEventRepository extends JpaRepository<CulturalEvent, Long> {

    /**
     * 외부 ID로 문화행사를 조회합니다.
     * 
     * @param externalId 서울시 API에서 제공하는 고유 ID
     * @return 문화행사 정보 (Optional)
     */
    Optional<CulturalEvent> findByExternalId(String externalId);

    /**
     * 외부 ID로 문화행사 존재 여부를 확인합니다.
     * 
     * @param externalId 서울시 API에서 제공하는 고유 ID
     * @return 존재 여부
     */
    boolean existsByExternalId(String externalId);

    /**
     * 특정 구에서 진행 중인 문화행사를 조회합니다.
     * 
     * @param district 구 이름 (예: 강남구, 서초구)
     * @param currentDate 현재 날짜
     * @return 진행 중인 문화행사 목록
     */
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

    /**
     * 지정된 위치에서 특정 반경 내의 문화행사를 조회합니다.
     * <p>
     * Haversine 공식을 사용하여 두 지점 간의 거리를 계산합니다.
     * 결과는 거리순으로 정렬됩니다.
     * </p>
     * 
     * @param latitude 기준 위도 (WGS84)
     * @param longitude 기준 경도 (WGS84)
     * @param radiusKm 검색 반경 (km 단위)
     * @return 반경 내 문화행사 목록 (거리순 정렬)
     */
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
