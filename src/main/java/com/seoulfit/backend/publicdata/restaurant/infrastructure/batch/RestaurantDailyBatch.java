package com.seoulfit.backend.publicdata.restaurant.infrastructure.batch;

import com.seoulfit.backend.publicdata.restaurant.application.port.in.RestaurantBatchUseCase;
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
public class RestaurantDailyBatch {

    private final RestaurantBatchUseCase restaurantBatchUseCase;

    /**
     * 서울시 관광 음식점 정보 일일 배치 처리
     * 매일 새벽 4시에 실행 (cron: "0 0 4 * * ?")
     */
    @Scheduled(cron = "${urbanping.scheduler.restaurant.daily-cron[0]}")
    public void executeTouristRestaurantDailyBatch() {
        log.info("=== 서울시 관광 음식점 정보 일일 배치 시작 ===");
        log.info("서울시 음식점 배치 실행 날짜: {}", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        restaurantBatchUseCase.processDailyBatch();
    }

}
