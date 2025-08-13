package com.seoulfit.backend.publicdata.facilities.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CoolingCenter 도메인 테스트")
class CoolingCenterTest {

    @Test
    @DisplayName("CoolingCenter 생성 - 성공")
    void createCoolingCenter_Success() {
        // given & when
        CoolingCenter center = CoolingCenter.builder()
                .facilityYear(2025)
                .areaCode("11010")
                .facilityType1("공공시설")
                .facilityType2("주민센터")
                .name("종로구청 무더위쉼터")
                .roadAddress("서울특별시 종로구 종로1길 36")
                .lotAddress("서울특별시 종로구 수송동 146")
                .areaSize("100.5㎡")
                .capacity(50)
                .remarks("에어컨 완비")
                .longitude(126.9780)
                .latitude(37.5665)
                .build();

        // then
        assertThat(center.getName()).isEqualTo("종로구청 무더위쉼터");
        assertThat(center.getFacilityYear()).isEqualTo(2025);
        assertThat(center.getCapacity()).isEqualTo(50);
        assertThat(center.getLatitude()).isEqualTo(37.5665);
        assertThat(center.getLongitude()).isEqualTo(126.9780);
    }

    @Test
    @DisplayName("위치 정보 확인 - 위치 정보 있음")
    void hasLocation_WithCoordinates() {
        // given
        CoolingCenter center = CoolingCenter.builder()
                .name("테스트 쉼터")
                .longitude(126.9780)
                .latitude(37.5665)
                .build();

        // when & then
        assertThat(center.hasLocation()).isTrue();
    }

    @Test
    @DisplayName("위치 정보 확인 - 위치 정보 없음")
    void hasLocation_WithoutCoordinates() {
        // given
        CoolingCenter center = CoolingCenter.builder()
                .name("테스트 쉼터")
                .build();

        // when & then
        assertThat(center.hasLocation()).isFalse();
    }

    @Test
    @DisplayName("수용 인원 확인 - 수용 인원 있음")
    void hasCapacity_WithCapacity() {
        // given
        CoolingCenter center = CoolingCenter.builder()
                .name("테스트 쉼터")
                .capacity(50)
                .build();

        // when & then
        assertThat(center.hasCapacity()).isTrue();
    }

    @Test
    @DisplayName("수용 인원 확인 - 수용 인원 없음")
    void hasCapacity_WithoutCapacity() {
        // given
        CoolingCenter center = CoolingCenter.builder()
                .name("테스트 쉼터")
                .build();

        // when & then
        assertThat(center.hasCapacity()).isFalse();
    }

    @Test
    @DisplayName("수용 인원 확인 - 0명")
    void hasCapacity_ZeroCapacity() {
        // given
        CoolingCenter center = CoolingCenter.builder()
                .name("테스트 쉼터")
                .capacity(0)
                .build();

        // when & then
        assertThat(center.hasCapacity()).isFalse();
    }

    @Test
    @DisplayName("실내 시설 확인 - 실내 시설")
    void isIndoorFacility_Indoor() {
        // given
        CoolingCenter center = CoolingCenter.builder()
                .name("테스트 쉼터")
                .facilityType1("실내체육관")
                .build();

        // when & then
        assertThat(center.isIndoorFacility()).isTrue();
    }

    @Test
    @DisplayName("실내 시설 확인 - 건물")
    void isIndoorFacility_Building() {
        // given
        CoolingCenter center = CoolingCenter.builder()
                .name("테스트 쉼터")
                .facilityType1("공공건물")
                .build();

        // when & then
        assertThat(center.isIndoorFacility()).isTrue();
    }

    @Test
    @DisplayName("실내 시설 확인 - 실외 시설")
    void isIndoorFacility_Outdoor() {
        // given
        CoolingCenter center = CoolingCenter.builder()
                .name("테스트 쉼터")
                .facilityType1("공원")
                .build();

        // when & then
        assertThat(center.isIndoorFacility()).isFalse();
    }

    @Test
    @DisplayName("GeoPoint 인터페이스 구현 - 위도 반환")
    void getLatitude_Implementation() {
        // given
        CoolingCenter center = CoolingCenter.builder()
                .name("테스트 쉼터")
                .latitude(37.5665)
                .build();

        // when & then
        assertThat(center.getLatitude()).isEqualTo(37.5665);
    }

    @Test
    @DisplayName("GeoPoint 인터페이스 구현 - 경도 반환")
    void getLongitude_Implementation() {
        // given
        CoolingCenter center = CoolingCenter.builder()
                .name("테스트 쉼터")
                .longitude(126.9780)
                .build();

        // when & then
        assertThat(center.getLongitude()).isEqualTo(126.9780);
    }

    @Test
    @DisplayName("GeoPoint 인터페이스 구현 - null 좌표 처리")
    void getCoordinates_WithNullValues() {
        // given
        CoolingCenter center = CoolingCenter.builder()
                .name("테스트 쉼터")
                .build();

        // when & then
        assertThat(center.getLatitude()).isEqualTo(0.0);
        assertThat(center.getLongitude()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("쉼터 정보 업데이트")
    void updateCoolingCenter() {
        // given
        CoolingCenter center = CoolingCenter.builder()
                .name("기존 쉼터")
                .roadAddress("기존 주소")
                .facilityType1("기존 타입1")
                .facilityType2("기존 타입2")
                .capacity(30)
                .longitude(126.0000)
                .latitude(37.0000)
                .build();

        // when
        center.update(
                "새로운 쉼터",
                "새로운 주소",
                "새로운 타입1",
                "새로운 타입2",
                100,
                127.0000,
                38.0000
        );

        // then
        assertThat(center.getName()).isEqualTo("새로운 쉼터");
        assertThat(center.getRoadAddress()).isEqualTo("새로운 주소");
        assertThat(center.getFacilityType1()).isEqualTo("새로운 타입1");
        assertThat(center.getFacilityType2()).isEqualTo("새로운 타입2");
        assertThat(center.getCapacity()).isEqualTo(100);
        assertThat(center.getLongitude()).isEqualTo(127.0000);
        assertThat(center.getLatitude()).isEqualTo(38.0000);
        assertThat(center.getUpdatedAt()).isNotNull();
    }
}
