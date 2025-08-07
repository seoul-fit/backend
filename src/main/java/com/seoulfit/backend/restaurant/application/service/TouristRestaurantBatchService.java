package com.seoulfit.backend.restaurant.application.service;

import com.seoulfit.backend.restaurant.application.port.in.TouristRestaurantBatchUseCase;
import com.seoulfit.backend.restaurant.application.port.out.TouristRestaurantApiClient;
import com.seoulfit.backend.restaurant.application.port.out.TouristRestaurantRepository;
import com.seoulfit.backend.restaurant.application.port.out.dto.TouristRestaurantApiResponse;
import com.seoulfit.backend.restaurant.domain.TouristRestaurant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 서울시 관광 음식점 정보 배치 처리 서비스
 * 헥사고날 아키텍처의 애플리케이션 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TouristRestaurantBatchService implements TouristRestaurantBatchUseCase {

    private final TouristRestaurantApiClient apiClient;
    private final TouristRestaurantRepository repository;

    @Override
    public TouristRestaurantBatchResult processDailyBatch(String dataDate) {
        log.info("서울시 관광 음식점 정보 일일 배치 처리 시작 - 날짜: {}", dataDate);

        try {
            // 1. API에서 음식점 정보 조회
            TouristRestaurantApiResponse apiResponse = apiClient.fetchAllRestaurantInfo();
            
            if (!apiResponse.isSuccess()) {
                String errorMessage = "API 호출 실패: " + 
                    (apiResponse.getTbVwRestaurants() != null && 
                     apiResponse.getTbVwRestaurants().getResult() != null ? 
                     apiResponse.getTbVwRestaurants().getResult().getMessage() : "Unknown error");
                log.error(errorMessage);
                return TouristRestaurantBatchResult.failure(dataDate, errorMessage);
            }

            List<TouristRestaurantApiResponse.RestaurantInfo> restaurantInfoList = apiResponse.getRestaurantInfoList();
            log.info("API에서 {} 개의 음식점 정보 조회 완료", restaurantInfoList.size());

            // 2. 데이터 변환 및 저장
            int savedCount = 0;
            int updatedCount = 0;

            for (TouristRestaurantApiResponse.RestaurantInfo restaurantInfo : restaurantInfoList) {
                try {
                    TouristRestaurant restaurant = convertToEntity(restaurantInfo, dataDate);
                    
                    // 기존 데이터 확인 (고유번호와 날짜로)
                    Optional<TouristRestaurant> existingRestaurant = repository.findByPostSnAndDataDate(
                        restaurant.getPostSn(), dataDate);
                    
                    if (existingRestaurant.isPresent()) {
                        // 기존 데이터 업데이트
                        TouristRestaurant existing = existingRestaurant.get();
                        existing.update(
                            restaurant.getLangCodeId(),
                            restaurant.getRestaurantName(),
                            restaurant.getPostUrl(),
                            restaurant.getAddress(),
                            restaurant.getNewAddress(),
                            restaurant.getPhoneNumber(),
                            restaurant.getWebsiteUrl(),
                            restaurant.getOperatingHours(),
                            restaurant.getSubwayInfo(),
                            restaurant.getHomepageLanguage(),
                            restaurant.getRepresentativeMenu()
                        );
                        repository.save(existing);
                        updatedCount++;
                    } else {
                        // 새 데이터 저장
                        repository.save(restaurant);
                        savedCount++;
                    }
                } catch (Exception e) {
                    log.warn("음식점 정보 저장 실패 - 고유번호: {}, 오류: {}", 
                        restaurantInfo.getPostSn(), e.getMessage());
                }
            }

            // 3. 이전 데이터 정리 (3일 이전 데이터 삭제)
            int deletedCount = cleanupOldData(3);

            log.info("서울시 관광 음식점 정보 배치 처리 완료 - 조회: {}, 저장: {}, 업데이트: {}, 삭제: {}", 
                restaurantInfoList.size(), savedCount, updatedCount, deletedCount);

            return TouristRestaurantBatchResult.success(dataDate, restaurantInfoList.size(), 
                savedCount, updatedCount, deletedCount);

        } catch (Exception e) {
            log.error("서울시 관광 음식점 정보 배치 처리 실패", e);
            return TouristRestaurantBatchResult.failure(dataDate, e.getMessage());
        }
    }

    @Override
    public int cleanupOldData(int retentionDays) {
        String cutoffDate = LocalDate.now()
            .minusDays(retentionDays)
            .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        int deletedCount = repository.deleteByDataDateBefore(cutoffDate);
        log.info("{}일 이전 음식점 데이터 {} 건 삭제 완료", retentionDays, deletedCount);
        
        return deletedCount;
    }

    /**
     * API 응답 데이터를 엔티티로 변환
     */
    private TouristRestaurant convertToEntity(TouristRestaurantApiResponse.RestaurantInfo restaurantInfo, String dataDate) {
        return TouristRestaurant.builder()
            .postSn(restaurantInfo.getPostSn())
            .langCodeId(restaurantInfo.getLangCodeId())
            .restaurantName(restaurantInfo.getPostSj())
            .postUrl(restaurantInfo.getPostUrl())
            .address(restaurantInfo.getAddress())
            .newAddress(restaurantInfo.getNewAddress())
            .phoneNumber(restaurantInfo.getCmmnTelno())
            .websiteUrl(restaurantInfo.getCmmnHmpgUrl())
            .operatingHours(restaurantInfo.getCmmnUseTime())
            .subwayInfo(restaurantInfo.getSubwayInfo())
            .homepageLanguage(restaurantInfo.getCmmnHmpgLang())
            .representativeMenu(restaurantInfo.getFdReprsntMenu())
            .dataDate(dataDate)
            .build();
    }
}
