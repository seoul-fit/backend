package com.seoulfit.backend.batch.realtime;

import com.seoulfit.backend.batch.BatchService;
import com.seoulfit.backend.external.PublicDataApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "seoul-fit.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class BikeRealTimeBatch implements BatchService {
    private final PublicDataApiClient publicDataApiClient;

    @Scheduled(cron = "${seoul-fit.scheduler.facilities.cooling-shelter.cron}")
    @Override
    public void realTimeBatch() {
        Mono<Map<String, Object>> bikeData = publicDataApiClient.getBikeData(1, 1000);

        log.info("realTimeBatch bikeData: {}", bikeData);

    }
}
