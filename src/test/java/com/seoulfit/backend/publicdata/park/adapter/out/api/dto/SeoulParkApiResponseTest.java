package com.seoulfit.backend.publicdata.park.adapter.out.api.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoulfit.backend.publicdata.park.domain.Park;
import com.seoulfit.backend.publicdata.park.infrastructure.mapper.ParkMapper;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SeoulParkApiResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void parsesCurrentSchemaAndKeepsWgs84CoordinatesInSeoul() throws Exception {
        try (InputStream fixture = getClass().getResourceAsStream(
                "/fixtures/seoul/search-park-info-service.json")) {
            SeoulParkApiResponse response = objectMapper.readValue(fixture, SeoulParkApiResponse.class);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getTotalCount()).isEqualTo(133);
            assertThat(response.getParkInfoList()).hasSize(2);

            List<Park> parks = ParkMapper.mapToEntity(response.getParkInfoList());
            assertThat(parks).allSatisfy(park -> {
                assertThat(park.getLatitude()).isBetween(37.4, 37.7);
                assertThat(park.getLongitude()).isBetween(126.7, 127.2);
            });
            assertThat(parks.getFirst().getParkIdx()).isEqualTo(1);
            assertThat(parks.getFirst().getName()).isEqualTo("남산공원");
            assertThat(parks.getFirst().getManagementDept()).isEqualTo("중부공원여가센터");
            assertThat(parks.getFirst().getOpenDate()).isEqualTo("1968.9.10");
        }
    }
}
