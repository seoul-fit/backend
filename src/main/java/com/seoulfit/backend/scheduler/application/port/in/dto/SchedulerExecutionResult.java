package com.seoulfit.backend.scheduler.application.port.in.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 스케줄러 실행 결과
 * <p>
 * 헥사고날 아키텍처의 Result 객체
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Getter
@Builder
public class SchedulerExecutionResult {

    private final String schedulerName;
    private final boolean success;
    private final LocalDateTime executedAt;
    private final long executionTimeMs;
    private final int processedCount;
    private final String message;
    private final String errorMessage;

    /**
     * 성공한 스케줄러 실행 결과 생성
     *
     * @param schedulerName    스케줄러명
     * @param executedAt       실행 시간
     * @param executionTimeMs  실행 소요 시간 (밀리초)
     * @param processedCount   처리된 항목 수
     * @param message          메시지
     * @return 스케줄러 실행 결과
     */
    public static SchedulerExecutionResult success(String schedulerName, LocalDateTime executedAt,
                                                  long executionTimeMs, int processedCount, String message) {
        return SchedulerExecutionResult.builder()
                .schedulerName(schedulerName)
                .success(true)
                .executedAt(executedAt)
                .executionTimeMs(executionTimeMs)
                .processedCount(processedCount)
                .message(message)
                .build();
    }

    /**
     * 실패한 스케줄러 실행 결과 생성
     *
     * @param schedulerName 스케줄러명
     * @param executedAt    실행 시간
     * @param errorMessage  오류 메시지
     * @return 스케줄러 실행 결과
     */
    public static SchedulerExecutionResult failure(String schedulerName, LocalDateTime executedAt, String errorMessage) {
        return SchedulerExecutionResult.builder()
                .schedulerName(schedulerName)
                .success(false)
                .executedAt(executedAt)
                .errorMessage(errorMessage)
                .build();
    }
}
