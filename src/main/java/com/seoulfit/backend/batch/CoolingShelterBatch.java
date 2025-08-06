package com.seoulfit.backend.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "seoul-fit.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class CoolingShelterBatch implements BatchService{

    @Override
    public void dailyBatch() {

    }
}
