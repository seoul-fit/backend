package com.seoulfit.backend.user.domain;

import lombok.Getter;

/**
 * 관심사 카테고리
 * <p>
 * UrbanPing 시스템에서 제공하는 도시 정보 카테고리
 * 사용자가 체크박스로 선택할 수 있는 관심사들
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
public enum InterestCategory {
    SPORTS("체육시설", "헬스장, 수영장, 테니스장 등 체육시설 정보", "🏃‍♂️"),
    CULTURE("문화시설", "공연장, 전시관, 미술관 등 문화시설 정보", "🏛️"),
    RESTAURANTS("맛집", "유명 음식점, 디저트 카페 등 맛집 정보", "🍽️"),
    LIBRARY("도서관", "공공도서관 현황, 운영시간, 예약 등", "📚"),
    PARK("공원", "주요 공원 현황, 시설 정보, 이용 안내", "🌳"),
    BIKE("따릉이", "따릉이 대여소 현황, 자전거 이용 정보 등", "🚲"),
    WEATHER("날씨", "기상정보, 폭염, 한파, 미세먼지 등 날씨 관련 정보", "🌤️"),
    CONGESTION("인구혼잡도", "유동인구, 혼잡 지역 정보 등", "👥");

    private final String displayName;
    private final String description;
    private final String emoji;

    InterestCategory(String displayName, String description, String emoji) {
        this.displayName = displayName;
        this.description = description;
        this.emoji = emoji;
    }

    /**
     * 이모지와 함께 표시명 반환
     * @return 이모지 포함 표시명
     */
    public String getDisplayNameWithEmoji() {
        return emoji + " " + displayName;
    }

    /**
     * 위치 기반 서비스 카테고리인지 확인
     * @return 위치 기반 서비스 여부
     */
    public boolean isLocationBased() {
        return this == SPORTS || this == CULTURE ||
               this == RESTAURANTS || this == LIBRARY ||
               this == PARK || this == BIKE;
    }

    /**
     * 실시간 알림이 필요한 카테고리인지 확인
     * @return 실시간 알림 필요 여부
     */
    public boolean isRealtimeNotificationRequired() {
        return this == WEATHER || this == CONGESTION || this == BIKE;
    }
}
