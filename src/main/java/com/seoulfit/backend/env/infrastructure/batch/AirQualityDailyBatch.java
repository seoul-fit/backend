package com.seoulfit.backend.env.infrastructure.batch;

import com.seoulfit.backend.env.application.port.in.AirQualityBatchUseCase;
import com.seoulfit.backend.env.application.port.out.AirQualityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 서울시 대기질 정보 일일 배치 작업
 * 매일 새벽 3시에 실행되어 전체 대기질 정보를 수집하고 오래된 데이터를 정리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AirQualityDailyBatch {

    private final AirQualityBatchUseCase airQualityBatchUseCase;
    private final AirQualityRepository airQualityRepository;

    /**
     * 서울시 대기질 정보 일일 배치 처리
     * 매일 새벽 3시에 실행 (cron: "0 0 3 * * ?")
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void executeDailyBatch() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        log.info("=== 서울시 대기질 정보 일일 배치 시작 ===");
        log.info("배치 실행 날짜: {}", today);

        try {
            // 1. 일일 배치 처리
            AirQualityBatchUseCase.AirQualityBatchResult result = 
                airQualityBatchUseCase.processDailyBatch(today);

            if (result.success()) {
                log.info("=== 서울시 대기질 정보 일일 배치 성공 ===");
                log.info("처리 결과 - 조회: {}, 저장: {}, 업데이트: {}, 스킵: {}", 
                    result.totalFetched(), result.totalSaved(), 
                    result.totalUpdated(), result.totalSkipped());

                // 2. 오래된 데이터 정리 (30일 이전 데이터 삭제)
                cleanupOldData();

                // 3. 데이터베이스 통계 로깅
                logDatabaseStatistics();

            } else {
                log.error("=== 서울시 대기질 정보 일일 배치 실패 ===");
                log.error("실패 사유: {}", result.errorMessage());
            }

        } catch (Exception e) {
            log.error("=== 서울시 대기질 정보 일일 배치 예외 발생 ===", e);
        }
    }

    /**
     * 주간 통계 배치 (매주 일요일 새벽 2시)
     */
    @Scheduled(cron = "0 0 2 * * SUN")
    public void executeWeeklyStatistics() {
        log.info("=== 서울시 대기질 정보 주간 통계 배치 시작 ===");

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneWeekAgo = now.minusDays(7);

            var statistics = airQualityRepository.getStatistics(oneWeekAgo, now);

            log.info("=== 주간 대기질 통계 (최근 7일) ===");
            log.info("평균 PM10: {:.2f} ㎍/㎥", statistics.avgPm10());
            log.info("평균 PM2.5: {:.2f} ㎍/㎥", statistics.avgPm25());
            log.info("평균 통합대기환경지수: {:.2f}", statistics.avgKhai());
            log.info("좋음: {}회, 보통: {}회, 나쁨 이상: {}회",
                    statistics.goodCount(), statistics.moderateCount(), statistics.unhealthyCount());
            log.info("전체 측정 횟수: {}", statistics.totalCount());

        } catch (Exception e) {
            log.error("주간 통계 배치 중 오류 발생", e);
        }
    }

    /**
     * 수동 일일 배치 실행 (테스트용)
     */
    public AirQualityBatchUseCase.AirQualityBatchResult executeManualDailyBatch() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        log.info("=== 서울시 대기질 정보 수동 일일 배치 시작 ===");
        log.info("배치 실행 날짜: {}", today);

        return airQualityBatchUseCase.processDailyBatch(today);
    }

    /**
     * 특정 날짜로 일일 배치 실행 (복구용)
     */
    public AirQualityBatchUseCase.AirQualityBatchResult executeManualDailyBatch(String dataDate) {
        log.info("=== 서울시 대기질 정보 수동 일일 배치 시작 (특정 날짜) ===");
        log.info("배치 실행 날짜: {}", dataDate);

        return airQualityBatchUseCase.processDailyBatch(dataDate);
    }

    /**
     * 오래된 데이터 정리
     */
    private void cleanupOldData() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            int deletedCount = airQualityRepository.deleteOldData(cutoffDate);
            
            if (deletedCount > 0) {
                log.info("오래된 대기질 데이터 정리 완료 - 삭제된 데이터 수: {}, 기준 날짜: {}", 
                    deletedCount, cutoffDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                log.debug("정리할 오래된 대기질 데이터가 없습니다.");
            }
        } catch (Exception e) {
            log.error("오래된 대기질 데이터 정리 중 오류 발생", e);
        }
    }

    /**
     * 데이터베이스 통계 로깅
     */
    private void logDatabaseStatistics() {
        try {
            long totalCount = airQualityRepository.count();
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneDayAgo = now.minusDays(1);
            LocalDateTime oneWeekAgo = now.minusDays(7);
            
            long oneDayCount = airQualityRepository.countByPeriod(oneDayAgo, now);
            long oneWeekCount = airQualityRepository.countByPeriod(oneWeekAgo, now);
            
            log.info("=== 대기질 데이터베이스 통계 ===");
            log.info("전체 데이터 수: {}", totalCount);
            log.info("최근 1일 데이터 수: {}", oneDayCount);
            log.info("최근 7일 데이터 수: {}", oneWeekCount);
            
        } catch (Exception e) {
            log.error("데이터베이스 통계 조회 중 오류 발생", e);
        }
    }

}
