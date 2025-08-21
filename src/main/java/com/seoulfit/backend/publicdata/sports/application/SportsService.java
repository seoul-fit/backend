package com.seoulfit.backend.publicdata.sports.application;

import com.seoulfit.backend.publicdata.sports.domain.Sports;
import com.seoulfit.backend.publicdata.sports.infrastructure.SportsApiClient;
import com.seoulfit.backend.publicdata.sports.infrastructure.SportsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 체육시설 서비스
 * 
 * 체육시설 정보의 조회, 저장, 업데이트 등의 비즈니스 로직을 처리합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SportsService {

    private final SportsRepository sportsRepository;
    private final SportsApiClient sportsApiClient;

    /**
     * 서울시 API에서 체육시설 데이터를 조회하여 DB에 저장
     * 
     * @return 저장된 체육시설 수
     */
    public int syncSportsData() {
        log.info("체육시설 데이터 동기화 시작");

        List<SportsApiClient.SportsApiDto> apiData = sportsApiClient.getSportsData();
        int savedCount = 0;

        for (SportsApiClient.SportsApiDto dto : apiData) {
            try {
                // 시설명이 null이거나 비어있으면 스킵
                if (dto.getFacilityName() == null || dto.getFacilityName().trim().isEmpty()) {
                    log.warn("시설명이 없는 데이터 스킵: {}", dto);
                    continue;
                }

                // 개별 트랜잭션으로 처리
                boolean saved = saveSingleSportsData(dto);
                if (saved) {
                    savedCount++;
                }

            } catch (Exception e) {
                log.error("체육시설 데이터 처리 실패: {} - {}", dto.getFacilityName(), e.getMessage());
            }
        }

        log.info("체육시설 데이터 동기화 완료: {} 건 저장", savedCount);
        return savedCount;
    }

    /**
     * 단일 체육시설 데이터를 개별 트랜잭션으로 저장
     * 
     * @param dto 체육시설 데이터 DTO
     * @return 저장 성공 여부
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean saveSingleSportsData(SportsApiClient.SportsApiDto dto) {
        try {
            // 기존 데이터 확인 (시설명으로 중복 체크)
            Optional<Sports> existingSports = sportsRepository.findByFacilityName(dto.getFacilityName());

            if (existingSports.isPresent()) {
                // 기존 데이터 업데이트
                Sports sports = existingSports.get();
                updateSportsInfo(sports, dto);
                sportsRepository.save(sports);
                log.debug("체육시설 정보 업데이트: {}", dto.getFacilityName());
                return false; // 업데이트는 새로 저장한 것이 아니므로 false
            } else {
                // 새로운 데이터 저장
                Sports newSports = createSportsFromDto(dto);
                Sports savedSports = sportsRepository.save(newSports);
                log.debug("새로운 체육시설 저장: {} (ID: {})", dto.getFacilityName(), savedSports.getId());
                return true;
            }
        } catch (Exception e) {
            log.error("체육시설 개별 저장 실패: {} - {}", dto.getFacilityName(), e.getMessage());
            return false;
        }
    }


    /**
     * 모든 체육시설 조회
     */
    public List<Sports> getAllSports() {
        return sportsRepository.findAll();
    }

    /**
     * 시설명으로 체육시설 조회
     */
    public Optional<Sports> getSportsByName(String facilityName) {
        return sportsRepository.findByFacilityName(facilityName);
    }

    /**
     * 시설유형으로 체육시설 목록 조회
     */
    public List<Sports> getSportsByType(String facilityType) {
        return sportsRepository.findByFacilityType(facilityType);
    }

    /**
     * 자치구로 체육시설 목록 조회
     */
    public List<Sports> getSportsByDistrict(String district) {
        return sportsRepository.findByDistrict(district);
    }

    /**
     * 키워드로 체육시설 검색
     */
    public List<Sports> searchSportsByKeyword(String keyword) {
        return sportsRepository.findByFacilityNameContainingIgnoreCase(keyword);
    }

    /**
     * 위치 기반 체육시설 조회
     */
    public List<Sports> getSportsNearby(double latitude, double longitude, double radiusKm) {
        return sportsRepository.findByLocationWithinRadius(latitude, longitude, radiusKm);
    }

    /**
     * 체육시설 통계 정보 조회
     */
    public SportsStats getSportsStats() {
        long totalCount = sportsRepository.countAllSports();
        long withLocationCount = sportsRepository.countSportsWithLocation();
        
        return SportsStats.builder()
                .totalCount(totalCount)
                .withLocationCount(withLocationCount)
                .withoutLocationCount(totalCount - withLocationCount)
                .build();
    }

    /**
     * DTO에서 Sports 엔티티 생성
     */
    private Sports createSportsFromDto(SportsApiClient.SportsApiDto dto) {
        return Sports.builder()
                .facilityName(dto.getFacilityName())
                .facilityType(dto.getFacilityType())
                .address(dto.getAddress())
                .phoneNumber(dto.getPhoneNumber())
                .operatingHours(dto.getOperatingHours())
                .holiday(dto.getHoliday())
                .feeInfo(dto.getFeeInfo())
                .homepageUrl(dto.getHomepageUrl())
                .district(dto.getDistrict())
                .facilityScale(dto.getFacilityScale())
                .parkingInfo(dto.getParkingInfo())
                .detailContent(dto.getDetailContent())
                .imageUrl(dto.getImageUrl())
                .xCoordinate(dto.getXCoordinate())
                .yCoordinate(dto.getYCoordinate())
                .serviceId(dto.getServiceId())
                .build();
    }

    /**
     * 기존 Sports 엔티티 정보 업데이트
     */
    private void updateSportsInfo(Sports sports, SportsApiClient.SportsApiDto dto) {
        sports.updateInfo(
                dto.getFacilityName(),
                dto.getFacilityType(),
                dto.getAddress(),
                dto.getPhoneNumber(),
                dto.getOperatingHours(),
                dto.getHoliday(),
                dto.getFeeInfo(),
                dto.getHomepageUrl(),
                dto.getDistrict(),
                dto.getFacilityScale(),
                dto.getParkingInfo(),
                dto.getDetailContent(),
                dto.getImageUrl(),
                dto.getXCoordinate(),
                dto.getYCoordinate(),
                dto.getServiceId()
        );
    }

    /**
     * 체육시설 통계 정보 DTO
     */
    public static class SportsStats {
        private long totalCount;
        private long withLocationCount;
        private long withoutLocationCount;

        public static SportsStatsBuilder builder() {
            return new SportsStatsBuilder();
        }

        public static class SportsStatsBuilder {
            private long totalCount;
            private long withLocationCount;
            private long withoutLocationCount;

            public SportsStatsBuilder totalCount(long totalCount) {
                this.totalCount = totalCount;
                return this;
            }

            public SportsStatsBuilder withLocationCount(long withLocationCount) {
                this.withLocationCount = withLocationCount;
                return this;
            }

            public SportsStatsBuilder withoutLocationCount(long withoutLocationCount) {
                this.withoutLocationCount = withoutLocationCount;
                return this;
            }

            public SportsStats build() {
                SportsStats stats = new SportsStats();
                stats.totalCount = this.totalCount;
                stats.withLocationCount = this.withLocationCount;
                stats.withoutLocationCount = this.withoutLocationCount;
                return stats;
            }
        }

        // Getters
        public long getTotalCount() { return totalCount; }
        public long getWithLocationCount() { return withLocationCount; }
        public long getWithoutLocationCount() { return withoutLocationCount; }
    }
}
