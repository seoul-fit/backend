package com.seoulfit.backend.publicdata.restaurant.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TouristRestaurant 도메인 테스트")
class TouristRestaurantTest {

    @Test
    @DisplayName("TouristRestaurant 생성 - 성공")
    void createTouristRestaurant_Success() {
        // given & when
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .postSn("POST001")
                .langCodeId("ko")
                .restaurantName("서울 한식당")
                .postUrl("https://example.com/restaurant")
                .address("서울특별시 중구 명동길 123")
                .phoneNumber("02-1234-5678")
                .websiteUrl("https://restaurant.com")
                .operatingHours("11:00~22:00")
                .representativeMenu("김치찌개, 불고기")
                .dataDate("20250812")
                .build();

        // then
        assertThat(restaurant.getPostSn()).isEqualTo("POST001");
        assertThat(restaurant.getLangCodeId()).isEqualTo("ko");
        assertThat(restaurant.getRestaurantName()).isEqualTo("서울 한식당");
        assertThat(restaurant.getPhoneNumber()).isEqualTo("02-1234-5678");
        assertThat(restaurant.getDataDate()).isEqualTo("20250812");
    }

    @Test
    @DisplayName("웹사이트 보유 여부 확인 - 있음")
    void hasWebsite_WithWebsite() {
        // given
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .restaurantName("테스트 음식점")
                .websiteUrl("https://restaurant.com")
                .build();

        // when & then
        assertThat(restaurant.hasWebsite()).isTrue();
    }

    @Test
    @DisplayName("웹사이트 보유 여부 확인 - 없음")
    void hasWebsite_WithoutWebsite() {
        // given
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .restaurantName("테스트 음식점")
                .build();

        // when & then
        assertThat(restaurant.hasWebsite()).isFalse();
    }

    @Test
    @DisplayName("웹사이트 보유 여부 확인 - 빈 문자열")
    void hasWebsite_EmptyWebsite() {
        // given
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .restaurantName("테스트 음식점")
                .websiteUrl("   ")
                .build();

        // when & then
        assertThat(restaurant.hasWebsite()).isFalse();
    }

    @Test
    @DisplayName("전화번호 보유 여부 확인 - 있음")
    void hasPhoneNumber_WithPhoneNumber() {
        // given
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .restaurantName("테스트 음식점")
                .phoneNumber("02-1234-5678")
                .build();

        // when & then
        assertThat(restaurant.hasPhoneNumber()).isTrue();
    }

    @Test
    @DisplayName("전화번호 보유 여부 확인 - 없음")
    void hasPhoneNumber_WithoutPhoneNumber() {
        // given
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .restaurantName("테스트 음식점")
                .build();

        // when & then
        assertThat(restaurant.hasPhoneNumber()).isFalse();
    }

    @Test
    @DisplayName("한국어 정보 확인 - 한국어")
    void isKorean_Korean() {
        // given
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .restaurantName("한식당")
                .langCodeId("ko")
                .build();

        // when & then
        assertThat(restaurant.isKorean()).isTrue();
    }

    @Test
    @DisplayName("한국어 정보 확인 - 영어")
    void isKorean_English() {
        // given
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .restaurantName("Korean Restaurant")
                .langCodeId("en")
                .build();

        // when & then
        assertThat(restaurant.isKorean()).isFalse();
    }

    @Test
    @DisplayName("영어 정보 확인 - 영어")
    void isEnglish_English() {
        // given
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .restaurantName("Korean Restaurant")
                .langCodeId("en")
                .build();

        // when & then
        assertThat(restaurant.isEnglish()).isTrue();
    }

    @Test
    @DisplayName("영어 정보 확인 - 한국어")
    void isEnglish_Korean() {
        // given
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .restaurantName("한식당")
                .langCodeId("ko")
                .build();

        // when & then
        assertThat(restaurant.isEnglish()).isFalse();
    }

    @Test
    @DisplayName("대표메뉴 보유 여부 확인 - 있음")
    void hasRepresentativeMenu_WithMenu() {
        // given
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .restaurantName("한식당")
                .representativeMenu("김치찌개, 불고기")
                .build();

        // when & then
        assertThat(restaurant.hasRepresentativeMenu()).isTrue();
    }

    @Test
    @DisplayName("대표메뉴 보유 여부 확인 - 없음")
    void hasRepresentativeMenu_WithoutMenu() {
        // given
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .restaurantName("한식당")
                .build();

        // when & then
        assertThat(restaurant.hasRepresentativeMenu()).isFalse();
    }

    @Test
    @DisplayName("교통정보 보유 여부 확인 - 있음")
    void hasSubwayInfo_WithInfo() {
        // given
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .restaurantName("한식당")
                .subwayInfo("지하철 2호선 을지로입구역 3번 출구")
                .build();

        // when & then
        assertThat(restaurant.hasSubwayInfo()).isTrue();
    }

    @Test
    @DisplayName("교통정보 보유 여부 확인 - 없음")
    void hasSubwayInfo_WithoutInfo() {
        // given
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .restaurantName("한식당")
                .build();

        // when & then
        assertThat(restaurant.hasSubwayInfo()).isFalse();
    }

    @Test
    @DisplayName("특정 언어 확인")
    void isLanguage_SpecificLanguage() {
        // given
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .restaurantName("Japanese Restaurant")
                .langCodeId("ja")
                .build();

        // when & then
        assertThat(restaurant.isLanguage("ja")).isTrue();
        assertThat(restaurant.isLanguage("ko")).isFalse();
        assertThat(restaurant.isLanguage("en")).isFalse();
    }

    @Test
    @DisplayName("음식점 정보 업데이트")
    void updateRestaurant() {
        // given
        TouristRestaurant restaurant = TouristRestaurant.builder()
                .postSn("POST001")
                .restaurantName("기존 음식점")
                .address("기존 주소")
                .phoneNumber("02-1111-1111")
                .build();

        // when
        restaurant.update(
                "ko",
                "새로운 음식점",
                "https://new-url.com",
                "새로운 주소",
                "새로운 신주소",
                "02-9999-9999",
                "https://new-website.com",
                "새로운 운영시간",
                "새로운 교통정보",
                "한국어",
                "새로운 대표메뉴"
        );

        // then
        assertThat(restaurant.getRestaurantName()).isEqualTo("새로운 음식점");
        assertThat(restaurant.getAddress()).isEqualTo("새로운 주소");
        assertThat(restaurant.getPhoneNumber()).isEqualTo("02-9999-9999");
        assertThat(restaurant.getWebsiteUrl()).isEqualTo("https://new-website.com");
        assertThat(restaurant.getRepresentativeMenu()).isEqualTo("새로운 대표메뉴");
        assertThat(restaurant.getUpdatedAt()).isNotNull();
    }
}
