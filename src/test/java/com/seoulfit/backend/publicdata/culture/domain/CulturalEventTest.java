package com.seoulfit.backend.publicdata.culture.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CulturalEvent 도메인 테스트")
class CulturalEventTest {

    @Test
    @DisplayName("CulturalEvent 생성 - 성공")
    void createCulturalEvent_Success() {
        // given & when
        CulturalEvent event = CulturalEvent.builder()
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

        // then
        assertThat(event.getTitle()).isEqualTo("서울 미술 전시회");
        assertThat(event.getDistrict()).isEqualTo("중구");
        assertThat(event.getCodeName()).isEqualTo("전시/미술");
        assertThat(event.getStartDate()).isEqualTo(LocalDate.of(2025, 8, 15));
        assertThat(event.getEndDate()).isEqualTo(LocalDate.of(2025, 8, 20));
    }

    @Test
    @DisplayName("무료 행사 확인 - 무료")
    void isFreeEvent_Free() {
        // given
        CulturalEvent event = CulturalEvent.builder()
                .title("무료 전시회")
                .isFree("무료")
                .build();

        // when & then
        assertThat(event.isFreeEvent()).isTrue();
    }

    @Test
    @DisplayName("무료 행사 확인 - 유료")
    void isFreeEvent_Paid() {
        // given
        CulturalEvent event = CulturalEvent.builder()
                .title("유료 전시회")
                .isFree("유료")
                .build();

        // when & then
        assertThat(event.isFreeEvent()).isFalse();
    }

    @Test
    @DisplayName("무료 행사 확인 - null")
    void isFreeEvent_Null() {
        // given
        CulturalEvent event = CulturalEvent.builder()
                .title("전시회")
                .build();

        // when & then
        assertThat(event.isFreeEvent()).isFalse();
    }

    @Test
    @DisplayName("진행 중인 행사 확인 - 진행 중")
    void isOngoing_InProgress() {
        // given
        LocalDate currentDate = LocalDate.of(2025, 8, 17);
        CulturalEvent event = CulturalEvent.builder()
                .title("진행 중인 전시회")
                .startDate(LocalDate.of(2025, 8, 15))
                .endDate(LocalDate.of(2025, 8, 20))
                .build();

        // when & then
        assertThat(event.isOngoing(currentDate)).isTrue();
    }

    @Test
    @DisplayName("진행 중인 행사 확인 - 시작 전")
    void isOngoing_BeforeStart() {
        // given
        LocalDate currentDate = LocalDate.of(2025, 8, 10);
        CulturalEvent event = CulturalEvent.builder()
                .title("시작 전 전시회")
                .startDate(LocalDate.of(2025, 8, 15))
                .endDate(LocalDate.of(2025, 8, 20))
                .build();

        // when & then
        assertThat(event.isOngoing(currentDate)).isFalse();
    }

    @Test
    @DisplayName("진행 중인 행사 확인 - 종료 후")
    void isOngoing_AfterEnd() {
        // given
        LocalDate currentDate = LocalDate.of(2025, 8, 25);
        CulturalEvent event = CulturalEvent.builder()
                .title("종료된 전시회")
                .startDate(LocalDate.of(2025, 8, 15))
                .endDate(LocalDate.of(2025, 8, 20))
                .build();

        // when & then
        assertThat(event.isOngoing(currentDate)).isFalse();
    }

    @Test
    @DisplayName("진행 중인 행사 확인 - 날짜 정보 없음")
    void isOngoing_NoDates() {
        // given
        LocalDate currentDate = LocalDate.of(2025, 8, 17);
        CulturalEvent event = CulturalEvent.builder()
                .title("날짜 정보 없는 전시회")
                .build();

        // when & then
        assertThat(event.isOngoing(currentDate)).isFalse();
    }

    @Test
    @DisplayName("예정된 행사 확인 - 예정됨")
    void isUpcoming_Upcoming() {
        // given
        LocalDate currentDate = LocalDate.of(2025, 8, 10);
        CulturalEvent event = CulturalEvent.builder()
                .title("예정된 전시회")
                .startDate(LocalDate.of(2025, 8, 15))
                .build();

        // when & then
        assertThat(event.isUpcoming(currentDate)).isTrue();
    }

    @Test
    @DisplayName("예정된 행사 확인 - 이미 시작됨")
    void isUpcoming_AlreadyStarted() {
        // given
        LocalDate currentDate = LocalDate.of(2025, 8, 17);
        CulturalEvent event = CulturalEvent.builder()
                .title("이미 시작된 전시회")
                .startDate(LocalDate.of(2025, 8, 15))
                .build();

        // when & then
        assertThat(event.isUpcoming(currentDate)).isFalse();
    }

    @Test
    @DisplayName("예정된 행사 확인 - 시작일 정보 없음")
    void isUpcoming_NoStartDate() {
        // given
        LocalDate currentDate = LocalDate.of(2025, 8, 17);
        CulturalEvent event = CulturalEvent.builder()
                .title("시작일 정보 없는 전시회")
                .build();

        // when & then
        assertThat(event.isUpcoming(currentDate)).isFalse();
    }

    @Test
    @DisplayName("행사 기간 경계값 테스트 - 시작일")
    void isOngoing_StartDate() {
        // given
        LocalDate startDate = LocalDate.of(2025, 8, 15);
        CulturalEvent event = CulturalEvent.builder()
                .title("경계값 테스트 전시회")
                .startDate(startDate)
                .endDate(LocalDate.of(2025, 8, 20))
                .build();

        // when & then
        assertThat(event.isOngoing(startDate)).isTrue();
    }

    @Test
    @DisplayName("행사 기간 경계값 테스트 - 종료일")
    void isOngoing_EndDate() {
        // given
        LocalDate endDate = LocalDate.of(2025, 8, 20);
        CulturalEvent event = CulturalEvent.builder()
                .title("경계값 테스트 전시회")
                .startDate(LocalDate.of(2025, 8, 15))
                .endDate(endDate)
                .build();

        // when & then
        assertThat(event.isOngoing(endDate)).isTrue();
    }
}
