package com.seoulfit.backend.park.application.service;

import com.seoulfit.backend.park.application.port.in.SeoulParkQueryUseCase;
import com.seoulfit.backend.park.application.port.out.SeoulParkRepository;
import com.seoulfit.backend.park.domain.SeoulPark;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 서울시 공원 정보 조회 서비스
 * 헥사고날 아키텍처의 애플리케이션 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeoulParkQueryService implements SeoulParkQueryUseCase {

    private final SeoulParkRepository repository;

    @Override
    public List<SeoulPark> findParksByDate(String dataDate) {
        log.debug("특정 날짜 공원 정보 조회 - 날짜: {}", dataDate);
        return repository.findByDataDate(dataDate);
    }

    @Override
    public List<SeoulPark> findLatestParks() {
        log.debug("최신 공원 정보 조회");
        List<String> availableDates = repository.findAllDataDatesOrderByDesc();
        
        if (availableDates.isEmpty()) {
            log.warn("사용 가능한 공원 데이터가 없습니다");
            return List.of();
        }
        
        String latestDate = availableDates.get(0);
        log.debug("최신 데이터 날짜: {}", latestDate);
        
        return repository.findByDataDate(latestDate);
    }

    @Override
    public List<SeoulPark> findParksByZone(String zone, String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 공원 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("지역별 공원 정보 조회 - 지역: {}, 날짜: {}", zone, targetDate);
        return repository.findByZoneAndDataDate(zone, targetDate);
    }

    @Override
    public List<SeoulPark> searchParksByName(String parkName, String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 공원 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("공원명 검색 - 검색어: {}, 날짜: {}", parkName, targetDate);
        return repository.findByParkNameContainingAndDataDate(parkName, targetDate);
    }

    @Override
    public Optional<SeoulPark> findParkByIdx(Long parkIdx, String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 공원 데이터가 없습니다");
            return Optional.empty();
        }
        
        log.debug("공원 번호로 조회 - 공원번호: {}, 날짜: {}", parkIdx, targetDate);
        return repository.findByParkIdxAndDataDate(parkIdx, targetDate);
    }

    @Override
    public List<SeoulPark> findParksWithCoordinates(String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 공원 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("좌표 정보가 있는 공원 조회 - 날짜: {}", targetDate);
        return repository.findByDataDateAndWgs84LongitudeIsNotNullAndWgs84LatitudeIsNotNull(targetDate);
    }

    @Override
    public List<String> getAvailableDataDates() {
        log.debug("사용 가능한 데이터 날짜 목록 조회");
        return repository.findAllDataDatesOrderByDesc();
    }

    @Override
    public ParkDataStatistics getParkStatistics(String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 공원 데이터가 없습니다");
            return new ParkDataStatistics(targetDate, 0, 0, List.of(), 0);
        }
        
        log.debug("공원 데이터 통계 조회 - 날짜: {}", targetDate);
        
        List<SeoulPark> allParks = repository.findByDataDate(targetDate);
        long totalParks = allParks.size();
        
        long parksWithCoordinates = allParks.stream()
            .filter(SeoulPark::hasValidCoordinates)
            .count();
        
        List<String> zones = allParks.stream()
            .map(SeoulPark::getZone)
            .filter(zone -> zone != null && !zone.trim().isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        long parksPerZone = zones.isEmpty() ? 0 : totalParks / zones.size();
        
        return new ParkDataStatistics(targetDate, totalParks, parksWithCoordinates, zones, parksPerZone);
    }

    /**
     * 최신 데이터 날짜 조회
     */
    private String getLatestDataDate() {
        List<String> availableDates = repository.findAllDataDatesOrderByDesc();
        return availableDates.isEmpty() ? null : availableDates.get(0);
    }
}
