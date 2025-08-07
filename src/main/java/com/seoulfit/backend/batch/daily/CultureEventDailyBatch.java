package com.seoulfit.backend.batch.daily;

import com.seoulfit.backend.batch.BatchService;
import com.seoulfit.backend.culture.application.service.CulturalEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "seoul-fit.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class CultureEventDailyBatch implements BatchService {
    private final CulturalEventService culturalEventService;

    /**
     * 매일 오전 6시에 문화행사 데이터 동기화
     */
    @Scheduled(cron = "${seoul-fit.scheduler.culture.request.cron[0]}")
    @Override
    public void dailyBatch() {
        log.info("Starting scheduled cultural events synchronization");

        try {
            int syncedCount = culturalEventService.saveCultureEvents();
            log.info("Scheduled cultural events sync completed successfully. Synced count: {}", syncedCount);
        } catch (Exception e) {
            log.error("Error during scheduled cultural events synchronization", e);
        }
    }

}
