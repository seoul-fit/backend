package com.seoulfit.backend.publicdata.culture.adapter.out.custom;

import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CulturalEventRepository 커스텀 쿼리 테스트")
class CulturalEventRepositoryTest {

    @Autowired
    private CulturalEventRepository culturalEventRepository;

    private CulturalEvent event1;
    private CulturalEvent event2;
    private CulturalEvent event3;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        culturalEventRepository.deleteAll();
        
        // 강남구 무료 공연 (진행중)
        event1 = CulturalEvent.builder()
                .externalId("EVT001")
                .title("서울 필하모닉 오케스트라")
                .codeName("클래식")
                .district("강남구")
                .place("강남문화센터")
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().plusDays(5))
                .isFree("무료")
                .latitude(new BigDecimal("37.5172"))
                .longitude(new BigDecimal("127.0473"))
                .build();
        
        // 서초구 유료 전시 (진행중)
        event2 = CulturalEvent.builder()
                .externalId("EVT002")
                .title("현대미술 특별전")
                .codeName("전시/미술")
                .district("서초구")
                .place("서초아트센터")
                .startDate(LocalDate.now().minusDays(10))
                .endDate(LocalDate.now().plusDays(20))
                .isFree("유료")
                .latitude(new BigDecimal("37.4837"))
                .longitude(new BigDecimal("127.0324"))
                .build();
        
        // 강남구 무료 축제 (미래)
        event3 = CulturalEvent.builder()
                .externalId("EVT003")
                .title("강남 페스티벌")
                .codeName("축제-문화/예술")
                .district("강남구")
                .place("강남역 광장")
                .startDate(LocalDate.now().plusDays(30))
                .endDate(LocalDate.now().plusDays(35))
                .isFree("무료")
                .latitude(new BigDecimal("37.4980"))
                .longitude(new BigDecimal("127.0276"))
                .build();
        
        culturalEventRepository.saveAll(Arrays.asList(event1, event2, event3));
    }

    @Test
    @DisplayName("외부 ID로 문화행사 조회 - 성공")
    void findByExternalId_Success() {
        // when
        Optional<CulturalEvent> found = culturalEventRepository.findByExternalId("EVT001");
        
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("서울 필하모닉 오케스트라");
        assertThat(found.get().getDistrict()).isEqualTo("강남구");
    }

    @Test
    @DisplayName("외부 ID로 문화행사 존재 여부 확인")
    void existsByExternalId() {
        // when & then
        assertThat(culturalEventRepository.existsByExternalId("EVT001")).isTrue();
        assertThat(culturalEventRepository.existsByExternalId("EVT999")).isFalse();
    }

    @Test
    @DisplayName("특정 구에서 진행 중인 문화행사 조회")
    void findOngoingEventsByDistrict() {
        // given
        LocalDate currentDate = LocalDate.now();
        
        // when
        List<CulturalEvent> gangnamEvents = culturalEventRepository.findOngoingEventsByDistrict("강남구", currentDate);
        List<CulturalEvent> seochoEvents = culturalEventRepository.findOngoingEventsByDistrict("서초구", currentDate);
        
        // then
        assertThat(gangnamEvents).hasSize(1);
        assertThat(gangnamEvents.get(0).getExternalId()).isEqualTo("EVT001");
        
        assertThat(seochoEvents).hasSize(1);
        assertThat(seochoEvents.get(0).getExternalId()).isEqualTo("EVT002");
    }

    @Test
    @DisplayName("카테고리별 진행 중인 문화행사 조회")
    void findOngoingEventsByCategories() {
        // given
        LocalDate currentDate = LocalDate.now();
        List<String> categories = Arrays.asList("클래식", "전시/미술");
        
        // when
        List<CulturalEvent> events = culturalEventRepository.findOngoingEventsByCategories(categories, currentDate);
        
        // then
        assertThat(events).hasSize(2);
        assertThat(events).extracting(CulturalEvent::getCodeName)
                .containsExactlyInAnyOrder("클래식", "전시/미술");
    }

    @Test
    @DisplayName("날짜 범위로 문화행사 조회")
    void findEventsByDateRange() {
        // given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(40);
        
        // when
        List<CulturalEvent> events = culturalEventRepository.findEventsByDateRange(startDate, endDate);
        
        // then
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getExternalId()).isEqualTo("EVT003");
    }

    @Test
    @DisplayName("진행 중인 무료 문화행사 조회")
    void findOngoingFreeEvents() {
        // given
        LocalDate currentDate = LocalDate.now();
        
        // when
        List<CulturalEvent> freeEvents = culturalEventRepository.findOngoingFreeEvents(currentDate);
        
        // then
        assertThat(freeEvents).hasSize(1);
        assertThat(freeEvents.get(0).getExternalId()).isEqualTo("EVT001");
        assertThat(freeEvents.get(0).getIsFree()).isEqualTo("무료");
    }

    @Test
    @DisplayName("제목 키워드로 문화행사 검색")
    void findByTitleContainingIgnoreCase() {
        // when
        List<CulturalEvent> orchestraEvents = culturalEventRepository.findByTitleContainingIgnoreCase("오케스트라");
        List<CulturalEvent> festivalEvents = culturalEventRepository.findByTitleContainingIgnoreCase("페스티벌");
        
        // then
        assertThat(orchestraEvents).hasSize(1);
        assertThat(orchestraEvents.get(0).getTitle()).contains("오케스트라");
        
        assertThat(festivalEvents).hasSize(1);
        assertThat(festivalEvents.get(0).getTitle()).contains("페스티벌");
    }

    @Test
    @DisplayName("모든 카테고리 조회")
    void findAllCategories() {
        // when
        List<String> categories = culturalEventRepository.findAllCategories();
        
        // then
        assertThat(categories).hasSize(3);
        assertThat(categories).containsExactly("전시/미술", "축제-문화/예술", "클래식");
    }

    @Test
    @DisplayName("모든 구 목록 조회")
    void findAllDistricts() {
        // when
        List<String> districts = culturalEventRepository.findAllDistricts();
        
        // then
        assertThat(districts).hasSize(2);
        assertThat(districts).containsExactly("강남구", "서초구");
    }

    @Test
    @DisplayName("위치 기반 문화행사 조회 - 반경 내")
    void findWithInRadius_Success() {
        // given
        BigDecimal latitude = new BigDecimal("37.5000");  // 강남역 근처
        BigDecimal longitude = new BigDecimal("127.0300");
        double radiusKm = 5.0;
        
        // when
        List<CulturalEvent> nearbyEvents = culturalEventRepository.findWithInRadius(latitude, longitude, radiusKm);
        
        // then
        assertThat(nearbyEvents).isNotEmpty();
        // 거리순 정렬 확인 (가까운 순서대로)
        assertThat(nearbyEvents.get(0).getDistrict()).isIn("강남구", "서초구");
    }

    @Test
    @DisplayName("위치 기반 문화행사 조회 - 반경 외")
    void findWithInRadius_OutOfRange() {
        // given
        BigDecimal latitude = new BigDecimal("37.6000");  // 먼 지역
        BigDecimal longitude = new BigDecimal("127.2000");
        double radiusKm = 1.0; // 작은 반경
        
        // when
        List<CulturalEvent> nearbyEvents = culturalEventRepository.findWithInRadius(latitude, longitude, radiusKm);
        
        // then
        assertThat(nearbyEvents).isEmpty();
    }

    @Test
    @DisplayName("위치 정보가 없는 행사 제외 확인")
    void findWithInRadius_ExcludesNullLocation() {
        // given
        CulturalEvent noLocationEvent = CulturalEvent.builder()
                .externalId("EVT004")
                .title("위치 정보 없는 행사")
                .codeName("기타")
                .district("종로구")
                .place("미정")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .isFree("무료")
                .latitude(null)
                .longitude(null)
                .build();
        culturalEventRepository.save(noLocationEvent);
        
        BigDecimal latitude = new BigDecimal("37.5000");
        BigDecimal longitude = new BigDecimal("127.0300");
        double radiusKm = 100.0; // 큰 반경
        
        // when
        List<CulturalEvent> events = culturalEventRepository.findWithInRadius(latitude, longitude, radiusKm);
        
        // then
        assertThat(events).noneMatch(e -> e.getExternalId().equals("EVT004"));
    }
}