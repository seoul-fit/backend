package com.seoulfit.backend.publicdata.infrastructure.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.Set;

/**
 * 운영 HTTP 노출 없이 공개 데이터 정기 작업의 실제 Spring 등록 상태를 남긴다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PublicDataScheduledTaskRegistrationLogger {

    private static final Set<String> OBSERVED_TARGETS = Set.of(
            "com.seoulfit.backend.publicdata.env.infrastructure.batch.AirQualityRealTimeBatch",
            "com.seoulfit.backend.publicdata.env.infrastructure.batch.AirQualityDailyBatch",
            "com.seoulfit.backend.publicdata.restaurant.infrastructure.batch.RestaurantDailyBatch");

    private final ScheduledTaskHolder scheduledTaskHolder;

    @EventListener(ApplicationReadyEvent.class)
    public void logRegisteredPublicDataTasks() {
        scheduledTaskHolder.getScheduledTasks().stream()
                .map(ScheduledTask::getTask)
                .filter(CronTask.class::isInstance)
                .map(CronTask.class::cast)
                .filter(task -> task.getRunnable() instanceof ScheduledMethodRunnable)
                .forEach(this::logIfObservedTarget);
    }

    private void logIfObservedTarget(CronTask task) {
        ScheduledMethodRunnable runnable = (ScheduledMethodRunnable) task.getRunnable();
        String targetClassName = ClassUtils.getUserClass(runnable.getTarget()).getName();

        if (OBSERVED_TARGETS.contains(targetClassName)) {
            log.info("정기 작업 등록 확인 - 대상: {}#{}, 크론: {}",
                    targetClassName, runnable.getMethod().getName(), task.getExpression());
        }
    }
}
