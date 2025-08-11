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
        Map<String, Object> parameters = Map.of(
                "userId", id,
                "userInterests", interestCategories != null ? interestCategories : List.of(),
                "latitude", locationLatitude,
                "longitude", locationLongitude,
                "userAddress", locationAddress != null ? locationAddress : ""
        );
        
        return new TriggerEvaluationCommand("ALL", id, parameters);
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public List<InterestCategory> getUserInterests() {
        @SuppressWarnings("unchecked")
        List<InterestCategory> interests = (List<InterestCategory>) parameters.get("userInterests");
        return interests != null ? interests : List.of();
    }
    
    public Double getLatitude() {
        return (Double) parameters.get("latitude");
    }
    
    public Double getLongitude() {
        return (Double) parameters.get("longitude");
    }
    
    public String getUserAddress() {
        return (String) parameters.get("userAddress");
    }
}
