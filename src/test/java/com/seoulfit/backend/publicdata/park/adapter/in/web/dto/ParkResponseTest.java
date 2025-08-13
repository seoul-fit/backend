package com.seoulfit.backend.publicdata.park.adapter.in.web.dto;

import com.seoulfit.backend.publicdata.park.domain.Park;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ParkResponse DTO 테스트")
class ParkResponseTest {

    @Test
    @DisplayName("Park 도메인에서 ParkResponse 변환 - 성공")
    void from_Park_Success() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Park park = Park.builder()
                .parkIdx(1)
                .name("남산공원")
                .content("서울의 대표적인 도심 공원")
                .area("125,000㎡")
                .openDate("1968-01-01")
                .mainEquipment("산책로, 전망대, 놀이터")
                .mainPlants("소나무, 벚나무, 단풍나무")
                .imageUrl("https://example.com/park.jpg")
                .zone("중구")
                .address("서울특별시 중구 남산공원길 105")
                .managementDept("중구청 공원녹지과")
                .adminTel("02-1234-5678")
                .latitude(37.5511)
                .longitude(126.9882)
                .templateUrl("https://example.com")
                .build();

        // when
        ParkResponse response = ParkResponse.from(park);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(park.getId());
        assertThat(response.getParkIdx()).isEqualTo(park.getParkIdx());
        assertThat(response.getName()).isEqualTo(park.getName());
        assertThat(response.getContent()).isEqualTo(park.getContent());
        assertThat(response.getArea()).isEqualTo(park.getArea());
        assertThat(response.getOpenDate()).isEqualTo(park.getOpenDate());
        assertThat(response.getMainEquipment()).isEqualTo(park.getMainEquipment());
        assertThat(response.getMainPlants()).isEqualTo(park.getMainPlants());
        assertThat(response.getImageUrl()).isEqualTo(park.getImageUrl());
        assertThat(response.getZone()).isEqualTo(park.getZone());
        assertThat(response.getAddress()).isEqualTo(park.getAddress());
        assertThat(response.getManagementDept()).isEqualTo(park.getManagementDept());
        assertThat(response.getAdminTel()).isEqualTo(park.getAdminTel());
        assertThat(response.getLatitude()).isEqualTo(park.getLatitude());
        assertThat(response.getLongitude()).isEqualTo(park.getLongitude());
        assertThat(response.getTemplateUrl()).isEqualTo(park.getTemplateUrl());
    }

    @Test
    @DisplayName("Park 도메인에서 ParkResponse 변환 - null 값 처리")
    void from_Park_WithNullValues() {
        // given
        Park park = Park.builder()
                .name("기본 공원")
                .build();

        // when
        ParkResponse response = ParkResponse.from(park);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("기본 공원");
        assertThat(response.getContent()).isNull();
        assertThat(response.getArea()).isNull();
        assertThat(response.getImageUrl()).isNull();
        assertThat(response.getLatitude()).isNull();
        assertThat(response.getLongitude()).isNull();
    }

    @Test
    @DisplayName("Park 리스트에서 ParkResponse 리스트 변환 - 성공")
    void from_ParkList_Success() {
        // given
        Park park1 = Park.builder()
                .parkIdx(1)
                .name("남산공원")
                .zone("중구")
                .build();

        Park park2 = Park.builder()
                .parkIdx(2)
                .name("한강공원")
                .zone("영등포구")
                .build();

        List<Park> parks = Arrays.asList(park1, park2);

        // when
        List<ParkResponse> responses = ParkResponse.from(parks);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getName()).isEqualTo("남산공원");
        assertThat(responses.get(0).getZone()).isEqualTo("중구");
        assertThat(responses.get(1).getName()).isEqualTo("한강공원");
        assertThat(responses.get(1).getZone()).isEqualTo("영등포구");
    }

    @Test
    @DisplayName("빈 Park 리스트에서 빈 ParkResponse 리스트 변환")
    void from_EmptyParkList() {
        // given
        List<Park> parks = Arrays.asList();

        // when
        List<ParkResponse> responses = ParkResponse.from(parks);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();
    }
}
