package com.seoulfit.backend.publicdata.restaurant.infrastructure.batch;

import com.seoulfit.backend.publicdata.restaurant.application.port.in.TouristRestaurantBatchUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 서울시 관광 음식점 정보 일일 배치 작업
 * 매일 새벽 4시에 실행되어 최신 음식점 정보를 수집하고 저장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TouristRestaurantDailyBatch {

    private final TouristRestaurantBatchUseCase touristRestaurantBatchUseCase;

    /**
     * 서울시 관광 음식점 정보 일일 배치 처리
     * 매일 새벽 4시에 실행 (cron: "0 0 4 * * ?")
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void executeTouristRestaurantDailyBatch() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        log.info("=== 서울시 관광 음식점 정보 일일 배치 시작 ===");
        log.info("배치 실행 날짜: {}", today);

        try {
            TouristRestaurantBatchUseCase.TouristRestaurantBatchResult result = 
                touristRestaurantBatchUseCase.processDailyBatch(today);

            if (result.success()) {
                log.info("=== 서울시 관광 음식점 정보 일일 배치 성공 ===");
                log.info("처리 결과 - 조회: {}, 저장: {}, 업데이트: {}, 삭제: {}", 
                    result.totalFetched(), result.totalSaved(), 
                    result.totalUpdated(), result.totalDeleted());
            } else {
                log.error("=== 서울시 관광 음식점 정보 일일 배치 실패 ===");
                log.error("실패 사유: {}", result.errorMessage());
            }

        } catch (Exception e) {
            log.error("=== 서울시 관광 음식점 정보 일일 배치 예외 발생 ===", e);
        }
    }

    /**
     * 수동 배치 실행 (테스트용)
     * 필요시 수동으로 배치를 실행할 수 있는 메서드
     */
    public TouristRestaurantBatchUseCase.TouristRestaurantBatchResult executeManualBatch() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        log.info("=== 서울시 관광 음식점 정보 수동 배치 시작 ===");
        log.info("배치 실행 날짜: {}", today);

        return touristRestaurantBatchUseCase.processDailyBatch(today);
    }

    /**
     * 특정 날짜로 배치 실행 (테스트용)
     */
    public TouristRestaurantBatchUseCase.TouristRestaurantBatchResult executeManualBatch(String dataDate) {
        log.info("=== 서울시 관광 음식점 정보 수동 배치 시작 (특정 날짜) ===");
        log.info("배치 실행 날짜: {}", dataDate);

        return touristRestaurantBatchUseCase.processDailyBatch(dataDate);
    }
}
