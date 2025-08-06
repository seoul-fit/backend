package com.seoulfit.backend.application.service;

import com.seoulfit.backend.culture.domain.CulturalEvent;
import com.seoulfit.backend.culture.adapter.out.CulturalEventRepository;
import com.seoulfit.backend.tmp.application.service.CulturalEventService;
import com.seoulfit.backend.tmp.application.service.SeoulCulturalApiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CulturalEventServiceTest {

    @Mock
    private CulturalEventRepository culturalEventRepository;

    @Mock
    private SeoulCulturalApiService seoulCulturalApiService;

    @InjectMocks
    private CulturalEventService culturalEventService;

    @Test
    @DisplayName("ID로 문화행사 조회 - 성공")
    void findById_Success() {
        // given
        Long eventId = 1L;
        CulturalEvent event = CulturalEvent.builder()
                .title("테스트 문화행사")
                .codeName("콘서트")
                .district("강남구")
                .place("테스트 장소")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .latitude(new BigDecimal("37.5665"))
                .longitude(new BigDecimal("126.9780"))
                .isFree("무료")
                .externalId("test_event_1")
                .build();

        given(culturalEventRepository.findById(eventId)).willReturn(Optional.of(event));

        // when
        CulturalEvent result = culturalEventService.findById(eventId);

        // then
        assertThat(result.getTitle()).isEqualTo("테스트 문화행사");
        assertThat(result.getCodeName()).isEqualTo("콘서트");
        assertThat(result.getDistrict()).isEqualTo("강남구");
    }

    @Test
    @DisplayName("ID로 문화행사 조회 - 존재하지 않는 경우")
    void findById_NotFound_ThrowsException() {
        // given
        Long eventId = 999L;
        given(culturalEventRepository.findById(eventId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> culturalEventService.findById(eventId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("문화행사를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("진행중인 무료 행사 조회")
    void findFreeEvents_Success() {
        // given
        LocalDate today = LocalDate.now();
        List<CulturalEvent> freeEvents = List.of(
                CulturalEvent.builder()
                        .title("무료 콘서트")
                        .isFree("무료")
                        .startDate(today.minusDays(1))
                        .endDate(today.plusDays(1))
                        .externalId("free_event_1")
                        .build()
        );

        given(culturalEventRepository.findOngoingFreeEvents(any(LocalDate.class)))
                .willReturn(freeEvents);

        // when
        List<CulturalEvent> result = culturalEventService.findFreeEvents();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("무료 콘서트");
        assertThat(result.get(0).isFreeEvent()).isTrue();
    }

    @Test
    @DisplayName("키워드로 문화행사 검색")
    void searchEventsByKeyword_Success() {
        // given
        String keyword = "콘서트";
        List<CulturalEvent> searchResults = List.of(
                CulturalEvent.builder()
                        .title("클래식 콘서트")
                        .codeName("클래식")
                        .externalId("classic_concert_1")
                        .build(),
                CulturalEvent.builder()
                        .title("재즈 콘서트")
                        .codeName("콘서트")
                        .externalId("jazz_concert_1")
                        .build()
        );

        given(culturalEventRepository.findByTitleContainingIgnoreCase(keyword))
                .willReturn(searchResults);

        // when
        List<CulturalEvent> result = culturalEventService.searchEventsByKeyword(keyword);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).contains("콘서트");
        assertThat(result.get(1).getTitle()).contains("콘서트");
    }
}
