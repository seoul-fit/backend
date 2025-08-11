package com.seoulfit.backend.publicdata.env.adapter.out.persistence;

import com.seoulfit.backend.publicdata.env.application.port.in.AirQualityQueryUseCase;
import com.seoulfit.backend.publicdata.env.application.port.out.AirQualityRepository;
import com.seoulfit.backend.publicdata.env.domain.AirQuality;
import com.seoulfit.backend.publicdata.env.domain.AirQualityStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 대기질 정보 저장소 어댑터
 * 헥사고날 아키텍처의 Adapter Out
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AirQualityRepositoryAdapter implements AirQualityRepository {

    private final AirQualityJpaRepository jpaRepository;

    @Override
    public AirQuality save(AirQuality airQuality) {
        return jpaRepository.save(airQuality);
    }

    @Override
    public List<AirQuality> saveAll(List<AirQuality> airQualities) {
        return jpaRepository.saveAll(airQualities);
    }

    @Override
    public Optional<AirQuality> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<AirQuality> findByStationAndDateTime(String stationName, LocalDateTime dateTime) {
        return jpaRepository.findByMsrSteNmAndMsrDt(stationName, dateTime);
    }

    @Override
    public List<AirQuality> findLatest() {
        // 각 측정소별 최신 데이터를 메모리에서 필터링
        List<AirQuality> allData = jpaRepository.findAllOrderByMsrDtDesc();
        
        Map<String, AirQuality> latestByStation = allData.stream()
            .collect(Collectors.toMap(
                AirQuality::getMsrSteNm,
                airQuality -> airQuality,
                (existing, replacement) -> 
                    existing.getMsrDt().isAfter(replacement.getMsrDt()) ? existing : replacement
            ));
        
        return latestByStation.values().stream()
            .sorted((a, b) -> a.getMsrSteNm().compareTo(b.getMsrSteNm()))
            .toList();
    }

    @Override
    public Optional<AirQuality> findLatestByStation(String stationName) {
        return jpaRepository.findTopByMsrSteNmOrderByMsrDtDesc(stationName);
    }

    @Override
    public List<AirQuality> findLatestByRegion(String regionName) {
        List<AirQuality> regionData = jpaRepository.findByMsrRgnNmOrderByMsrDtDesc(regionName);
        
        Map<String, AirQuality> latestByStation = regionData.stream()
            .collect(Collectors.toMap(
                AirQuality::getMsrSteNm,
                airQuality -> airQuality,
                (existing, replacement) -> 
                    existing.getMsrDt().isAfter(replacement.getMsrDt()) ? existing : replacement
            ));
        
        return latestByStation.values().stream()
            .sorted((a, b) -> a.getMsrSteNm().compareTo(b.getMsrSteNm()))
            .toList();
    }

    @Override
    public List<AirQuality> findByPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        return jpaRepository.findByMsrDtBetweenOrderByMsrDtDesc(startTime, endTime);
    }

    @Override
    public List<AirQuality> findByStatus(AirQualityStatus minStatus) {
        Integer minKhaiValue = getMinKhaiValueForStatus(minStatus);
        if (minKhaiValue == null) {
            return List.of();
        }
        return jpaRepository.findByKhaiValueGreaterThanEqualOrderByMsrDtDesc(minKhaiValue);
    }

    @Override
    public List<AirQuality> findByQuery(AirQualityQueryUseCase.AirQualityQuery query) {
        List<AirQuality> results = jpaRepository.findByQuery(
            query.stationName(),
            query.regionName(),
            query.startTime(),
            query.endTime(),
            query.minStatus() != null ? getMinKhaiValueForStatus(query.minStatus()) : null
        );
        
        // 메모리에서 limit과 offset 처리
        if (query.offset() != null && query.limit() != null) {
            int start = query.offset();
            int end = Math.min(start + query.limit(), results.size());
            if (start < results.size()) {
                return results.subList(start, end);
            } else {
                return List.of();
            }
        } else if (query.limit() != null) {
            return results.stream().limit(query.limit()).toList();
        }
        
        return results;
    }

    @Override
    public AirQualityQueryUseCase.AirQualityStatistics getStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        List<AirQuality> data = findByPeriod(startTime, endTime);
        
        if (data.isEmpty()) {
            return new AirQualityQueryUseCase.AirQualityStatistics(
                0.0, 0.0, 0.0, 0, 0, 0, 0, LocalDateTime.now()
            );
        }
        
        double avgPm10 = data.stream()
            .filter(a -> a.getPm10Value() != null)
            .mapToInt(AirQuality::getPm10Value)
            .average()
            .orElse(0.0);
            
        double avgPm25 = data.stream()
            .filter(a -> a.getPm25Value() != null)
            .mapToInt(AirQuality::getPm25Value)
            .average()
            .orElse(0.0);
            
        double avgKhai = data.stream()
            .filter(a -> a.getKhaiValue() != null)
            .mapToInt(AirQuality::getKhaiValue)
            .average()
            .orElse(0.0);
            
        long goodCount = data.stream()
            .filter(a -> a.getKhaiValue() != null && a.getKhaiValue() <= 50)
            .count();
            
        long moderateCount = data.stream()
            .filter(a -> a.getKhaiValue() != null && a.getKhaiValue() > 50 && a.getKhaiValue() <= 100)
            .count();
            
        long unhealthyCount = data.stream()
            .filter(a -> a.getKhaiValue() != null && a.getKhaiValue() > 100)
            .count();
        
        return new AirQualityQueryUseCase.AirQualityStatistics(
            avgPm10, avgPm25, avgKhai, goodCount, moderateCount, unhealthyCount, data.size(), LocalDateTime.now()
        );
    }

    @Override
    public int deleteOldData(LocalDateTime beforeDateTime) {
        return jpaRepository.deleteByMsrDtBefore(beforeDateTime);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countByStation(String stationName) {
        return jpaRepository.countByMsrSteNm(stationName);
    }

    @Override
    public long countByPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        return jpaRepository.countByMsrDtBetween(startTime, endTime);
    }

    /**
     * 대기질 상태에 따른 최소 KHAI 값 반환
     */
    private Integer getMinKhaiValueForStatus(AirQualityStatus status) {
        return switch (status) {
            case GOOD -> 0;
            case MODERATE -> 51;
            case UNHEALTHY_FOR_SENSITIVE -> 101;
            case UNHEALTHY -> 151;
            case VERY_UNHEALTHY -> 201;
            case HAZARDOUS -> 301;
            case UNKNOWN -> null;
        };
    }
}
