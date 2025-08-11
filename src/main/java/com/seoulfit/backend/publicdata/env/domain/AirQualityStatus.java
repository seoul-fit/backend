package com.seoulfit.backend.publicdata.env.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 대기질 상태 열거형
 */
@Getter
@RequiredArgsConstructor
public enum AirQualityStatus {
    GOOD("좋음", "대기오염 관련 질환자군에서도 영향이 거의 없는 수준"),
    MODERATE("보통", "환경기준 이하로 평상시 활동하기에 좋은 수준"),
    UNHEALTHY_FOR_SENSITIVE("민감군 주의", "환경기준 초과이나 건강한 사람에게는 영향이 거의 없는 수준"),
    UNHEALTHY("나쁨", "환경기준 초과로 모든 사람들이 건강상 불쾌감을 경험할 수 있는 수준"),
    VERY_UNHEALTHY("매우 나쁨", "환경기준을 크게 초과하여 모든 사람들이 건강상 영향을 경험할 수 있는 수준"),
    HAZARDOUS("위험", "환경기준을 심각하게 초과하여 응급조치가 필요한 수준"),
    UNKNOWN("알 수 없음", "데이터가 없거나 측정되지 않은 상태");

    private final String description;
    private final String detail;

    /**
     * 대기질 지수로부터 상태 결정
     */
    public static AirQualityStatus fromKhaiValue(Integer khaiValue) {
        if (khaiValue == null) {
            return UNKNOWN;
        }
        
        if (khaiValue <= 50) {
            return GOOD;
        } else if (khaiValue <= 100) {
            return MODERATE;
        } else if (khaiValue <= 150) {
            return UNHEALTHY_FOR_SENSITIVE;
        } else if (khaiValue <= 200) {
            return UNHEALTHY;
        } else if (khaiValue <= 300) {
            return VERY_UNHEALTHY;
        } else {
            return HAZARDOUS;
        }
    }

    /**
     * PM2.5 농도로부터 상태 결정
     */
    public static AirQualityStatus fromPm25Value(Integer pm25Value) {
        if (pm25Value == null) {
            return UNKNOWN;
        }
        
        if (pm25Value <= 15) {
            return GOOD;
        } else if (pm25Value <= 35) {
            return MODERATE;
        } else if (pm25Value <= 75) {
            return UNHEALTHY_FOR_SENSITIVE;
        } else if (pm25Value <= 150) {
            return UNHEALTHY;
        } else {
            return VERY_UNHEALTHY;
        }
    }

    /**
     * 알림이 필요한 상태인지 확인
     */
    public boolean requiresNotification() {
        return this == UNHEALTHY || this == VERY_UNHEALTHY || this == HAZARDOUS;
    }

    /**
     * 민감군에게 주의가 필요한 상태인지 확인
     */
    public boolean requiresSensitiveGroupAlert() {
        return this == UNHEALTHY_FOR_SENSITIVE || requiresNotification();
    }
}
