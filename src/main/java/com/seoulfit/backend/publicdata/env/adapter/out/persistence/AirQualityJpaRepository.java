package com.seoulfit.backend.publicdata.env.adapter.out.persistence;

import com.seoulfit.backend.publicdata.env.domain.AirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 대기질 정보 JPA Repository
 */
@Repository
public interface AirQualityJpaRepository extends JpaRepository<AirQuality, Long> {

    /**
     * 측정소명과 측정일시로 조회
     */
    Optional<AirQuality> findByMsrSteNmAndMsrDt(String stationName, LocalDateTime dateTime);

    /**
     * 특정 측정소의 최신 데이터 조회
     */
    Optional<AirQuality> findTopByMsrSteNmOrderByMsrDtDesc(String stationName);

    /**
     * 특정 기간의 데이터 조회
     */
    List<AirQuality> findByMsrDtBetweenOrderByMsrDtDesc(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * KHAI 값 이상의 데이터 조회
     */
    List<AirQuality> findByKhaiValueGreaterThanEqualOrderByMsrDtDesc(Integer minKhaiValue);

    /**
     * 측정소별 데이터 개수 조회
     */
    long countByMsrSteNm(String stationName);

    /**
     * 특정 기간의 데이터 개수 조회
     */
    long countByMsrDtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 특정 날짜 이전 데이터 삭제
     */
    @Modifying
    int deleteByMsrDtBefore(LocalDateTime beforeDateTime);

    /**
     * 모든 측정소의 최신 데이터 조회 (간단한 버전)
     */
    @Query("SELECT a FROM AirQuality a ORDER BY a.msrDt DESC")
    List<AirQuality> findAllOrderByMsrDtDesc();

    /**
     * 특정 지역의 데이터 조회
     */
    List<AirQuality> findByMsrRgnNmOrderByMsrDtDesc(String regionName);

    /**
     * 복합 조건 쿼리 (간단한 버전)
     */
    @Query("""
        SELECT a FROM AirQuality a 
        WHERE (:stationName IS NULL OR a.msrSteNm = :stationName)
        AND (:regionName IS NULL OR a.msrRgnNm = :regionName)
        AND (:startTime IS NULL OR a.msrDt >= :startTime)
        AND (:endTime IS NULL OR a.msrDt <= :endTime)
        AND (:minKhaiValue IS NULL OR a.khaiValue >= :minKhaiValue)
        ORDER BY a.msrDt DESC
        """)
    List<AirQuality> findByQuery(
        @Param("stationName") String stationName,
        @Param("regionName") String regionName,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("minKhaiValue") Integer minKhaiValue
    );
}
