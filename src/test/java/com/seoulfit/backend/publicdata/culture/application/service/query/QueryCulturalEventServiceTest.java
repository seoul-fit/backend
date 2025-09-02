package com.seoulfit.backend.publicdata.culture.application.service.query;

import com.seoulfit.backend.publicdata.culture.application.port.out.query.QueryCulturalEventPort;
import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

/**
 * QueryCulturalEventService 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("QueryCulturalEventService 단위 테스트")
class QueryCulturalEventServiceTest {

    @Mock
    private QueryCulturalEventPort culturalEventPort;
    
    @InjectMocks
    private QueryCulturalEventService queryCulturalEventService;
    
    private List<CulturalEvent> mockCulturalEvents;
    private CulturalEvent culturalEvent1;
    private CulturalEvent culturalEvent2;
    private CulturalEvent culturalEvent3;
    
    @BeforeEach
    void setUp() {
        culturalEvent1 = createCulturalEvent(1L, "EVT001", "서울시 음악 페스티벌", "음악", 37.5665, 126.9780);
        culturalEvent2 = createCulturalEvent(2L, "EVT002", "한강 불꽃축제", "축제", 37.5172, 126.9631);
        culturalEvent3 = createCulturalEvent(3L, "EVT003", "서울 미술관 특별전", "전시", 37.5797, 126.9770);
        
        mockCulturalEvents = Arrays.asList(culturalEvent1, culturalEvent2, culturalEvent3);
    }
    
    @Nested
    @DisplayName("전체 문화행사 조회 테스트")
    class GetAllCulturalEventsTest {
        
        @Test
        @DisplayName("전체 문화행사 조회 - 성공")
        void getAllCulturalEvents_Success() {
            // given
            when(culturalEventPort.getAllCulturalEvent()).thenReturn(mockCulturalEvents);
            
            // when
            List<CulturalEvent> result = queryCulturalEventService.getAllCulturalEvents();
            
            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getTitle()).isEqualTo("서울시 음악 페스티벌");
            assertThat(result.get(1).getTitle()).isEqualTo("한강 불꽃축제");
            assertThat(result.get(2).getTitle()).isEqualTo("서울 미술관 특별전");
            
            // getAllCulturalEvent가 2번 호출됨 (로깅용 1번, 실제 반환용 1번)
            verify(culturalEventPort, times(2)).getAllCulturalEvent();
        }
        
        @Test
        @DisplayName("전체 문화행사 조회 - 빈 목록")
        void getAllCulturalEvents_EmptyList() {
            // given
            when(culturalEventPort.getAllCulturalEvent()).thenReturn(Collections.emptyList());
            
            // when
            List<CulturalEvent> result = queryCulturalEventService.getAllCulturalEvents();
            
            // then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
            verify(culturalEventPort, times(2)).getAllCulturalEvent();
        }
        
        @Test
        @DisplayName("전체 문화행사 조회 - 대용량 데이터")
        void getAllCulturalEvents_LargeDataset() {
            // given
            List<CulturalEvent> largeList = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                largeList.add(createCulturalEvent(
                    (long) i,
                    "EVT" + String.format("%04d", i),
                    "이벤트 " + i,
                    "카테고리",
                    37.5665 + (i * 0.001),
                    126.9780 + (i * 0.001)
                ));
            }
            when(culturalEventPort.getAllCulturalEvent()).thenReturn(largeList);
            
            // when
            List<CulturalEvent> result = queryCulturalEventService.getAllCulturalEvents();
            
            // then
            assertThat(result).hasSize(1000);
            verify(culturalEventPort, times(2)).getAllCulturalEvent();
        }
        
        @Test
        @DisplayName("전체 문화행사 조회 - null 반환 처리")
        void getAllCulturalEvents_NullResponse() {
            // given
            when(culturalEventPort.getAllCulturalEvent()).thenReturn(null);
            
            // when & then - NullPointerException이 발생해야 함
            assertThatThrownBy(() -> queryCulturalEventService.getAllCulturalEvents())
                    .isInstanceOf(NullPointerException.class);
            
            verify(culturalEventPort).getAllCulturalEvent();
        }
    }
    
    @Nested
    @DisplayName("위치 기반 문화행사 조회 테스트")
    class GetCulturalEventByLocationTest {
        
        @Test
        @DisplayName("위치 기반 문화행사 조회 - 성공")
        void getCulturalEventByLocation_Success() {
            // given
            String latitude = "37.5665";
            String longitude = "126.9780";
            List<CulturalEvent> nearbyEvents = Arrays.asList(culturalEvent1, culturalEvent2);
            
            when(culturalEventPort.getCulturalEventLocation(37.5665, 126.9780))
                    .thenReturn(nearbyEvents);
            
            // when
            List<CulturalEvent> result = queryCulturalEventService
                    .getCulturalEventByLatitudeAndLongitude(latitude, longitude);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).contains(culturalEvent1, culturalEvent2);
            verify(culturalEventPort).getCulturalEventLocation(37.5665, 126.9780);
        }
        
        @Test
        @DisplayName("위치 기반 문화행사 조회 - 정확한 좌표 일치")
        void getCulturalEventByLocation_ExactMatch() {
            // given
            String latitude = "37.5172";
            String longitude = "126.9631";
            List<CulturalEvent> exactMatch = Arrays.asList(culturalEvent2);
            
            when(culturalEventPort.getCulturalEventLocation(37.5172, 126.9631))
                    .thenReturn(exactMatch);
            
            // when
            List<CulturalEvent> result = queryCulturalEventService
                    .getCulturalEventByLatitudeAndLongitude(latitude, longitude);
            
            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("한강 불꽃축제");
            verify(culturalEventPort).getCulturalEventLocation(37.5172, 126.9631);
        }
        
        @Test
        @DisplayName("위치 기반 문화행사 조회 - 결과 없음")
        void getCulturalEventByLocation_NoResults() {
            // given
            String latitude = "35.0000";
            String longitude = "125.0000";
            
            when(culturalEventPort.getCulturalEventLocation(35.0000, 125.0000))
                    .thenReturn(Collections.emptyList());
            
            // when
            List<CulturalEvent> result = queryCulturalEventService
                    .getCulturalEventByLatitudeAndLongitude(latitude, longitude);
            
            // then
            assertThat(result).isEmpty();
            verify(culturalEventPort).getCulturalEventLocation(35.0000, 125.0000);
        }
        
        @Test
        @DisplayName("위치 기반 문화행사 조회 - 잘못된 좌표 형식")
        void getCulturalEventByLocation_InvalidFormat() {
            // given
            String latitude = "invalid";
            String longitude = "126.9780";
            
            // when & then
            assertThatThrownBy(() -> 
                queryCulturalEventService.getCulturalEventByLatitudeAndLongitude(latitude, longitude))
                    .isInstanceOf(NumberFormatException.class);
            
            verify(culturalEventPort, never()).getCulturalEventLocation(anyDouble(), anyDouble());
        }
        
        @Test
        @DisplayName("위치 기반 문화행사 조회 - null 좌표")
        void getCulturalEventByLocation_NullCoordinates() {
            // given
            String latitude = null;
            String longitude = "126.9780";
            
            // when & then
            assertThatThrownBy(() -> 
                queryCulturalEventService.getCulturalEventByLatitudeAndLongitude(latitude, longitude))
                    .isInstanceOf(NullPointerException.class);
            
            verify(culturalEventPort, never()).getCulturalEventLocation(anyDouble(), anyDouble());
        }
        
        @Test
        @DisplayName("위치 기반 문화행사 조회 - 경계값 테스트")
        void getCulturalEventByLocation_BoundaryValues() {
            // given
            String latitude = "90.0000";  // 북극
            String longitude = "180.0000"; // 국제 날짜 변경선
            
            when(culturalEventPort.getCulturalEventLocation(90.0000, 180.0000))
                    .thenReturn(Collections.emptyList());
            
            // when
            List<CulturalEvent> result = queryCulturalEventService
                    .getCulturalEventByLatitudeAndLongitude(latitude, longitude);
            
            // then
            assertThat(result).isEmpty();
            verify(culturalEventPort).getCulturalEventLocation(90.0000, 180.0000);
        }
        
        @Test
        @DisplayName("위치 기반 문화행사 조회 - 음수 좌표")
        void getCulturalEventByLocation_NegativeCoordinates() {
            // given
            String latitude = "-37.5665";
            String longitude = "-126.9780";
            
            when(culturalEventPort.getCulturalEventLocation(-37.5665, -126.9780))
                    .thenReturn(Collections.emptyList());
            
            // when
            List<CulturalEvent> result = queryCulturalEventService
                    .getCulturalEventByLatitudeAndLongitude(latitude, longitude);
            
            // then
            assertThat(result).isEmpty();
            verify(culturalEventPort).getCulturalEventLocation(-37.5665, -126.9780);
        }
        
        @Test
        @DisplayName("위치 기반 문화행사 조회 - 소수점 많은 좌표")
        void getCulturalEventByLocation_HighPrecisionCoordinates() {
            // given
            String latitude = "37.56653214789";
            String longitude = "126.97801234567";
            
            when(culturalEventPort.getCulturalEventLocation(37.56653214789, 126.97801234567))
                    .thenReturn(mockCulturalEvents);
            
            // when
            List<CulturalEvent> result = queryCulturalEventService
                    .getCulturalEventByLatitudeAndLongitude(latitude, longitude);
            
            // then
            assertThat(result).hasSize(3);
            verify(culturalEventPort).getCulturalEventLocation(37.56653214789, 126.97801234567);
        }
        
        @Test
        @DisplayName("위치 기반 문화행사 조회 - 빈 문자열")
        void getCulturalEventByLocation_EmptyString() {
            // given
            String latitude = "";
            String longitude = "";
            
            // when & then
            assertThatThrownBy(() -> 
                queryCulturalEventService.getCulturalEventByLatitudeAndLongitude(latitude, longitude))
                    .isInstanceOf(NumberFormatException.class);
            
            verify(culturalEventPort, never()).getCulturalEventLocation(anyDouble(), anyDouble());
        }
        
        @Test
        @DisplayName("위치 기반 문화행사 조회 - 공백 포함 좌표")
        void getCulturalEventByLocation_CoordinatesWithSpaces() {
            // given
            String latitude = " 37.5665 ";
            String longitude = " 126.9780 ";
            
            when(culturalEventPort.getCulturalEventLocation(37.5665, 126.9780))
                    .thenReturn(mockCulturalEvents);
            
            // when
            List<CulturalEvent> result = queryCulturalEventService
                    .getCulturalEventByLatitudeAndLongitude(latitude, longitude);
            
            // then
            assertThat(result).hasSize(3);
            verify(culturalEventPort).getCulturalEventLocation(37.5665, 126.9780);
        }
    }
    
    // Helper methods
    private CulturalEvent createCulturalEvent(Long id, String cultcode, String title, 
                                             String category, Double lat, Double lng) {
        CulturalEvent event = CulturalEvent.builder()
                .title(title)
                .codeName(category)
                .district("서울시")
                .place("서울 광장")
                .orgName("서울시청")
                .useTarget("전체")
                .useFee("무료")
                .player("시민")
                .program("문화 프로그램")
                .etcDesc("기타 설명")
                .orgLink("http://seoul.go.kr")
                .mainImg("http://image.url")
                .registrationDate("2024-01-01")
                .ticket("현장구매")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .themeCode("THEME001")
                .eventDate("2024-01-01~2024-01-31")
                .latitude(BigDecimal.valueOf(lat))
                .longitude(BigDecimal.valueOf(lng))
                .build();
        
        // Set ID using reflection
        try {
            java.lang.reflect.Field idField = CulturalEvent.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(event, id);
        } catch (Exception e) {
            // Ignore in test
        }
        
        return event;
    }
}