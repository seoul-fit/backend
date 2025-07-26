package com.seoulfit.backend.application.scheduler;

import com.seoulfit.backend.application.service.CulturalEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "seoul-fit.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class CulturalEventScheduler {

    private final CulturalEventService culturalEventService;

    /**
     * 매일 오전 6시에 문화행사 데이터 동기화
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void syncCulturalEventsDaily() {
        log.info("Starting scheduled cultural events synchronization");
        
        try {
            int syncedCount = culturalEventService.syncCulturalEventsFromApi();
            log.info("Scheduled cultural events sync completed successfully. Synced count: {}", syncedCount);
        } catch (Exception e) {
            log.error("Error during scheduled cultural events synchronization", e);
        }
    }

    /**
     * 매주 월요일 오전 3시에 전체 데이터 재동기화
     */
    @Scheduled(cron = "0 0 3 * * MON")
    public void fullSyncCulturalEventsWeekly() {
        log.info("Starting weekly full cultural events synchronization");
        
        try {
            int syncedCount = culturalEventService.syncCulturalEventsFromApi();
            log.info("Weekly full cultural events sync completed successfully. Synced count: {}", syncedCount);
        } catch (Exception e) {
            log.error("Error during weekly full cultural events synchronization", e);
        }
    }
}
