package com.seoulfit.backend.batch.daily;

import com.seoulfit.backend.batch.BatchService;
import com.seoulfit.backend.facilities.application.port.in.CommandCoolingShelterUseCase;
import com.seoulfit.backend.facilities.domain.CoolingShelter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "seoul-fit.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class CoolingShelterDailyBatch implements BatchService {
    private final CommandCoolingShelterUseCase commandCoolingShelterUseCase;

    @Scheduled(cron = "${seoul-fit.scheduler.facilities.cooling-shelter.cron}")
    @Override
    public void dailyBatch() {
        CommandCoolingShelterUseCase.GetAmenitiesQuery query =
                new CommandCoolingShelterUseCase.GetAmenitiesQuery(1, 1000, "", "");
        List<CoolingShelter> coolingShelter = commandCoolingShelterUseCase.saveCoolingShelter(query);

        log.info("CoolingShelter Batch Success: {}", coolingShelter.size());
    }

}
