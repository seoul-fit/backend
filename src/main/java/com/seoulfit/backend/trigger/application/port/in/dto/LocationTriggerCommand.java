package com.seoulfit.backend.trigger.application.port.in.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 위치 기반 트리거 명령
 * 
 * 사용자의 위치 정보를 기반으로 트리거를 평가하기 위한 명령 객체
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
@Builder
public class LocationTriggerCommand {

    private final String userId;
    private final Double latitude;
    private final Double longitude;
    private final Integer radius;
    private final List<String> triggerTypes;
    private final Boolean forceEvaluation;

    /**
     * 위치 기반 트리거 명령 생성
     *
     * @param userId 사용자 ID
     * @param latitude 위도
     * @param longitude 경도
     * @param radius 검색 반경 (미터)
     * @param triggerTypes 평가할 트리거 타입 목록
     * @return 위치 기반 트리거 명령
     */
    public static LocationTriggerCommand of(String userId, Double latitude, Double longitude,
                                          Integer radius, List<String> triggerTypes) {
        return LocationTriggerCommand.builder()
                .userId(userId)
                .latitude(latitude)
                .longitude(longitude)
                .radius(radius != null ? radius : 2000)
                .triggerTypes(triggerTypes)
                .forceEvaluation(false)
                .build();
    }

    /**
     * 강제 평가 옵션을 포함한 위치 기반 트리거 명령 생성
     */
    public static LocationTriggerCommand ofForced(String userId, Double latitude, Double longitude,
                                                Integer radius, List<String> triggerTypes, 
                                                Boolean forceEvaluation) {
        return LocationTriggerCommand.builder()
                .userId(userId)
                .latitude(latitude)
                .longitude(longitude)
                .radius(radius != null ? radius : 2000)
                .triggerTypes(triggerTypes)
                .forceEvaluation(forceEvaluation != null ? forceEvaluation : false)
                .build();
    }
}
