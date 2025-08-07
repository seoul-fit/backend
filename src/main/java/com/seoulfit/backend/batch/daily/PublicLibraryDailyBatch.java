package com.seoulfit.backend.batch.daily;

import com.seoulfit.backend.batch.BatchService;
import com.seoulfit.backend.facilities.application.port.in.CommandPublicLibraryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "seoul-fit.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class PublicLibraryDailyBatch implements BatchService {
    private final CommandPublicLibraryUseCase commandPublicLibraryUseCase;

    @Scheduled(cron = "${seoul-fit.scheduler.facilities.public-library.cron}")
    @Override
    public void dailyBatch() {
        commandPublicLibraryUseCase.savePublicLibraryList();
        log.info("Success Public Library Daily Batch");
    }
}
