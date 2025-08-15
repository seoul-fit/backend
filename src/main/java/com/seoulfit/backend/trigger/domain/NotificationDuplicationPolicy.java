package com.seoulfit.backend.trigger.domain;

import lombok.Getter;

import java.time.Duration;

/**
 * 알림 중복 방지 정책
 * 
 * 각 트리거 조건에 따른 중복 알림 방지 규칙을 정의
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
public enum NotificationDuplicationPolicy {
    // 문화행사: 영구 중복 방지 (한번 알림 받으면 다시 안받음)
    CULTURAL_EVENT_PERMANENT(Duration.ofDays(365), DuplicationCheckType.UNIQUE_IDENTIFIER, "cultural_event_id"),
    CULTURAL_EVENT_START_PERMANENT(Duration.ofDays(365), DuplicationCheckType.UNIQUE_IDENTIFIER, "cultural_event_id"),
    
    // 따릉이: 1시간 캐시 (같은 대여소는 1시간 내 중복 알림 안함)
    BIKE_SHORTAGE_HOURLY(Duration.ofHours(1), DuplicationCheckType.LOCATION_BASED, "bike_station_id"),
    BIKE_FULL_HOURLY(Duration.ofHours(1), DuplicationCheckType.LOCATION_BASED, "bike_station_id"),
    
    // 날씨: 30분 캐시 (같은 조건은 30분 내 중복 알림 안함)
    TEMPERATURE_HIGH_HALF_HOUR(Duration.ofMinutes(30), DuplicationCheckType.CONDITION_BASED, null),
    TEMPERATURE_LOW_HALF_HOUR(Duration.ofMinutes(30), DuplicationCheckType.CONDITION_BASED, null),
    HEAVY_RAIN_HALF_HOUR(Duration.ofMinutes(30), DuplicationCheckType.CONDITION_BASED, null),
    
    // 대기질: 1시간 캐시
    AIR_QUALITY_BAD_HOURLY(Duration.ofHours(1), DuplicationCheckType.CONDITION_BASED, null),
    FINE_DUST_HIGH_HOURLY(Duration.ofHours(1), DuplicationCheckType.CONDITION_BASED, null),
    OZONE_WARNING_HOURLY(Duration.ofHours(1), DuplicationCheckType.CONDITION_BASED, null),
    
    // 교통: 30분 캐시
    TRAFFIC_JAM_HALF_HOUR(Duration.ofMinutes(30), DuplicationCheckType.CONDITION_BASED, null),
    ROAD_CLOSURE_HALF_HOUR(Duration.ofMinutes(30), DuplicationCheckType.CONDITION_BASED, null),
    
    // 혼잡도: 30분 캐시
    CONGESTION_HALF_HOUR(Duration.ofMinutes(30), DuplicationCheckType.CONDITION_BASED, null),
    SUBWAY_CROWDED_HALF_HOUR(Duration.ofMinutes(30), DuplicationCheckType.CONDITION_BASED, null),
    
    // 긴급상황: 중복 방지 없음 (항상 알림)
    EMERGENCY_NO_LIMIT(Duration.ZERO, DuplicationCheckType.NO_CHECK, null),
    DISASTER_NO_LIMIT(Duration.ZERO, DuplicationCheckType.NO_CHECK, null),
    
    // 복지: 6시간 캐시
    WELFARE_INFO_SIX_HOURS(Duration.ofHours(6), DuplicationCheckType.CONDITION_BASED, null),
    
    // 기타: 기본 30분 캐시
    DEFAULT_HALF_HOUR(Duration.ofMinutes(30), DuplicationCheckType.CONDITION_BASED, null);
    
    private final Duration preventionDuration;
    private final DuplicationCheckType checkType;
    private final String uniqueIdentifierKey;
    
    NotificationDuplicationPolicy(Duration preventionDuration, DuplicationCheckType checkType, String uniqueIdentifierKey) {
        this.preventionDuration = preventionDuration;
        this.checkType = checkType;
        this.uniqueIdentifierKey = uniqueIdentifierKey;
    }
    
    /**
     * TriggerCondition으로부터 적용할 중복 방지 정책을 찾습니다.
     * 
     * @param triggerCondition 트리거 조건
     * @return 적용할 중복 방지 정책
     */
    public static NotificationDuplicationPolicy fromTriggerCondition(TriggerCondition triggerCondition) {
        String conditionName = triggerCondition.name();
        try {
            // 우선 정확한 매칭 시도
            for (NotificationDuplicationPolicy policy : values()) {
                if (policy.name().startsWith(conditionName)) {
                    return policy;
                }
            }
            
            // 특별한 경우 처리
            switch (triggerCondition) {
                case CULTURAL_EVENT:
                    return CULTURAL_EVENT_PERMANENT;
                case CULTURAL_EVENT_START:
                    return CULTURAL_EVENT_START_PERMANENT;
                case BIKE_SHORTAGE:
                    return BIKE_SHORTAGE_HOURLY;
                case BIKE_FULL:
                    return BIKE_FULL_HOURLY;
                case TEMPERATURE_HIGH:
                    return TEMPERATURE_HIGH_HALF_HOUR;
                case TEMPERATURE_LOW:
                    return TEMPERATURE_LOW_HALF_HOUR;
                case HEAVY_RAIN:
                    return HEAVY_RAIN_HALF_HOUR;
                case AIR_QUALITY_BAD:
                    return AIR_QUALITY_BAD_HOURLY;
                case FINE_DUST_HIGH:
                    return FINE_DUST_HIGH_HOURLY;
                case OZONE_WARNING:
                    return OZONE_WARNING_HOURLY;
                case TRAFFIC_JAM:
                    return TRAFFIC_JAM_HALF_HOUR;
                case ROAD_CLOSURE:
                    return ROAD_CLOSURE_HALF_HOUR;
                case CONGESTION:
                    return CONGESTION_HALF_HOUR;
                case SUBWAY_CROWDED:
                    return SUBWAY_CROWDED_HALF_HOUR;
                case EMERGENCY:
                    return EMERGENCY_NO_LIMIT;
                case DISASTER:
                    return DISASTER_NO_LIMIT;
                case WELFARE_INFO:
                    return WELFARE_INFO_SIX_HOURS;
                default:
                    return DEFAULT_HALF_HOUR;
            }
        } catch (Exception e) {
            return DEFAULT_HALF_HOUR;
        }
    }
    
    /**
     * 중복 방지가 필요한지 확인합니다.
     * 
     * @return 중복 방지가 필요하면 true
     */
    public boolean isPreventionRequired() {
        return !preventionDuration.isZero() && checkType != DuplicationCheckType.NO_CHECK;
    }
    
    /**
     * 고유 식별자 기반 중복 확인인지 여부
     * 
     * @return 고유 식별자 기반이면 true
     */
    public boolean isUniqueIdentifierBased() {
        return checkType == DuplicationCheckType.UNIQUE_IDENTIFIER;
    }
    
    /**
     * 위치 기반 중복 확인인지 여부
     * 
     * @return 위치 기반이면 true
     */
    public boolean isLocationBased() {
        return checkType == DuplicationCheckType.LOCATION_BASED;
    }
    
    /**
     * 조건 기반 중복 확인인지 여부 (일반적인 시간 기반 중복 방지)
     * 
     * @return 조건 기반이면 true
     */
    public boolean isConditionBased() {
        return checkType == DuplicationCheckType.CONDITION_BASED;
    }
    
    /**
     * 중복 확인 유형
     */
    public enum DuplicationCheckType {
        /**
         * 중복 확인 안함 (긴급상황 등)
         */
        NO_CHECK,
        
        /**
         * 고유 식별자 기반 (문화행사 ID 등)
         * 한번 알림 받으면 영구적으로 중복 방지
         */
        UNIQUE_IDENTIFIER,
        
        /**
         * 위치 기반 (따릉이 대여소 등)
         * 특정 위치에 대해 시간 제한 중복 방지
         */
        LOCATION_BASED,
        
        /**
         * 조건 기반 (일반적인 트리거 조건)
         * 동일한 트리거 조건에 대해 시간 제한 중복 방지
         */
        CONDITION_BASED
    }
}