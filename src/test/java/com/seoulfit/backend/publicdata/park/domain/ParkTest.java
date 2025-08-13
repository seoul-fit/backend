package com.seoulfit.backend.publicdata.park.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Park 도메인 테스트")
class ParkTest {

    @Test
    @DisplayName("Park 생성 - 성공")
    void createPark_Success() {
        // given & when
        Park park = Park.builder()
                .parkIdx(1)
                .name("남산공원")
                .content("서울의 대표적인 도심 공원")
                .area("125,000㎡")
                .address("서울특별시 중구 남산공원길 105")
                .zone("중구")
                .longitude(126.9882)
                .latitude(37.5511)
                .adminTel("02-1234-5678")
                .imageUrl("https://example.com/image.jpg")
                .build();

        // then
        assertThat(park.getName()).isEqualTo("남산공원");
        assertThat(park.getZone()).isEqualTo("중구");
        assertThat(park.getLongitude()).isEqualTo(126.9882);
        assertThat(park.getLatitude()).isEqualTo(37.5511);
    }

    @Test
    @DisplayName("위치 정보 확인 - 위치 정보 있음")
    void hasLocation_WithCoordinates() {
        // given
        Park park = Park.builder()
                .name("남산공원")
                .longitude(126.9882)
                .latitude(37.5511)
                .build();

        // when & then
        assertThat(park.hasLocation()).isTrue();
    }

    @Test
    @DisplayName("위치 정보 확인 - 위치 정보 없음")
    void hasLocation_WithoutCoordinates() {
        // given
        Park park = Park.builder()
                .name("남산공원")
                .build();

        // when & then
        assertThat(park.hasLocation()).isFalse();
    }

    @Test
    @DisplayName("연락처 정보 확인 - 연락처 있음")
    void hasContact_WithPhoneNumber() {
        // given
        Park park = Park.builder()
                .name("남산공원")
                .adminTel("02-1234-5678")
                .build();

        // when & then
        assertThat(park.hasContact()).isTrue();
    }

    @Test
    @DisplayName("연락처 정보 확인 - 연락처 없음")
    void hasContact_WithoutPhoneNumber() {
        // given
        Park park = Park.builder()
                .name("남산공원")
                .build();

        // when & then
        assertThat(park.hasContact()).isFalse();
    }

    @Test
    @DisplayName("연락처 정보 확인 - 빈 문자열")
    void hasContact_WithEmptyPhoneNumber() {
        // given
        Park park = Park.builder()
                .name("남산공원")
                .adminTel("   ")
                .build();

        // when & then
        assertThat(park.hasContact()).isFalse();
    }

    @Test
    @DisplayName("이미지 정보 확인 - 이미지 있음")
    void hasImage_WithImageUrl() {
        // given
        Park park = Park.builder()
                .name("남산공원")
                .imageUrl("https://example.com/image.jpg")
                .build();

        // when & then
        assertThat(park.hasImage()).isTrue();
    }

    @Test
    @DisplayName("이미지 정보 확인 - 이미지 없음")
    void hasImage_WithoutImageUrl() {
        // given
        Park park = Park.builder()
                .name("남산공원")
                .build();

        // when & then
        assertThat(park.hasImage()).isFalse();
    }

    @Test
    @DisplayName("GeoPoint 인터페이스 구현 - 위도 반환")
    void getLatitude_Implementation() {
        // given
        Park park = Park.builder()
                .name("남산공원")
                .latitude(37.5511)
                .build();

        // when & then
        assertThat(park.getLatitude()).isEqualTo(37.5511);
    }

    @Test
    @DisplayName("GeoPoint 인터페이스 구현 - 경도 반환")
    void getLongitude_Implementation() {
        // given
        Park park = Park.builder()
                .name("남산공원")
                .longitude(126.9882)
                .build();

        // when & then
        assertThat(park.getLongitude()).isEqualTo(126.9882);
    }

    @Test
    @DisplayName("GeoPoint 인터페이스 구현 - null 좌표 처리")
    void getCoordinates_WithNullValues() {
        // given
        Park park = Park.builder()
                .name("남산공원")
                .build();

        // when & then
        assertThat(park.getLatitude()).isEqualTo(0.0);
        assertThat(park.getLongitude()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("공원 정보 업데이트")
    void updatePark() {
        // given
        Park park = Park.builder()
                .name("남산공원")
                .content("기존 설명")
                .area("100,000㎡")
                .build();

        // when
        park.update(
                "남산공원 (업데이트)",
                "새로운 설명",
                "125,000㎡",
                "새로운 시설",
                "새로운 주소",
                "02-9999-8888",
                127.0000,
                38.0000
        );

        // then
        assertThat(park.getName()).isEqualTo("남산공원 (업데이트)");
        assertThat(park.getContent()).isEqualTo("새로운 설명");
        assertThat(park.getArea()).isEqualTo("125,000㎡");
        assertThat(park.getMainEquipment()).isEqualTo("새로운 시설");
        assertThat(park.getAddress()).isEqualTo("새로운 주소");
        assertThat(park.getAdminTel()).isEqualTo("02-9999-8888");
        assertThat(park.getLongitude()).isEqualTo(127.0000);
        assertThat(park.getLatitude()).isEqualTo(38.0000);
        assertThat(park.getUpdatedAt()).isNotNull();
    }
}
