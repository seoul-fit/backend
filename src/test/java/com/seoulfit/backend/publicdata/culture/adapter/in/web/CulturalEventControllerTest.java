package com.seoulfit.backend.publicdata.culture.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoulfit.backend.publicdata.culture.application.port.in.QueryCulturalEventsUseCase;
import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = CulturalEventController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
@ActiveProfiles("test")
@DisplayName("CulturalEventController 테스트")
class CulturalEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QueryCulturalEventsUseCase queryCulturalEventsUseCase;

    @Test
    @DisplayName("모든 문화행사 조회 - 성공")
    void getAllCulturalEvents_Success() throws Exception {
        // given
        List<CulturalEvent> events = createTestEvents();
        given(queryCulturalEventsUseCase.getAllCulturalEvents()).willReturn(events);

        // when & then
        mockMvc.perform(get("/api/v1/cultural-events/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("서울 미술 전시회"))
                .andExpect(jsonPath("$[0].district").value("중구"))
                .andExpect(jsonPath("$[1].title").value("클래식 콘서트"));
    }

    @Test
    @DisplayName("근처 문화행사 조회 - 성공")
    void getNearbyEvents_Success() throws Exception {
        // given
        List<CulturalEvent> nearbyEvents = createTestEvents().subList(0, 1);
        given(queryCulturalEventsUseCase.getCulturalEventByLatitudeAndLongitude(anyString(), anyString()))
                .willReturn(nearbyEvents);

        // when & then
        mockMvc.perform(get("/api/v1/cultural-events/nearby")
                .param("latitude", "37.5665")
                .param("longitude", "126.9780")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("서울 미술 전시회"));
    }

    @Test
    @DisplayName("근처 문화행사 조회 - 필수 파라미터 누락")
    void getNearbyEvents_MissingParameters() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/cultural-events/nearby")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("문화행사 조회 - 서버 오류 처리")
    void getAllCulturalEvents_ServerError() throws Exception {
        // given
        given(queryCulturalEventsUseCase.getAllCulturalEvents())
                .willThrow(new RuntimeException("Database connection failed"));

        // when & then
        mockMvc.perform(get("/api/v1/cultural-events/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    private List<CulturalEvent> createTestEvents() {
        CulturalEvent event1 = CulturalEvent.builder()
                .codeName("전시/미술")
                .district("중구")
                .title("서울 미술 전시회")
                .eventDate("2025-08-15 ~ 2025-08-20")
                .startDate(LocalDate.of(2025, 8, 15))
                .endDate(LocalDate.of(2025, 8, 20))
                .place("서울시립미술관")
                .orgName("서울시립미술관")
                .useTarget("전체")
                .useFee("무료")
                .latitude(new BigDecimal("37.5665"))
                .longitude(new BigDecimal("126.9780"))
                .isFree("무료")
                .build();

        CulturalEvent event2 = CulturalEvent.builder()
                .codeName("콘서트")
                .district("강남구")
                .title("클래식 콘서트")
                .eventDate("2025-08-25 ~ 2025-08-25")
                .startDate(LocalDate.of(2025, 8, 25))
                .endDate(LocalDate.of(2025, 8, 25))
                .place("예술의전당")
                .orgName("예술의전당")
                .useTarget("전체")
                .useFee("유료")
                .latitude(new BigDecimal("37.4782"))
                .longitude(new BigDecimal("127.0122"))
                .isFree("유료")
                .build();

        return Arrays.asList(event1, event2);
    }
}
