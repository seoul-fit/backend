package com.seoulfit.backend.trigger.application.port.in.dto;

import com.seoulfit.backend.user.domain.InterestCategory;
import java.util.List;
import java.util.Map;

/**
 * 트리거 평가 명령
 */
public record TriggerEvaluationCommand(
        String triggerType,
        Long userId,
        Map<String, Object> parameters
) {

    public TriggerEvaluationCommand {
        if (triggerType == null || triggerType.isBlank()) {
            throw new IllegalArgumentException("트리거 타입은 필수입니다.");
        }
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        if (parameters == null) {
            parameters = Map.of();
        }
    }

    public static TriggerEvaluationCommand of(Long id, List<InterestCategory> interestCategories, Double locationLatitude,
            Double locationLongitude, String locationAddress) {
        return null;
    }
}
