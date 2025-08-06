package com.seoulfit.backend.batch;

import com.seoulfit.backend.tmp.application.service.impl.CulturalSpaceServiceImpl;
import com.seoulfit.backend.tmp.presentation.culture.dtos.response.SeoulCulturalSpaceApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "seoul-fit.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class CultureSpaceBatch {
    private final CulturalSpaceServiceImpl culturalSpaceService;

    /**
     * 매일 오전 6시 30분에 문화공간 데이터 동기화
     */
    @Scheduled(cron = "${seoul-fit.scheduler.culture.request.cron[1]}")
    public void syncCulturalEventsDaily() {
        log.info("Starting scheduled cultural space synchronization");

        try {
            SeoulCulturalSpaceApiResponse response = culturalSpaceService.saveCultureSpace(1, 1000);
            log.info("Scheduled cultural Space sync completed successfully. Synced count: {}", response.getRow().size());
        } catch (Exception e) {
            log.error("Error during scheduled cultural space synchronization", e);
        }
    }
}
