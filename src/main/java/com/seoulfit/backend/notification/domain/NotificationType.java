package com.seoulfit.backend.notification.domain;

import lombok.Getter;

/**
 * 알림 타입
 * <p>
 * UrbanPing 시스템에서 사용되는 알림 분류
 * 우선순위와 긴급도를 포함한 도메인 값 객체
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
public enum NotificationType {
    EMERGENCY("긴급", "재난, 안전사고 등 긴급 상황 알림", "🚨", 1, true),
    WEATHER("날씨", "기상정보, 폭염, 한파, 미세먼지 등 날씨 관련 알림", "🌤️", 2, false),
    TRAFFIC("교통", "교통정체, 도로통제, 대중교통 지연 등 교통 관련 알림", "🚗", 3, false),
    BIKE_SHARING("따릉이", "따릉이 대여소 현황, 자전거 이용 정보 등", "🚲", 4, false),
    CONGESTION("인구혼잡도", "지하철 혼잡도, 유동인구, 혼잡 지역 정보 등", "👥", 5, false),
    CULTURE("문화생활", "문화행사, 축제, 공연, 전시회 등 문화 관련 알림", "🎭", 6, false),
    WELFARE("복지", "복지 혜택, 지원 정보 등 복지 관련 알림", "🏛️", 7, false);

    private final String displayName;
    private final String description;
    private final String emoji;
    private final int priority; // 낮을수록 높은 우선순위
    private final boolean urgent; // 긴급 알림 여부

    NotificationType(String displayName, String description, String emoji, int priority, boolean urgent) {
        this.displayName = displayName;
        this.description = description;
        this.emoji = emoji;
        this.priority = priority;
        this.urgent = urgent;
    }

    /**
     * 표시명으로 알림 타입 찾기
     * @param displayName 표시명
     * @return 알림 타입
     */
    public static NotificationType fromDisplayName(String displayName) {
        for (NotificationType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 알림 타입입니다: " + displayName);
    }

    /**
     * 이모지와 함께 표시명 반환
     * @return 이모지 포함 표시명
     */
    public String getDisplayNameWithEmoji() {
        return emoji + " " + displayName;
    }

    /**
     * 긴급 알림 여부 확인
     * @return 긴급 알림 여부
     */
    public boolean isUrgent() {
        return this.urgent;
    }

    /**
     * 실시간 알림 여부 확인
     * @return 실시간 알림 여부
     */
    public boolean isRealtime() {
        return this == WEATHER || this == TRAFFIC || this == BIKE_SHARING || 
               this == CONGESTION || this == EMERGENCY;
    }

    /**
     * 높은 우선순위 알림 여부 확인 (우선순위 3 이하)
     * @return 높은 우선순위 여부
     */
    public boolean isHighPriority() {
        return this.priority <= 3;
    }

    /**
     * 알림 타입 유효성 검증
     * @param typeName 타입명
     * @return 유효 여부
     */
    public static boolean isValid(String typeName) {
        try {
            valueOf(typeName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
