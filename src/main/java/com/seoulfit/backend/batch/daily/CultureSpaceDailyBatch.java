package com.seoulfit.backend.batch.daily;

import com.seoulfit.backend.batch.BatchService;
import com.seoulfit.backend.culture.application.service.CulturalSpaceService;
import com.seoulfit.backend.culture.adapter.in.web.dto.response.SeoulCulturalSpaceApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "seoul-fit.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class CultureSpaceDailyBatch implements BatchService {
    private final CulturalSpaceService culturalSpaceService;

    /**
     * 매일 오전 6시 30분에 문화공간 데이터 동기화
     */
    @Scheduled(cron = "${seoul-fit.scheduler.culture.request.cron[1]}")
    @Override
    public void dailyBatch() {
        log.info("Starting scheduled cultural space synchronization");

        try {
            SeoulCulturalSpaceApiResponse response = culturalSpaceService.saveCultureSpace(1, 1000);
            log.info("Scheduled cultural Space sync completed successfully. Synced count: {}", response.getRow().size());
        } catch (Exception e) {
            log.error("Error during scheduled cultural space synchronization", e);
        }
    }


}
