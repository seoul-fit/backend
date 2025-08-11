package com.seoulfit.backend.scheduler.application.service;

import com.seoulfit.backend.scheduler.application.port.in.ManageSchedulerUseCase;
import com.seoulfit.backend.scheduler.application.port.in.dto.SchedulerExecutionResult;
import com.seoulfit.backend.trigger.application.port.in.ManageTriggerUseCase;
import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationResult;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 스케줄러 서비스
 * <p>
 * 헥사고날 아키텍처의 애플리케이션 서비스 스케줄링 작업과 관련된 비즈니스 로직을 처리
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService implements ManageSchedulerUseCase {

    private final ManageTriggerUseCase manageTriggerUseCase;

    @Override
    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public SchedulerExecutionResult executeRealtimeTriggerScheduler() {
        String schedulerName = "RealtimeTriggerScheduler";
        LocalDateTime startTime = LocalDateTime.now();

        try {
            log.info("실시간 트리거 스케줄러 시작");

            List<TriggerEvaluationResult> results = manageTriggerUseCase.evaluateRealtimeTriggers();
            long triggeredCount = results.stream()
                    .mapToLong(result -> result.isTriggered() ? 1 : 0)
                    .sum();

            long executionTime = java.time.Duration.between(startTime, LocalDateTime.now()).toMillis();

            log.info("실시간 트리거 스케줄러 완료: 평가 결과 수={}, 트리거 발동 수={}, 소요시간={}ms",
                    results.size(), triggeredCount, executionTime);

            return SchedulerExecutionResult.success(
                    schedulerName,
                    startTime,
                    executionTime,
                    results.size(),
                    String.format("트리거 발동 수: %d", triggeredCount)
            );
        } catch (Exception e) {
            log.error("실시간 트리거 스케줄러 실행 실패: error={}", e.getMessage(), e);
            return SchedulerExecutionResult.failure(schedulerName, startTime, e.getMessage());
        }
    }

    @Override
    @Scheduled(fixedRate = 1800000) // 30분마다 실행
    public SchedulerExecutionResult executeCulturalEventTriggerScheduler() {
        String schedulerName = "CulturalEventTriggerScheduler";
        LocalDateTime startTime = LocalDateTime.now();

        try {
            log.info("문화행사 트리거 스케줄러 시작");

            List<TriggerEvaluationResult> results = manageTriggerUseCase.evaluateCulturalEventTriggers();
            long triggeredCount = results.stream()
                    .mapToLong(result -> result.isTriggered() ? 1 : 0)
                    .sum();

            long executionTime = java.time.Duration.between(startTime, LocalDateTime.now()).toMillis();

            log.info("문화행사 트리거 스케줄러 완료: 평가 결과 수={}, 트리거 발동 수={}, 소요시간={}ms",
                    results.size(), triggeredCount, executionTime);

            return SchedulerExecutionResult.success(
                    schedulerName,
                    startTime,
                    executionTime,
                    results.size(),
                    String.format("트리거 발동 수: %d", triggeredCount)
            );
        } catch (Exception e) {
            log.error("문화행사 트리거 스케줄러 실행 실패: error={}", e.getMessage(), e);
            return SchedulerExecutionResult.failure(schedulerName, startTime, e.getMessage());
        }
    }

    @Override
    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    public SchedulerExecutionResult executeDataCollectionScheduler() {
        String schedulerName = "DataCollectionScheduler";
        LocalDateTime startTime = LocalDateTime.now();

        try {
            log.info("데이터 수집 스케줄러 시작");

            // TODO: 공공 데이터 수집 로직 구현
            int collectedCount = 0;

            long executionTime = java.time.Duration.between(startTime, LocalDateTime.now()).toMillis();

            log.info("데이터 수집 스케줄러 완료: 수집 데이터 수={}, 소요시간={}ms",
                    collectedCount, executionTime);

            return SchedulerExecutionResult.success(
                    schedulerName,
                    startTime,
                    executionTime,
                    collectedCount,
                    "데이터 수집 완료"
            );
        } catch (Exception e) {
            log.error("데이터 수집 스케줄러 실행 실패: error={}", e.getMessage(), e);
            return SchedulerExecutionResult.failure(schedulerName, startTime, e.getMessage());
        }
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시 실행
    public SchedulerExecutionResult executeNotificationCleanupScheduler() {
        String schedulerName = "NotificationCleanupScheduler";
        LocalDateTime startTime = LocalDateTime.now();

        try {
            log.info("알림 정리 스케줄러 시작");

            // TODO: 오래된 알림 정리 로직 구현
            int cleanedCount = 0;

            long executionTime = java.time.Duration.between(startTime, LocalDateTime.now()).toMillis();

            log.info("알림 정리 스케줄러 완료: 정리된 알림 수={}, 소요시간={}ms",
                    cleanedCount, executionTime);

            return SchedulerExecutionResult.success(
                    schedulerName,
                    startTime,
                    executionTime,
                    cleanedCount,
                    "알림 정리 완료"
            );
        } catch (Exception e) {
            log.error("알림 정리 스케줄러 실행 실패: error={}", e.getMessage(), e);
            return SchedulerExecutionResult.failure(schedulerName, startTime, e.getMessage());
        }
    }
}
