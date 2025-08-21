package com.seoulfit.backend.publicdata.culture.infrastructure.batch;

import com.seoulfit.backend.publicdata.culture.application.service.CulturalEventService;
import com.seoulfit.backend.publicdata.culture.application.service.CulturalReservationService;
import com.seoulfit.backend.publicdata.culture.application.service.CulturalSpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CultureDailyBatch {
    private final CulturalEventService culturalEventService;
    private final CulturalReservationService culturalReservationService;
    private final CulturalSpaceService culturalSpaceService;

    @Scheduled(cron = "${seoulfit.scheduler.culture.daily-cron[0]}")
    public void cultureEventDailyBatch() {
        culturalEventService.saveCultureEvents();
    }

    @Scheduled(cron = "${seoulfit.scheduler.culture.daily-cron[1]}")
    public void cultureSpaceDailyBatch() {
        culturalSpaceService.saveCultureSpace(1,1000); //최대 1000건 제한
    }

    @Scheduled(cron = "${seoulfit.scheduler.culture.daily-cron[2]}")
    public void cultureReservationDailyBatch() {
        int count = culturalReservationService.saveCulturalReservation(1, 1000);
        log.info("Count : {}", count);
    }

}
