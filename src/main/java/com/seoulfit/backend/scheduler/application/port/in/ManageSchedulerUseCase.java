package com.seoulfit.backend.scheduler.application.port.in;

import com.seoulfit.backend.scheduler.application.port.in.dto.SchedulerExecutionResult;

/**
 * 스케줄러 관리 Use Case
 * <p>
 * 헥사고날 아키텍처의 입력 포트
 * 스케줄링 작업과 관련된 비즈니스 로직을 정의
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
public interface ManageSchedulerUseCase {

    /**
     * 실시간 트리거 스케줄러 실행
     *
     * @return 스케줄러 실행 결과
     */
    SchedulerExecutionResult executeRealtimeTriggerScheduler();

    /**
     * 문화행사 트리거 스케줄러 실행
     *
     * @return 스케줄러 실행 결과
     */
    SchedulerExecutionResult executeCulturalEventTriggerScheduler();

    /**
     * 데이터 수집 스케줄러 실행
     *
     * @return 스케줄러 실행 결과
     */
    SchedulerExecutionResult executeDataCollectionScheduler();

    /**
     * 알림 정리 스케줄러 실행
     *
     * @return 스케줄러 실행 결과
     */
    SchedulerExecutionResult executeNotificationCleanupScheduler();
}
