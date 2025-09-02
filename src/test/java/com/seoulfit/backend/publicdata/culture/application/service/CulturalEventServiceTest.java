package com.seoulfit.backend.publicdata.culture.application.service;

import com.seoulfit.backend.publicdata.culture.adapter.in.web.dto.res.SeoulApiResponse;
import com.seoulfit.backend.publicdata.culture.adapter.out.custom.CulturalEventRepository;
import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import com.seoulfit.backend.publicdata.culture.infrastructure.mapper.CulturalEventMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CulturalEventService 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CulturalEventService 단위 테스트")
class CulturalEventServiceTest {

    @Mock
    private EntityManager entityManager;
    
    @Mock
    private CulturalEventRepository culturalEventRepository;
    
    @Mock
    private CulturalEventMapper culturalEventMapper;
    
    @Mock
    private SeoulCulturalApiService seoulCulturalApiService;
    
    @Mock
    private Query nativeQuery;
    
    @InjectMocks
    private CulturalEventService culturalEventService;
    
    private SeoulApiResponse mockResponse;
    private List<SeoulApiResponse.CulturalEventData> mockEventData;
    private List<CulturalEvent> mockCulturalEvents;
    
    @BeforeEach
    void setUp() {
        // Mock API Response 설정
        mockResponse = mock(SeoulApiResponse.class);
        SeoulApiResponse.CulturalEventInfo eventInfo = mock(SeoulApiResponse.CulturalEventInfo.class);
        SeoulApiResponse.Result result = mock(SeoulApiResponse.Result.class);
        
        // Mock Event Data 생성
        mockEventData = Arrays.asList(
            createMockEventData("EVT001", "서울시 음악 페스티벌", "음악"),
            createMockEventData("EVT002", "한강 불꽃축제", "축제"),
            createMockEventData("EVT003", "서울 미술관 특별전", "전시")
        );
        
        // Mock Cultural Events 생성
        mockCulturalEvents = Arrays.asList(
            createCulturalEvent("EVT001", "서울시 음악 페스티벌", "음악"),
            createCulturalEvent("EVT002", "한강 불꽃축제", "축제"),
            createCulturalEvent("EVT003", "서울 미술관 특별전", "전시")
        );
        
        // Response 설정
        when(mockResponse.getCulturalEventInfo()).thenReturn(eventInfo);
        when(eventInfo.getResult()).thenReturn(result);
        when(eventInfo.getRow()).thenReturn(mockEventData);
        when(result.getCode()).thenReturn("INFO-000");
        when(result.getMessage()).thenReturn("정상 처리되었습니다");
    }
    
    @Nested
    @DisplayName("문화행사 저장 테스트")
    class SaveCultureEventsTest {
        
        @Test
        @DisplayName("문화행사 저장 - 성공")
        void saveCultureEvents_Success() {
            // given
            when(seoulCulturalApiService.isApiHealthy()).thenReturn(true);
            when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
            when(nativeQuery.executeUpdate()).thenReturn(0);
            
            when(seoulCulturalApiService.fetchAllCulturalEvents(1, 1000)).thenReturn(mockResponse);
            when(seoulCulturalApiService.fetchAllCulturalEvents(1001, 2000)).thenReturn(mockResponse);
            when(seoulCulturalApiService.fetchAllCulturalEvents(2001, 3000)).thenReturn(mockResponse);
            when(seoulCulturalApiService.fetchAllCulturalEvents(3001, 4000)).thenReturn(mockResponse);
            
            when(mockResponse.isValid()).thenReturn(true);
            when(mockResponse.isSuccess()).thenReturn(true);
            when(mockResponse.hasData()).thenReturn(true);
            
            when(culturalEventMapper.mapToEntity(anyList())).thenReturn(mockCulturalEvents);
            when(culturalEventRepository.saveAll(anyList())).thenReturn(mockCulturalEvents);
            
            // when
            int savedCount = culturalEventService.saveCultureEvents();
            
            // then
            assertThat(savedCount).isEqualTo(3);
            verify(entityManager).createNativeQuery("TRUNCATE TABLE cultural_events");
            verify(seoulCulturalApiService, times(4)).fetchAllCulturalEvents(anyInt(), anyInt());
            verify(culturalEventMapper, times(4)).mapToEntity(anyList());
            verify(culturalEventRepository, times(4)).saveAll(anyList());
        }
        
        @Test
        @DisplayName("문화행사 저장 - API 상태 불량 경고")
        void saveCultureEvents_ApiUnhealthyWarning() {
            // given
            when(seoulCulturalApiService.isApiHealthy()).thenReturn(false);
            when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
            when(nativeQuery.executeUpdate()).thenReturn(0);
            
            when(seoulCulturalApiService.fetchAllCulturalEvents(anyInt(), anyInt())).thenReturn(mockResponse);
            when(mockResponse.isValid()).thenReturn(true);
            when(mockResponse.isSuccess()).thenReturn(true);
            when(mockResponse.hasData()).thenReturn(true);
            
            when(culturalEventMapper.mapToEntity(anyList())).thenReturn(mockCulturalEvents);
            when(culturalEventRepository.saveAll(anyList())).thenReturn(mockCulturalEvents);
            
            // when
            int savedCount = culturalEventService.saveCultureEvents();
            
            // then
            assertThat(savedCount).isEqualTo(3);
            verify(seoulCulturalApiService).isApiHealthy();
        }
        
        @Test
        @DisplayName("문화행사 저장 - 유효하지 않은 응답")
        void saveCultureEvents_InvalidResponse() {
            // given
            when(seoulCulturalApiService.isApiHealthy()).thenReturn(true);
            when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
            when(nativeQuery.executeUpdate()).thenReturn(0);
            
            when(seoulCulturalApiService.fetchAllCulturalEvents(anyInt(), anyInt())).thenReturn(mockResponse);
            when(mockResponse.isValid()).thenReturn(false);
            
            // when
            int savedCount = culturalEventService.saveCultureEvents();
            
            // then
            assertThat(savedCount).isEqualTo(0);
            verify(culturalEventRepository, never()).saveAll(anyList());
        }
        
        @Test
        @DisplayName("문화행사 저장 - API 오류 응답")
        void saveCultureEvents_ApiError() {
            // given
            when(seoulCulturalApiService.isApiHealthy()).thenReturn(true);
            when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
            when(nativeQuery.executeUpdate()).thenReturn(0);
            
            when(seoulCulturalApiService.fetchAllCulturalEvents(anyInt(), anyInt())).thenReturn(mockResponse);
            when(mockResponse.isValid()).thenReturn(true);
            when(mockResponse.isSuccess()).thenReturn(false);
            
            SeoulApiResponse.CulturalEventInfo eventInfo = mock(SeoulApiResponse.CulturalEventInfo.class);
            SeoulApiResponse.Result result = mock(SeoulApiResponse.Result.class);
            when(mockResponse.getCulturalEventInfo()).thenReturn(eventInfo);
            when(eventInfo.getResult()).thenReturn(result);
            when(result.getCode()).thenReturn("ERROR-500");
            when(result.getMessage()).thenReturn("서버 오류");
            
            // when & then
            assertThatThrownBy(() -> culturalEventService.saveCultureEvents())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Seoul API error: ERROR-500 - 서버 오류");
        }
        
        @Test
        @DisplayName("문화행사 저장 - 데이터 없음")
        void saveCultureEvents_NoData() {
            // given
            when(seoulCulturalApiService.isApiHealthy()).thenReturn(true);
            when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
            when(nativeQuery.executeUpdate()).thenReturn(0);
            
            when(seoulCulturalApiService.fetchAllCulturalEvents(anyInt(), anyInt())).thenReturn(mockResponse);
            when(mockResponse.isValid()).thenReturn(true);
            when(mockResponse.isSuccess()).thenReturn(true);
            when(mockResponse.hasData()).thenReturn(false);
            
            // when
            int savedCount = culturalEventService.saveCultureEvents();
            
            // then
            assertThat(savedCount).isEqualTo(0);
            verify(culturalEventRepository, never()).saveAll(anyList());
        }
        
        @Test
        @DisplayName("문화행사 저장 - Null 응답 처리")
        void saveCultureEvents_NullResponse() {
            // given
            when(seoulCulturalApiService.isApiHealthy()).thenReturn(true);
            when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
            when(nativeQuery.executeUpdate()).thenReturn(0);
            
            when(seoulCulturalApiService.fetchAllCulturalEvents(anyInt(), anyInt())).thenReturn(null);
            
            // when
            int savedCount = culturalEventService.saveCultureEvents();
            
            // then
            assertThat(savedCount).isEqualTo(0);
            verify(culturalEventRepository, never()).saveAll(anyList());
        }
        
        @Test
        @DisplayName("문화행사 저장 - 예외 발생")
        void saveCultureEvents_Exception() {
            // given
            when(seoulCulturalApiService.isApiHealthy()).thenReturn(true);
            when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
            when(nativeQuery.executeUpdate()).thenThrow(new RuntimeException("DB 오류"));
            
            // when & then
            assertThatThrownBy(() -> culturalEventService.saveCultureEvents())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to sync cultural events");
        }
        
        @Test
        @DisplayName("문화행사 저장 - 부분 실패 처리")
        void saveCultureEvents_PartialFailure() {
            // given
            when(seoulCulturalApiService.isApiHealthy()).thenReturn(true);
            when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
            when(nativeQuery.executeUpdate()).thenReturn(0);
            
            // 첫 번째 API 호출은 성공, 나머지는 null
            when(seoulCulturalApiService.fetchAllCulturalEvents(1, 1000)).thenReturn(mockResponse);
            when(seoulCulturalApiService.fetchAllCulturalEvents(1001, 2000)).thenReturn(null);
            when(seoulCulturalApiService.fetchAllCulturalEvents(2001, 3000)).thenReturn(null);
            when(seoulCulturalApiService.fetchAllCulturalEvents(3001, 4000)).thenReturn(null);
            
            when(mockResponse.isValid()).thenReturn(true);
            when(mockResponse.isSuccess()).thenReturn(true);
            when(mockResponse.hasData()).thenReturn(true);
            
            when(culturalEventMapper.mapToEntity(anyList())).thenReturn(mockCulturalEvents);
            when(culturalEventRepository.saveAll(anyList())).thenReturn(mockCulturalEvents);
            
            // when & then - 부분 실패 시 NullPointerException이 발생해야 함
            assertThatThrownBy(() -> culturalEventService.saveCultureEvents())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to sync cultural events");
            
            verify(seoulCulturalApiService, times(4)).fetchAllCulturalEvents(anyInt(), anyInt());
        }
        
        @Test
        @DisplayName("문화행사 저장 - 빈 이벤트 목록")
        void saveCultureEvents_EmptyEventList() {
            // given
            when(seoulCulturalApiService.isApiHealthy()).thenReturn(true);
            when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
            when(nativeQuery.executeUpdate()).thenReturn(0);
            
            SeoulApiResponse emptyResponse = mock(SeoulApiResponse.class);
            SeoulApiResponse.CulturalEventInfo eventInfo = mock(SeoulApiResponse.CulturalEventInfo.class);
            SeoulApiResponse.Result result = mock(SeoulApiResponse.Result.class);
            
            when(seoulCulturalApiService.fetchAllCulturalEvents(anyInt(), anyInt())).thenReturn(emptyResponse);
            when(emptyResponse.isValid()).thenReturn(true);
            when(emptyResponse.isSuccess()).thenReturn(true);
            when(emptyResponse.hasData()).thenReturn(true);
            when(emptyResponse.getCulturalEventInfo()).thenReturn(eventInfo);
            when(eventInfo.getResult()).thenReturn(result);
            when(eventInfo.getRow()).thenReturn(Collections.emptyList());
            
            when(culturalEventMapper.mapToEntity(anyList())).thenReturn(Collections.emptyList());
            
            // when
            int savedCount = culturalEventService.saveCultureEvents();
            
            // then
            assertThat(savedCount).isEqualTo(0);
            verify(culturalEventRepository, times(4)).saveAll(Collections.emptyList());
        }
    }
    
    // Helper methods
    private SeoulApiResponse.CulturalEventData createMockEventData(String id, String title, String category) {
        SeoulApiResponse.CulturalEventData eventData = mock(SeoulApiResponse.CulturalEventData.class);
        when(eventData.getTitle()).thenReturn(title);
        when(eventData.getCodeName()).thenReturn(category);
        when(eventData.getGuName()).thenReturn("서울시");
        when(eventData.getPlace()).thenReturn("서울 광장");
        when(eventData.getOrgName()).thenReturn("서울시청");
        when(eventData.getUseTarget()).thenReturn("전체");
        when(eventData.getUseFee()).thenReturn("무료");
        when(eventData.getPlayer()).thenReturn("시민");
        when(eventData.getProgram()).thenReturn("문화 프로그램");
        when(eventData.getEtcDesc()).thenReturn("기타 설명");
        when(eventData.getOrgLink()).thenReturn("http://seoul.go.kr");
        when(eventData.getMainImg()).thenReturn("http://image.url");
        when(eventData.getRegistrationDate()).thenReturn("2024-01-01");
        when(eventData.getTicket()).thenReturn("현장구매");
        when(eventData.getStartDate()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0));
        when(eventData.getEndDate()).thenReturn(LocalDateTime.of(2024, 1, 1, 18, 0));
        when(eventData.getThemeCode()).thenReturn("THEME001");
        when(eventData.getDate()).thenReturn("2024-01-01~2024-01-01");
        return eventData;
    }
    
    private CulturalEvent createCulturalEvent(String id, String title, String category) {
        return CulturalEvent.builder()
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
                .endDate(LocalDate.of(2024, 1, 1))
                .themeCode("THEME001")
                .eventDate("2024-01-01~2024-01-01")
                .latitude(BigDecimal.valueOf(37.5665))
                .longitude(BigDecimal.valueOf(126.9780))
                .build();
    }
}