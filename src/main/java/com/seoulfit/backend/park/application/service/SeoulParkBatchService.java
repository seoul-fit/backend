package com.seoulfit.backend.park.application.service;

import com.seoulfit.backend.park.application.port.in.SeoulParkBatchUseCase;
import com.seoulfit.backend.park.application.port.out.SeoulParkApiClient;
import com.seoulfit.backend.park.application.port.out.SeoulParkRepository;
import com.seoulfit.backend.park.application.port.out.dto.SeoulParkApiResponse;
import com.seoulfit.backend.park.domain.SeoulPark;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 서울시 공원 정보 배치 처리 서비스
 * 헥사고날 아키텍처의 애플리케이션 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SeoulParkBatchService implements SeoulParkBatchUseCase {

    private final SeoulParkApiClient apiClient;
    private final SeoulParkRepository repository;

    @Override
    public SeoulParkBatchResult processDailyBatch(String dataDate) {
        log.info("서울시 공원 정보 일일 배치 처리 시작 - 날짜: {}", dataDate);

        try {
            // 1. API에서 공원 정보 조회
            SeoulParkApiResponse apiResponse = apiClient.fetchAllParkInfo();
            
            if (!apiResponse.isSuccess()) {
                String errorMessage = "API 호출 실패: " + 
                    (apiResponse.getSearchParkInfoService() != null && 
                     apiResponse.getSearchParkInfoService().getResult() != null ? 
                     apiResponse.getSearchParkInfoService().getResult().getMessage() : "Unknown error");
                log.error(errorMessage);
                return SeoulParkBatchResult.failure(dataDate, errorMessage);
            }

            List<SeoulParkApiResponse.ParkInfo> parkInfoList = apiResponse.getParkInfoList();
            log.info("API에서 {} 개의 공원 정보 조회 완료", parkInfoList.size());

            // 2. 데이터 변환 및 저장
            int savedCount = 0;
            int updatedCount = 0;

            for (SeoulParkApiResponse.ParkInfo parkInfo : parkInfoList) {
                try {
                    SeoulPark seoulPark = convertToEntity(parkInfo, dataDate);
                    
                    // 기존 데이터 확인
                    Optional<SeoulPark> existingPark = repository.findByParkIdxAndDataDate(
                        seoulPark.getParkIdx(), dataDate);
                    
                    if (existingPark.isPresent()) {
                        // 기존 데이터 업데이트
                        SeoulPark existing = existingPark.get();
                        existing.update(
                            seoulPark.getParkName(),
                            seoulPark.getDescription(),
                            seoulPark.getArea(),
                            seoulPark.getOpenDate(),
                            seoulPark.getMainEquipment(),
                            seoulPark.getMainPlants(),
                            seoulPark.getGuidance(),
                            seoulPark.getVisitRoad(),
                            seoulPark.getUsageReference(),
                            seoulPark.getImageUrl(),
                            seoulPark.getZone(),
                            seoulPark.getAddress(),
                            seoulPark.getManagementDepartment(),
                            seoulPark.getPhoneNumber(),
                            seoulPark.getGrs80Longitude(),
                            seoulPark.getGrs80Latitude(),
                            seoulPark.getWgs84Longitude(),
                            seoulPark.getWgs84Latitude(),
                            seoulPark.getTemplateUrl()
                        );
                        repository.save(existing);
                        updatedCount++;
                    } else {
                        // 새 데이터 저장
                        repository.save(seoulPark);
                        savedCount++;
                    }
                } catch (Exception e) {
                    log.warn("공원 정보 저장 실패 - 공원번호: {}, 오류: {}", 
                        parkInfo.getPIdx(), e.getMessage());
                }
            }

            // 3. 이전 데이터 정리 (3일 이전 데이터 삭제)
            int deletedCount = cleanupOldData(3);

            log.info("서울시 공원 정보 배치 처리 완료 - 조회: {}, 저장: {}, 업데이트: {}, 삭제: {}", 
                parkInfoList.size(), savedCount, updatedCount, deletedCount);

            return SeoulParkBatchResult.success(dataDate, parkInfoList.size(), 
                savedCount, updatedCount, deletedCount);

        } catch (Exception e) {
            log.error("서울시 공원 정보 배치 처리 실패", e);
            return SeoulParkBatchResult.failure(dataDate, e.getMessage());
        }
    }

    @Override
    public int cleanupOldData(int retentionDays) {
        String cutoffDate = LocalDate.now()
            .minusDays(retentionDays)
            .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        int deletedCount = repository.deleteByDataDateBefore(cutoffDate);
        log.info("{}일 이전 공원 데이터 {} 건 삭제 완료", retentionDays, deletedCount);
        
        return deletedCount;
    }

    /**
     * API 응답 데이터를 엔티티로 변환
     */
    private SeoulPark convertToEntity(SeoulParkApiResponse.ParkInfo parkInfo, String dataDate) {
        return SeoulPark.builder()
            .parkIdx(parkInfo.getPIdx())
            .parkName(parkInfo.getPPark())
            .description(parkInfo.getPListContent())
            .area(parkInfo.getArea())
            .openDate(parkInfo.getOpenDt())
            .mainEquipment(parkInfo.getMainEquip())
            .mainPlants(parkInfo.getMainPlants())
            .guidance(parkInfo.getGuidance())
            .visitRoad(parkInfo.getVisitRoad())
            .usageReference(parkInfo.getUseRefer())
            .imageUrl(parkInfo.getPImg())
            .zone(parkInfo.getPZone())
            .address(parkInfo.getPAddr())
            .managementDepartment(parkInfo.getPName())
            .phoneNumber(parkInfo.getPAdmintel())
            .grs80Longitude(parseDouble(parkInfo.getGLongitude()))
            .grs80Latitude(parseDouble(parkInfo.getGLatitude()))
            .wgs84Longitude(parseDouble(parkInfo.getLongitude()))
            .wgs84Latitude(parseDouble(parkInfo.getLatitude()))
            .templateUrl(parkInfo.getTemplateUrl())
            .dataDate(dataDate)
            .build();
    }

    /**
     * 문자열을 Double로 안전하게 변환
     */
    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty() || "0".equals(value.trim())) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            log.warn("좌표 값 파싱 실패: {}", value);
            return null;
        }
    }
}
