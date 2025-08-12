package com.seoulfit.backend.publicdata.park.infrastructure.batch;

import com.seoulfit.backend.publicdata.park.application.port.in.ParkBatchUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 서울시 공원 정보 일일 배치 작업
 * 매일 새벽 3시 30분에 실행되어 최신 공원 정보를 수집하고 저장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ParkDailyBatch {
   private final ParkBatchUseCase parkBatchUseCase;

    @Scheduled(cron = "${urbanping.scheduler.park.daily-cron[0]}")
    public void parkDailyBatch() {
        parkBatchUseCase.processDailyBatch();
    }

}
