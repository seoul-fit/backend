package com.seoulfit.backend.trigger.application.port.in.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 트리거 평가 결과
 */
public record TriggerEvaluationResult(
    String triggerType,
    Long userId,
    boolean isTriggered,
    String message,
    Map<String, Object> resultData,
    LocalDateTime evaluatedAt
) {
    
    public static TriggerEvaluationResult success(String triggerType, Long userId, String message, Map<String, Object> resultData) {
        return new TriggerEvaluationResult(triggerType, userId, true, message, resultData, LocalDateTime.now());
    }
    
    public static TriggerEvaluationResult failure(String triggerType, Long userId, String message) {
        return new TriggerEvaluationResult(triggerType, userId, false, message, Map.of(), LocalDateTime.now());
    }
}
