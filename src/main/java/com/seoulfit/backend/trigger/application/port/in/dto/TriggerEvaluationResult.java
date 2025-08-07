package com.seoulfit.backend.trigger.application.port.in.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 트리거 평가 결과
 * 
 * 트리거 평가 결과를 담는 객체
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Getter
@Builder
public class TriggerEvaluationResult {

    private final boolean triggered;
    private final int triggeredCount;
    private final int totalEvaluated;
    private final List<TriggeredInfo> triggeredList;
    private final LocalDateTime evaluationTime;
    private final LocationInfo locationInfo;
    private final Map<String, Object> metadata;

    /**
     * 발동된 트리거 정보
     */
    @Getter
    @Builder
    public static class TriggeredInfo {
        private final String triggerType;
        private final String title;
        private final String message;
        private final int priority;
        private final String locationInfo;
        private final LocalDateTime triggeredTime;
    }

    /**
     * 위치 정보
     */
    @Getter
    @Builder
    public static class LocationInfo {
        private final Double latitude;
        private final Double longitude;
        private final String address;
        private final String district;
    }

    /**
     * 트리거 발동 결과 생성
     */
    public static TriggerEvaluationResult triggered(List<TriggeredInfo> triggeredList, 
                                                  int totalEvaluated, LocationInfo locationInfo) {
        return TriggerEvaluationResult.builder()
                .triggered(true)
                .triggeredCount(triggeredList.size())
                .totalEvaluated(totalEvaluated)
                .triggeredList(triggeredList)
                .evaluationTime(LocalDateTime.now())
                .locationInfo(locationInfo)
                .build();
    }

    /**
     * 트리거 미발동 결과 생성
     */
    public static TriggerEvaluationResult notTriggered(int totalEvaluated, LocationInfo locationInfo) {
        return TriggerEvaluationResult.builder()
                .triggered(false)
                .triggeredCount(0)
                .totalEvaluated(totalEvaluated)
                .triggeredList(List.of())
                .evaluationTime(LocalDateTime.now())
                .locationInfo(locationInfo)
                .build();
    }
}
