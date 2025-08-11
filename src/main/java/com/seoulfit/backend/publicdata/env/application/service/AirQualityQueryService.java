package com.seoulfit.backend.publicdata.env.application.service;

import com.seoulfit.backend.publicdata.env.application.port.in.AirQualityQueryUseCase;
import com.seoulfit.backend.publicdata.env.application.port.out.AirQualityRepository;
import com.seoulfit.backend.publicdata.env.domain.AirQuality;
import com.seoulfit.backend.publicdata.env.domain.AirQualityStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 대기질 정보 조회 서비스
 * 헥사고날 아키텍처의 애플리케이션 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirQualityQueryService implements AirQualityQueryUseCase {

    private final AirQualityRepository repository;

    @Override
    public List<AirQuality> getLatestAirQuality() {
        log.debug("최신 대기질 정보 조회");
        return repository.findLatest();
    }

    @Override
    public Optional<AirQuality> getLatestAirQualityByStation(String stationName) {
        log.debug("특정 측정소의 최신 대기질 정보 조회: {}", stationName);
        return repository.findLatestByStation(stationName);
    }

    @Override
    public List<AirQuality> getLatestAirQualityByRegion(String regionName) {
        log.debug("지역별 최신 대기질 정보 조회: {}", regionName);
        return repository.findLatestByRegion(regionName);
    }

    @Override
    public List<AirQuality> getAirQualityByPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("특정 기간의 대기질 정보 조회: {} ~ {}", startTime, endTime);
        return repository.findByPeriod(startTime, endTime);
    }

    @Override
    public List<AirQuality> getAirQualityByStatus(AirQualityStatus minStatus) {
        log.debug("특정 상태 이상의 대기질 정보 조회: {}", minStatus);
        return repository.findByStatus(minStatus);
    }

    @Override
    public List<AirQuality> getAirQuality(AirQualityQuery query) {
        log.debug("쿼리를 통한 대기질 정보 조회: {}", query);
        return repository.findByQuery(query);
    }

    @Override
    public AirQualityStatistics getAirQualityStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("대기질 통계 정보 조회: {} ~ {}", startTime, endTime);
        return repository.getStatistics(startTime, endTime);
    }

    /**
     * 알림이 필요한 대기질 정보 조회
     */
    public List<AirQuality> getAirQualityRequiringNotification() {
        log.debug("알림이 필요한 대기질 정보 조회");
        return repository.findByStatus(AirQualityStatus.UNHEALTHY_FOR_SENSITIVE);
    }

    /**
     * 특정 측정소의 최근 24시간 대기질 변화 조회
     */
    public List<AirQuality> getAirQualityTrend(String stationName, int hours) {
        log.debug("특정 측정소의 최근 {}시간 대기질 변화 조회: {}", hours, stationName);
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(hours);
        
        AirQualityQuery query = new AirQualityQuery(
            stationName, null, startTime, endTime, null, null, null
        );
        
        return repository.findByQuery(query);
    }

    /**
     * 서울시 전체 대기질 현황 요약
     */
    public AirQualitySummary getSeoulAirQualitySummary() {
        log.debug("서울시 전체 대기질 현황 요약 조회");
        
        List<AirQuality> latestData = repository.findLatest();
        
        if (latestData.isEmpty()) {
            return new AirQualitySummary(0, 0, 0, 0, 0, 0, 0, LocalDateTime.now());
        }

        int goodCount = 0;
        int moderateCount = 0;
        int unhealthyForSensitiveCount = 0;
        int unhealthyCount = 0;
        int veryUnhealthyCount = 0;
        int hazardousCount = 0;

        for (AirQuality airQuality : latestData) {
            AirQualityStatus status = airQuality.getAirQualityStatus();
            switch (status) {
                case GOOD -> goodCount++;
                case MODERATE -> moderateCount++;
                case UNHEALTHY_FOR_SENSITIVE -> unhealthyForSensitiveCount++;
                case UNHEALTHY -> unhealthyCount++;
                case VERY_UNHEALTHY -> veryUnhealthyCount++;
                case HAZARDOUS -> hazardousCount++;
            }
        }

        return new AirQualitySummary(
            goodCount, moderateCount, unhealthyForSensitiveCount,
            unhealthyCount, veryUnhealthyCount, hazardousCount,
            latestData.size(), LocalDateTime.now()
        );
    }

    /**
     * 대기질 현황 요약
     */
    public record AirQualitySummary(
        int goodCount,
        int moderateCount,
        int unhealthyForSensitiveCount,
        int unhealthyCount,
        int veryUnhealthyCount,
        int hazardousCount,
        int totalStations,
        LocalDateTime calculatedAt
    ) {}
}
