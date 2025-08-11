package com.seoulfit.backend.env.application.port.in;

import com.seoulfit.backend.env.domain.AirQuality;
import com.seoulfit.backend.env.domain.AirQualityStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 대기질 정보 조회 Use Case
 */
public interface AirQualityQueryUseCase {

    /**
     * 최신 대기질 정보 조회
     */
    List<AirQuality> getLatestAirQuality();

    /**
     * 특정 측정소의 최신 대기질 정보 조회
     */
    Optional<AirQuality> getLatestAirQualityByStation(String stationName);

    /**
     * 지역별 최신 대기질 정보 조회
     */
    List<AirQuality> getLatestAirQualityByRegion(String regionName);

    /**
     * 특정 기간의 대기질 정보 조회
     */
    List<AirQuality> getAirQualityByPeriod(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 특정 상태 이상의 대기질 정보 조회
     */
    List<AirQuality> getAirQualityByStatus(AirQualityStatus minStatus);

    /**
     * 대기질 조회 쿼리
     */
    record AirQualityQuery(
        String stationName,
        String regionName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AirQualityStatus minStatus,
        Integer limit,
        Integer offset
    ) {
        public static AirQualityQuery of(String stationName) {
            return new AirQualityQuery(stationName, null, null, null, null, null, null);
        }

        public static AirQualityQuery of(String stationName, String regionName) {
            return new AirQualityQuery(stationName, regionName, null, null, null, null, null);
        }

        public static AirQualityQuery ofPeriod(LocalDateTime startTime, LocalDateTime endTime) {
            return new AirQualityQuery(null, null, startTime, endTime, null, null, null);
        }

        public static AirQualityQuery ofStatus(AirQualityStatus minStatus) {
            return new AirQualityQuery(null, null, null, null, minStatus, null, null);
        }
    }

    /**
     * 쿼리를 통한 대기질 정보 조회
     */
    List<AirQuality> getAirQuality(AirQualityQuery query);

    /**
     * 대기질 통계 정보 조회
     */
    AirQualityStatistics getAirQualityStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 대기질 통계 정보
     */
    record AirQualityStatistics(
        double avgPm10,
        double avgPm25,
        double avgKhai,
        long goodCount,
        long moderateCount,
        long unhealthyCount,
        long totalCount,
        LocalDateTime calculatedAt
    ) {}
}
