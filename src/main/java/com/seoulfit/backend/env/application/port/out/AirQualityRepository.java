package com.seoulfit.backend.env.application.port.out;

import com.seoulfit.backend.env.application.port.in.AirQualityQueryUseCase;
import com.seoulfit.backend.env.domain.AirQuality;
import com.seoulfit.backend.env.domain.AirQualityStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 대기질 정보 저장소 포트
 */
public interface AirQualityRepository {

    /**
     * 대기질 정보 저장
     */
    AirQuality save(AirQuality airQuality);

    /**
     * 대기질 정보 일괄 저장
     */
    List<AirQuality> saveAll(List<AirQuality> airQualities);

    /**
     * ID로 대기질 정보 조회
     */
    Optional<AirQuality> findById(Long id);

    /**
     * 측정소명과 측정일시로 대기질 정보 조회
     */
    Optional<AirQuality> findByStationAndDateTime(String stationName, LocalDateTime dateTime);

    /**
     * 최신 대기질 정보 조회
     */
    List<AirQuality> findLatest();

    /**
     * 특정 측정소의 최신 대기질 정보 조회
     */
    Optional<AirQuality> findLatestByStation(String stationName);

    /**
     * 지역별 최신 대기질 정보 조회
     */
    List<AirQuality> findLatestByRegion(String regionName);

    /**
     * 특정 기간의 대기질 정보 조회
     */
    List<AirQuality> findByPeriod(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 특정 상태 이상의 대기질 정보 조회
     */
    List<AirQuality> findByStatus(AirQualityStatus minStatus);

    /**
     * 쿼리를 통한 대기질 정보 조회
     */
    List<AirQuality> findByQuery(AirQualityQueryUseCase.AirQualityQuery query);

    /**
     * 대기질 통계 정보 조회
     */
    AirQualityQueryUseCase.AirQualityStatistics getStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 특정 기간 이전의 데이터 삭제 (데이터 정리용)
     */
    int deleteOldData(LocalDateTime beforeDateTime);

    /**
     * 전체 데이터 개수 조회
     */
    long count();

    /**
     * 특정 측정소의 데이터 개수 조회
     */
    long countByStation(String stationName);

    /**
     * 특정 기간의 데이터 개수 조회
     */
    long countByPeriod(LocalDateTime startTime, LocalDateTime endTime);
}
