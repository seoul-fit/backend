package com.seoulfit.backend.publicdata.env.application.port.in;

import java.time.LocalDateTime;

/**
 * 대기질 정보 배치 처리 Use Case
 */
public interface AirQualityBatchUseCase {

    /**
     * 5분 단위 대기질 정보 배치 처리
     */
    AirQualityBatchResult processRealTimeBatch();

    /**
     * 일일 대기질 정보 배치 처리
     */
    AirQualityBatchResult processDailyBatch(String dataDate);

    /**
     * 특정 시간대 대기질 정보 배치 처리
     */
    AirQualityBatchResult processTimeBatch(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 배치 처리 결과
     */
    record AirQualityBatchResult(
        boolean success,
        int totalFetched,
        int totalSaved,
        int totalUpdated,
        int totalSkipped,
        String errorMessage,
        LocalDateTime processedAt
    ) {
        public static AirQualityBatchResult success(int totalFetched, int totalSaved, 
                                                   int totalUpdated, int totalSkipped) {
            return new AirQualityBatchResult(
                true, totalFetched, totalSaved, totalUpdated, totalSkipped, 
                null, LocalDateTime.now()
            );
        }

        public static AirQualityBatchResult failure(String errorMessage) {
            return new AirQualityBatchResult(
                false, 0, 0, 0, 0, errorMessage, LocalDateTime.now()
            );
        }
    }
}
