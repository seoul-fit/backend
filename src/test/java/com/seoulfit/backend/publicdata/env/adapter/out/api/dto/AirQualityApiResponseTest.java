package com.seoulfit.backend.publicdata.env.adapter.out.api.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoulfit.backend.publicdata.env.domain.AirQuality;
import com.seoulfit.backend.publicdata.env.infrastructure.mapper.AirQualityMapper;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AirQualityApiResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void parsesCurrentSchemaNumericValuesAndCompactMeasurementTime() throws Exception {
        try (InputStream fixture = getClass().getResourceAsStream(
                "/fixtures/seoul/realtime-city-air.json")) {
            AirQualityApiResponse response = objectMapper.readValue(fixture, AirQualityApiResponse.class);

            assertThat(response.getRealtimeCityAir().getResult().getCode()).isEqualTo("INFO-000");
            assertThat(response.getRealtimeCityAir().getRow()).hasSize(2);

            List<AirQuality> entities = AirQualityMapper.toEntity(response.getRealtimeCityAir());
            AirQuality jongno = entities.get(1);
            assertThat(jongno.getMsrDt()).isEqualTo(LocalDateTime.of(2026, 7, 20, 13, 0));
            assertThat(jongno.getMsrRgnNm()).isEqualTo("도심권");
            assertThat(jongno.getMsrSteNm()).isEqualTo("종로구");
            assertThat(jongno.getPm10Value()).isEqualTo(11);
            assertThat(jongno.getPm25Value()).isEqualTo(10);
            assertThat(jongno.getO3Value()).isEqualTo(0.033);
            assertThat(jongno.getKhaiValue()).isEqualTo(53);
            assertThat(jongno.getPm1024hAvg()).isNull();
            assertThat(jongno.getPm2524hAvg()).isNull();
        }
    }
}
