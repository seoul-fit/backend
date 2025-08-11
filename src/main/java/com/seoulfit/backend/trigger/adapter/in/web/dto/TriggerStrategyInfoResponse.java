package com.seoulfit.backend.trigger.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * 트리거 전략 정보 응답 DTO
 * 
 * 등록된 트리거 전략의 정보를 클라이언트에 반환하기 위한 응답 객체
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Schema(description = "트리거 전략 정보")
@Getter
@Builder
public class TriggerStrategyInfoResponse {

    @Schema(description = "트리거 타입", example = "TEMPERATURE")
    private final String type;

    @Schema(description = "전략 설명", example = "기온이 설정된 임계값을 초과하거나 미만일 때 알림을 발송합니다.")
    private final String description;

    @Schema(description = "우선순위 (낮을수록 높은 우선순위)", example = "10")
    private final int priority;

    @Schema(description = "활성화 여부", example = "true")
    private final boolean enabled;

    @Schema(description = "클래스명", example = "TemperatureTriggerStrategy")
    private final String className;

    @Schema(description = "지원하는 관심사 카테고리", example = "[\"WEATHER\"]")
    private final String[] supportedCategories;

    @Schema(description = "설정 가능한 파라미터")
    private final Map<String, Object> configurableParameters;

    @Schema(description = "현재 설정값")
    private final Map<String, Object> currentConfiguration;

    @Schema(description = "마지막 실행 시간")
    private final String lastExecutionTime;

    @Schema(description = "실행 통계")
    private final ExecutionStats executionStats;

    /**
     * 실행 통계 정보
     */
    @Schema(description = "트리거 실행 통계")
    @Getter
    @Builder
    public static class ExecutionStats {
        
        @Schema(description = "총 실행 횟수", example = "1250")
        private final long totalExecutions;

        @Schema(description = "성공 횟수", example = "1248")
        private final long successCount;

        @Schema(description = "실패 횟수", example = "2")
        private final long failureCount;

        @Schema(description = "트리거 발동 횟수", example = "45")
        private final long triggeredCount;

        @Schema(description = "평균 실행 시간 (ms)", example = "125.5")
        private final double averageExecutionTime;
    }
}
