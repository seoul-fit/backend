package com.seoulfit.backend.publicdata.park.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoulfit.backend.publicdata.park.application.port.in.ParkQueryUseCase;
import com.seoulfit.backend.publicdata.park.domain.Park;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = SeoulParkController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
@ActiveProfiles("test")
@DisplayName("SeoulParkController 테스트")
class SeoulParkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ParkQueryUseCase parkQueryUseCase;

    @Test
    @DisplayName("모든 공원 조회 - 성공")
    void getAllParks_Success() throws Exception {
        // given
        List<Park> parks = createTestParks();
        given(parkQueryUseCase.getAllPark()).willReturn(parks);

        // when & then
        mockMvc.perform(get("/api/v1/parks/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("남산공원"))
                .andExpect(jsonPath("$[0].zone").value("중구"))
                .andExpect(jsonPath("$[1].name").value("한강공원"));
    }

    @Test
    @DisplayName("근처 공원 조회 - 성공")
    void getParksNearby_Success() throws Exception {
        // given
        List<Park> nearbyParks = createTestParks().subList(0, 1);
        given(parkQueryUseCase.getParkByLatitudeAndLongitude(anyString(), anyString()))
                .willReturn(nearbyParks);

        // when & then
        mockMvc.perform(get("/api/v1/parks/nearby")
                .param("latitude", "37.5665")
                .param("longitude", "126.9780")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("남산공원"));
    }

    @Test
    @DisplayName("근처 공원 조회 - 필수 파라미터 누락")
    void getParksNearby_MissingParameters() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/parks/nearby")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("공원 상세 조회 - 향후 구현 예정")
    void getParkDetail_NotImplemented() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/parks/detail")
                .param("parkId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private List<Park> createTestParks() {
        Park park1 = Park.builder()
                .parkIdx(1)
                .name("남산공원")
                .content("서울의 대표적인 도심 공원")
                .area("125,000㎡")
                .address("서울특별시 중구 남산공원길 105")
                .zone("중구")
                .longitude(126.9882)
                .latitude(37.5511)
                .build();

        Park park2 = Park.builder()
                .parkIdx(2)
                .name("한강공원")
                .content("한강을 따라 조성된 시민공원")
                .area("210,000㎡")
                .address("서울특별시 영등포구 여의동로 330")
                .zone("영등포구")
                .longitude(126.9356)
                .latitude(37.5219)
                .build();

        return Arrays.asList(park1, park2);
    }
}
