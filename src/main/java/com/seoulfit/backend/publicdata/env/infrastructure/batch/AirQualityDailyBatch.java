package com.seoulfit.backend.publicdata.env.infrastructure.batch;

import com.seoulfit.backend.publicdata.env.application.port.in.AirQualityBatchUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 서울시 대기질 측정 데이터 보관 정리 배치 작업
 * 매일 새벽에 보관 기간을 초과한 측정 데이터를 정리한다.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "seoulfit.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class AirQualityDailyBatch {

    private final AirQualityBatchUseCase airQualityBatchUseCase;
    private final int retentionDays;

    public AirQualityDailyBatch(
            AirQualityBatchUseCase airQualityBatchUseCase,
            @Value("${seoul-api.v1.environment.retention-days}") int retentionDays) {
        this.airQualityBatchUseCase = airQualityBatchUseCase;
        this.retentionDays = retentionDays;
    }

    /**
     * 서울시 대기질 보관 정리 처리
     * 매일 새벽 3시 30분에 실행한다.
     */
    @Scheduled(cron = "${seoulfit.scheduler.env-daily-cron}")
    public void cleanupOldAirQualityData() {
        log.info("=== 서울시 대기질 보관 정리 시작 ===");
        try {
            int deletedCount = airQualityBatchUseCase.cleanupOldData(retentionDays);
            log.info("=== 서울시 대기질 보관 정리 완료 - 삭제: {}건 ===", deletedCount);
        } catch (Exception e) {
            log.error("=== 서울시 대기질 보관 정리 예외 발생 ===", e);
        }
    }
}
