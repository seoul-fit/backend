package com.seoulfit.backend.user.domain;

import lombok.Getter;

/**
 * 관심사 카테고리
 * 
 * UrbanPing 시스템에서 제공하는 도시 정보 카테고리
 * 사용자가 체크박스로 선택할 수 있는 관심사들
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Getter
public enum InterestCategory {
    // 기존 카테고리
    WEATHER("날씨", "기상정보, 폭염, 한파, 미세먼지 등 날씨 관련 정보", "🌤️"),
    CULTURE("문화생활", "문화행사, 축제, 공연, 전시회 등 문화 관련 정보", "🎭"),
    TRAFFIC("교통", "교통정체, 도로통제, 대중교통 지연 등 교통 관련 정보", "🚗"),
    BIKE_SHARING("따릉이", "따릉이 대여소 현황, 자전거 이용 정보 등", "🚲"),
    CONGESTION("인구혼잡도", "지하철 혼잡도, 유동인구, 혼잡 지역 정보 등", "👥"),
    
    // 새로 추가된 카테고리
    RESTAURANTS("맛집", "서울시 관광 음식점, 맛집 정보 및 추천", "🍽️"),
    CULTURAL_FACILITIES("문화시설", "박물관, 미술관, 공연장 등 문화시설 정보", "🏛️"),
    LIBRARIES("도서관", "공공도서관 현황, 운영시간, 프로그램 정보", "📚"),
    PARKS("공원", "주요 공원 현황, 시설 정보, 이용 안내", "🌳"),
    SPORTS_FACILITIES("체육시설", "공공체육시설, 운영프로그램, 예약 정보", "🏃‍♂️"),
    COOLING_CENTERS("무더위쉼터", "여름철 무더위쉼터 위치 및 이용 정보", "❄️");

    private final String displayName;
    private final String description;
    private final String emoji;

    InterestCategory(String displayName, String description, String emoji) {
        this.displayName = displayName;
        this.description = description;
        this.emoji = emoji;
    }

    /**
     * 표시명으로 카테고리 찾기
     * @param displayName 표시명
     * @return 관심사 카테고리
     */
    public static InterestCategory fromDisplayName(String displayName) {
        for (InterestCategory category : values()) {
            if (category.displayName.equals(displayName)) {
                return category;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 관심사 카테고리입니다: " + displayName);
    }

    /**
     * 이모지와 함께 표시명 반환
     * @return 이모지 포함 표시명
     */
    public String getDisplayNameWithEmoji() {
        return emoji + " " + displayName;
    }

    /**
     * 카테고리 유효성 검증
     * @param categoryName 카테고리명
     * @return 유효 여부
     */
    public static boolean isValid(String categoryName) {
        try {
            valueOf(categoryName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 위치 기반 서비스 카테고리인지 확인
     * @return 위치 기반 서비스 여부
     */
    public boolean isLocationBased() {
        return this == RESTAURANTS || this == CULTURAL_FACILITIES || 
               this == LIBRARIES || this == PARKS || 
               this == SPORTS_FACILITIES || this == COOLING_CENTERS;
    }

    /**
     * 실시간 알림이 필요한 카테고리인지 확인
     * @return 실시간 알림 필요 여부
     */
    public boolean isRealtimeNotificationRequired() {
        return this == WEATHER || this == TRAFFIC || 
               this == BIKE_SHARING || this == CONGESTION;
    }
}
