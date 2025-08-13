package com.seoulfit.backend.geo;

import com.seoulfit.backend.geo.application.port.in.GeocodingUseCase;
import com.seoulfit.backend.geo.domain.AdministrativeDistrict;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("지오코딩 통합 테스트")
class GeocodingIntegrationTest {

    @Autowired
    private GeocodingUseCase geocodingUseCase;

    @Test
    @DisplayName("서울 지역 좌표로 행정구역 조회")
    void testSeoulCoordinate() {
        // given - 서울시청 좌표
        double latitude = 37.5665;
        double longitude = 126.9780;

        // when
        Optional<AdministrativeDistrict> result = geocodingUseCase
                .getAdministrativeDistrict(latitude, longitude);

        // then
        assertThat(result).isPresent();
        AdministrativeDistrict district = result.get();
        
        assertThat(district.getSidoName()).isEqualTo("서울특별시");
        assertThat(district.getAdminCode()).isNotNull();
        assertThat(district.getFullAddress()).contains("서울특별시");
        
        System.out.println("조회 결과: " + district.getFullAddress());
        System.out.println("행정구역 코드: " + district.getAdminCode());
    }

    @Test
    @DisplayName("서울 밖 좌표로 조회 - 결과 없음")
    void testOutsideSeoul() {
        // given - 부산 해운대 좌표
        double latitude = 35.1796;
        double longitude = 129.0756;

        // when
        Optional<AdministrativeDistrict> result = geocodingUseCase
                .getAdministrativeDistrict(latitude, longitude);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("유효하지 않은 좌표로 조회")
    void testInvalidCoordinate() {
        // given
        double latitude = 0.0;
        double longitude = 0.0;

        // when
        Optional<AdministrativeDistrict> result = geocodingUseCase
                .getAdministrativeDistrict(latitude, longitude);

        // then
        assertThat(result).isEmpty();
    }
}
