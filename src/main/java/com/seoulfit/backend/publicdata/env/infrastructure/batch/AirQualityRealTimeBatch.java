package com.seoulfit.backend.publicdata.env.infrastructure.batch;

import com.seoulfit.backend.publicdata.env.application.port.in.AirQualityBatchUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 서울시 대기질 정보 실시간 배치 작업
 * 5분마다 실행되어 최신 대기질 정보를 수집하고 저장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AirQualityRealTimeBatch {

    private final AirQualityBatchUseCase airQualityBatchUseCase;

    //@Scheduled(cron = "0 */5 * * * ?")
    public void executeRealTimeBatch() {
        LocalDateTime now = LocalDateTime.now();
        String currentTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        log.info("=== 서울시 대기질 정보 실시간 배치 시작 ===");
        log.info("배치 실행 시간: {}", currentTime);

        try {
            AirQualityBatchUseCase.AirQualityBatchResult result = 
                airQualityBatchUseCase.processRealTimeBatch();

            if (result.success()) {
                log.info("=== 서울시 대기질 정보 실시간 배치 성공 ===");
                log.info("처리 결과 - 조회: {}, 저장: {}, 업데이트: {}, 스킵: {}", 
                    result.totalFetched(), result.totalSaved(), 
                    result.totalUpdated(), result.totalSkipped());
                
                // 알림이 필요한 상황 체크
                if (result.totalSaved() > 0 || result.totalUpdated() > 0) {
                    log.info("새로운 대기질 데이터가 업데이트되었습니다. 알림 시스템에 전달합니다.");
                    // TODO: 이벤트 발행하여 알림 시스템에 전달
                }
            } else {
                log.error("=== 서울시 대기질 정보 실시간 배치 실패 ===");
                log.error("실패 사유: {}", result.errorMessage());
            }

        } catch (Exception e) {
            log.error("=== 서울시 대기질 정보 실시간 배치 예외 발생 ===", e);
        }
    }

    /**
     * 수동 실시간 배치 실행 (테스트용)
     */
    public AirQualityBatchUseCase.AirQualityBatchResult executeManualRealTimeBatch() {
        log.info("=== 서울시 대기질 정보 수동 실시간 배치 시작 ===");
        return airQualityBatchUseCase.processRealTimeBatch();
    }

    /**
     * 특정 시간대 배치 실행 (복구용)
     */
    public AirQualityBatchUseCase.AirQualityBatchResult executeTimeBatch(
            LocalDateTime startTime, LocalDateTime endTime) {
        log.info("=== 서울시 대기질 정보 특정 시간대 배치 시작 ===");
        log.info("시간 범위: {} ~ {}", startTime, endTime);
        return airQualityBatchUseCase.processTimeBatch(startTime, endTime);
    }
}
