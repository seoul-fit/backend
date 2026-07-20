package com.seoulfit.backend.publicdata.culture.infrastructure.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoulfit.backend.publicdata.culture.adapter.in.web.dto.res.SeoulApiResponse;
import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class CulturalEventMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CulturalEventMapper mapper = new CulturalEventMapper();

    @Test
    void mapsLotToLongitudeAndLatToLatitude() throws Exception {
        try (InputStream fixture = getClass().getResourceAsStream(
                "/fixtures/seoul/cultural-event-info.json")) {
            SeoulApiResponse response = objectMapper.readValue(fixture, SeoulApiResponse.class);

            CulturalEvent event = mapper.mapToEntity(
                    response.getCulturalEventInfo().getRow()).getFirst();
            assertThat(event.getLatitude().doubleValue()).isBetween(37.4, 37.7);
            assertThat(event.getLongitude().doubleValue()).isBetween(126.7, 127.2);
            assertThat(event.getLatitude().doubleValue()).isEqualTo(37.6202544613023);
            assertThat(event.getLongitude().doubleValue()).isEqualTo(127.044324732036);
        }
    }
}
