package com.seoulfit.backend.publicdata.facilities.infrastructure.batch;

import com.seoulfit.backend.publicdata.facilities.application.port.in.SportsFacilityProgramBatchUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 서울시 공공체육시설 프로그램 정보 일일 배치 작업
 * 매일 새벽 3시에 실행되어 최신 프로그램 정보를 수집하고 저장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SportsFacilityProgramDailyBatch {

    private final SportsFacilityProgramBatchUseCase sportsFacilityProgramBatchUseCase;

    /**
     * 서울시 공공체육시설 프로그램 정보 일일 배치 처리
     * 매일 새벽 3시에 실행 (cron: "0 0 3 * * ?")
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void executeSportsFacilityProgramDailyBatch() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        log.info("=== 서울시 공공체육시설 프로그램 정보 일일 배치 시작 ===");
        log.info("배치 실행 날짜: {}", today);

        try {
            SportsFacilityProgramBatchUseCase.SportsFacilityProgramBatchResult result = 
                sportsFacilityProgramBatchUseCase.processDailyBatch(today);

            if (result.success()) {
                log.info("=== 서울시 공공체육시설 프로그램 정보 일일 배치 성공 ===");
                log.info("처리 결과 - 조회: {}, 저장: {}, 업데이트: {}, 삭제: {}", 
                    result.totalFetched(), result.totalSaved(), 
                    result.totalUpdated(), result.totalDeleted());
            } else {
                log.error("=== 서울시 공공체육시설 프로그램 정보 일일 배치 실패 ===");
                log.error("실패 사유: {}", result.errorMessage());
            }

        } catch (Exception e) {
            log.error("=== 서울시 공공체육시설 프로그램 정보 일일 배치 예외 발생 ===", e);
        }
    }

    /**
     * 수동 배치 실행 (테스트용)
     * 필요시 수동으로 배치를 실행할 수 있는 메서드
     */
    public SportsFacilityProgramBatchUseCase.SportsFacilityProgramBatchResult executeManualBatch() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        log.info("=== 서울시 공공체육시설 프로그램 정보 수동 배치 시작 ===");
        log.info("배치 실행 날짜: {}", today);

        return sportsFacilityProgramBatchUseCase.processDailyBatch(today);
    }

    /**
     * 특정 날짜로 배치 실행 (테스트용)
     */
    public SportsFacilityProgramBatchUseCase.SportsFacilityProgramBatchResult executeManualBatch(String dataDate) {
        log.info("=== 서울시 공공체육시설 프로그램 정보 수동 배치 시작 (특정 날짜) ===");
        log.info("배치 실행 날짜: {}", dataDate);

        return sportsFacilityProgramBatchUseCase.processDailyBatch(dataDate);
    }

    /**
     * 특정 종목 배치 실행 (테스트용)
     */
    public SportsFacilityProgramBatchUseCase.SportsFacilityProgramBatchResult executeManualBatchBySubject(String dataDate, String subjectName) {
        log.info("=== 서울시 공공체육시설 프로그램 정보 종목별 수동 배치 시작 ===");
        log.info("배치 실행 날짜: {}, 종목: {}", dataDate, subjectName);

        return sportsFacilityProgramBatchUseCase.processBatchBySubject(dataDate, subjectName);
    }
}
