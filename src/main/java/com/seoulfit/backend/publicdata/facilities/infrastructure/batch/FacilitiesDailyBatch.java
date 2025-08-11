package com.seoulfit.backend.publicdata.facilities.infrastructure.batch;

import com.seoulfit.backend.publicdata.facilities.application.port.in.CommandCoolingShelterUseCase;
import com.seoulfit.backend.publicdata.facilities.application.port.in.CommandPublicLibraryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class FacilitiesDailyBatch {
    private final CommandCoolingShelterUseCase coolingShelterUseCase;
    private final CommandPublicLibraryUseCase libraryUseCase;


    @Scheduled(cron = "${urbanping.scheduler.facilities.daily-cron[0]}")
    public void coolingShelterDailyBatch() {
        coolingShelterUseCase.saveCoolingShelter(new CommandCoolingShelterUseCase.GetAmenitiesQuery(
                        1, 1000, "", ""
                )
        );
    }

    @Scheduled(cron = "${urbanping.scheduler.facilities.daily-cron[1]}")
    public void libraryDailyBatch() {
        libraryUseCase.savePublicLibraryList();
    }
}
