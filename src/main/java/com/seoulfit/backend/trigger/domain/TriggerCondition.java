package com.seoulfit.backend.trigger.domain;

import lombok.Getter;

/**
 * 트리거 조건
 * <p>
 * UrbanPing 시스템에서 사용되는 트리거 발동 조건들을 정의 각 조건은 특정 상황에서 알림을 발송하기 위한 기준
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Getter
public enum TriggerCondition {
    // 날씨 관련 트리거 조건
    TEMPERATURE_HIGH("고온", "기온이 설정된 임계값을 초과했을 때", "🌡️"),
    TEMPERATURE_LOW("저온", "기온이 설정된 임계값 미만일 때", "🥶"),
    HEAVY_RAIN("폭우", "강수량이 설정된 임계값을 초과했을 때", "🌧️"),

    // 대기질 관련 트리거 조건
    AIR_QUALITY_BAD("대기질 나쁨", "미세먼지 또는 대기질 지수가 나쁨 수준일 때", "😷"),
    FINE_DUST_HIGH("미세먼지 높음", "미세먼지 농도가 높을 때", "🌫️"),
    OZONE_WARNING("오존 경보", "오존 농도가 경보 수준일 때", "⚠️"),

    // 교통 관련 트리거 조건
    TRAFFIC_JAM("교통 정체", "교통 정체가 심할 때", "🚗"),
    ROAD_CLOSURE("도로 통제", "도로가 통제되거나 차단될 때", "🚧"),

    // 따릉이 관련 트리거 조건
    BIKE_SHORTAGE("따릉이 부족", "대여소에 자전거가 부족할 때", "🚲"),
    BIKE_FULL("따릉이 포화", "대여소가 자전거로 가득 찰 때", "🅿️"),

    // 인구 혼잡도 관련 트리거 조건
    CONGESTION("혼잡", "특정 지역의 인구 밀도가 높을 때", "👥"),
    SUBWAY_CROWDED("지하철 혼잡", "지하철이 혼잡할 때", "🚇"),

    // 문화/이벤트 관련 트리거 조건
    CULTURAL_EVENT("문화행사", "새로운 문화행사가 등록되거나 시작될 때", "🎭"),
    CULTURAL_EVENT_START("문화행사 시작", "문화행사가 시작될 때", "🎪"),

    // 긴급 상황 관련 트리거 조건
    EMERGENCY("긴급상황", "재난이나 긴급상황이 발생했을 때", "🚨"),
    DISASTER("재난", "자연재해나 사고가 발생했을 때", "⛑️"),

    // 복지 관련 트리거 조건
    WELFARE_INFO("복지정보", "새로운 복지 혜택이나 지원 정보가 있을 때", "🏛️"),

    // 기타 트리거 조건
    WEATHER_CHANGE("날씨 변화", "날씨가 변화할 때", "🌤️"),
    INTEREST_BASED_RECOMMENDATION("관심사 기반 추천", "사용자 관심사에 기반한 추천", "💡"),
    LOCATION_BASED("위치 기반 알림", "사용자 위치에 기반한 알림", "📍"),
    TIME_BASED("시간 기반 알림", "특정 시간에 발송되는 알림", "⏰"),
    MANUAL("수동 알림", "관리자가 수동으로 발송하는 알림", "✋");

    private final String displayName;
    private final String description;
    private final String emoji;

    TriggerCondition(String displayName, String description, String emoji) {
        this.displayName = displayName;
        this.description = description;
        this.emoji = emoji;
    }

    /**
     * 표시명으로 트리거 조건 찾기
     *
     * @param displayName 표시명
     * @return 트리거 조건
     */
    public static TriggerCondition fromDisplayName(String displayName) {
        for (TriggerCondition condition : values()) {
            if (condition.displayName.equals(displayName)) {
                return condition;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 트리거 조건입니다: " + displayName);
    }

    /**
     * 이모지와 함께 표시명 반환
     *
     * @return 이모지 포함 표시명
     */
    public String getDisplayNameWithEmoji() {
        return emoji + " " + displayName;
    }

    /**
     * 긴급 조건 여부 확인
     *
     * @return 긴급 조건 여부
     */
    public boolean isUrgent() {
        return this == EMERGENCY || this == DISASTER;
    }

    /**
     * 실시간 조건 여부 확인
     *
     * @return 실시간 조건 여부
     */
    public boolean isRealtime() {
        return this == TEMPERATURE_HIGH || this == TEMPERATURE_LOW ||
                this == HEAVY_RAIN || this == AIR_QUALITY_BAD ||
                this == TRAFFIC_JAM || this == BIKE_SHORTAGE ||
                this == CONGESTION || this == EMERGENCY;
    }

    /**
     * 트리거 조건 유효성 검증
     *
     * @param conditionName 조건명
     * @return 유효 여부
     */
    public static boolean isValid(String conditionName) {
        try {
            valueOf(conditionName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
