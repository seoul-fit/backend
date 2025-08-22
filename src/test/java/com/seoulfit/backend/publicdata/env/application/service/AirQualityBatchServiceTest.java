package com.seoulfit.backend.publicdata.env.application.service;

import com.seoulfit.backend.publicdata.env.adapter.out.api.dto.AirQualityApiResponse;
import com.seoulfit.backend.publicdata.env.application.port.in.AirQualityBatchUseCase;
import com.seoulfit.backend.publicdata.env.application.port.out.AirQualityApiClient;
import com.seoulfit.backend.publicdata.env.application.port.out.AirQualityRepository;
import com.seoulfit.backend.publicdata.env.domain.AirQuality;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AirQualityBatchService 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AirQualityBatchService 단위 테스트")
class AirQualityBatchServiceTest {

    @Mock
    private AirQualityApiClient apiClient;
    
    @Mock
    private AirQualityRepository repository;
    
    @InjectMocks
    private AirQualityBatchService airQualityBatchService;
    
    private AirQualityApiResponse mockApiResponse;
    private List<AirQualityApiResponse.AirQualityRow> mockAirQualityRows;
    private AirQuality mockAirQuality;
    
    @BeforeEach
    void setUp() {
        // Mock API Response 설정
        mockApiResponse = mock(AirQualityApiResponse.class);
        AirQualityApiResponse.RealtimeCityAir realtimeCityAir = mock(AirQualityApiResponse.RealtimeCityAir.class);
        
        // Mock Air Quality Rows 생성
        mockAirQualityRows = Arrays.asList(
            createMockAirQualityRow("중구", "시청역", "2024-01-20 10:00", "35", "20", "0.030", "0.020", "0.5", "0.002", "55", "2", "40", "25"),
            createMockAirQualityRow("강남구", "강남역", "2024-01-20 10:00", "45", "25", "0.035", "0.025", "0.6", "0.003", "65", "2", "50", "30"),
            createMockAirQualityRow("서초구", "서초역", "2024-01-20 10:00", "40", "22", "0.032", "0.022", "0.55", "0.0025", "60", "2", "45", "27")
        );
        
        // Mock Air Quality Entity
        mockAirQuality = createMockAirQuality("중구", "시청역", LocalDateTime.of(2024, 1, 20, 10, 0));
        
        // Response 설정
        when(mockApiResponse.getRealtimeCityAir()).thenReturn(realtimeCityAir);
        when(realtimeCityAir.getRow()).thenReturn(mockAirQualityRows);
        when(realtimeCityAir.getListTotalCount()).thenReturn(3);
    }
    
    @Nested
    @DisplayName("실시간 대기질 배치 처리 테스트")
    class ProcessRealTimeBatchTest {
        
        @Test
        @DisplayName("실시간 대기질 배치 처리 - 모든 데이터 신규 저장")
        void processRealTimeBatch_AllNewData() {
            // given
            when(apiClient.fetchRealTimeAirQuality()).thenReturn(mockApiResponse);
            when(repository.findByStationAndDateTime(anyString(), any(LocalDateTime.class)))
                    .thenReturn(Optional.empty());
            when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
            
            // when
            AirQualityBatchUseCase.AirQualityBatchResult result = airQualityBatchService.processRealTimeBatch();
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.success()).isTrue();
            assertThat(result.totalFetched()).isEqualTo(3);
            assertThat(result.totalSaved()).isEqualTo(3);
            assertThat(result.totalUpdated()).isEqualTo(0);
            assertThat(result.totalSkipped()).isEqualTo(0);
            
            verify(apiClient).fetchRealTimeAirQuality();
            verify(repository, times(3)).findByStationAndDateTime(anyString(), any(LocalDateTime.class));
            verify(repository).saveAll(anyList());
        }
        
        @Test
        @DisplayName("실시간 대기질 배치 처리 - 기존 데이터 업데이트")
        void processRealTimeBatch_UpdateExistingData() {
            // given
            when(apiClient.fetchRealTimeAirQuality()).thenReturn(mockApiResponse);
            when(repository.findByStationAndDateTime(anyString(), any(LocalDateTime.class)))
                    .thenReturn(Optional.of(mockAirQuality));
            when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
            
            // when
            AirQualityBatchUseCase.AirQualityBatchResult result = airQualityBatchService.processRealTimeBatch();
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.success()).isTrue();
            assertThat(result.totalFetched()).isEqualTo(3);
            assertThat(result.totalSaved()).isEqualTo(0);
            assertThat(result.totalUpdated()).isEqualTo(3);
            assertThat(result.totalSkipped()).isEqualTo(0);
            
            verify(repository).saveAll(anyList());
        }
        
        @Test
        @DisplayName("실시간 대기질 배치 처리 - 혼합 데이터 (신규 + 업데이트)")
        void processRealTimeBatch_MixedData() {
            // given
            when(apiClient.fetchRealTimeAirQuality()).thenReturn(mockApiResponse);
            
            // 첫 번째는 기존 데이터, 나머지는 신규
            when(repository.findByStationAndDateTime(eq("시청역"), any(LocalDateTime.class)))
                    .thenReturn(Optional.of(mockAirQuality));
            when(repository.findByStationAndDateTime(eq("강남역"), any(LocalDateTime.class)))
                    .thenReturn(Optional.empty());
            when(repository.findByStationAndDateTime(eq("서초역"), any(LocalDateTime.class)))
                    .thenReturn(Optional.empty());
            
            when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
            
            // when
            AirQualityBatchUseCase.AirQualityBatchResult result = airQualityBatchService.processRealTimeBatch();
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.success()).isTrue();
            assertThat(result.totalFetched()).isEqualTo(3);
            assertThat(result.totalSaved()).isEqualTo(2);
            assertThat(result.totalUpdated()).isEqualTo(1);
            
            verify(repository).saveAll(anyList());
        }
        
        @Test
        @DisplayName("실시간 대기질 배치 처리 - API 호출 실패")
        void processRealTimeBatch_ApiFailure() {
            // given
            when(apiClient.fetchRealTimeAirQuality())
                    .thenThrow(new RuntimeException("API 호출 실패"));
            
            // when
            AirQualityBatchUseCase.AirQualityBatchResult result = airQualityBatchService.processRealTimeBatch();
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.success()).isFalse();
            assertThat(result.errorMessage()).contains("API 호출 실패");
            
            verify(repository, never()).saveAll(anyList());
        }
        
        @Test
        @DisplayName("실시간 대기질 배치 처리 - 빈 응답 처리")
        void processRealTimeBatch_EmptyResponse() {
            // given
            AirQualityApiResponse emptyResponse = mock(AirQualityApiResponse.class);
            AirQualityApiResponse.RealtimeCityAir emptyRealtimeCityAir = mock(AirQualityApiResponse.RealtimeCityAir.class);
            
            when(apiClient.fetchRealTimeAirQuality()).thenReturn(emptyResponse);
            when(emptyResponse.getRealtimeCityAir()).thenReturn(emptyRealtimeCityAir);
            when(emptyRealtimeCityAir.getRow()).thenReturn(Collections.emptyList());
            when(emptyRealtimeCityAir.getListTotalCount()).thenReturn(0);
            
            // when
            AirQualityBatchUseCase.AirQualityBatchResult result = airQualityBatchService.processRealTimeBatch();
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.success()).isTrue();
            assertThat(result.totalFetched()).isEqualTo(0);
            assertThat(result.totalSaved()).isEqualTo(0);
            assertThat(result.totalUpdated()).isEqualTo(0);
            
            verify(repository, never()).saveAll(anyList());
        }
        
        @Test
        @DisplayName("실시간 대기질 배치 처리 - 잘못된 데이터 포맷")
        void processRealTimeBatch_InvalidDataFormat() {
            // given
            List<AirQualityApiResponse.AirQualityRow> invalidRows = Arrays.asList(
                createMockAirQualityRow("중구", "시청역", "invalid-date", "35", "20", "0.030", "0.020", "0.5", "0.002", "55", "2", "40", "25")
            );
            
            AirQualityApiResponse invalidResponse = mock(AirQualityApiResponse.class);
            AirQualityApiResponse.RealtimeCityAir invalidRealtimeCityAir = mock(AirQualityApiResponse.RealtimeCityAir.class);
            
            when(apiClient.fetchRealTimeAirQuality()).thenReturn(invalidResponse);
            when(invalidResponse.getRealtimeCityAir()).thenReturn(invalidRealtimeCityAir);
            when(invalidRealtimeCityAir.getRow()).thenReturn(invalidRows);
            when(invalidRealtimeCityAir.getListTotalCount()).thenReturn(1);
            
            // when
            AirQualityBatchUseCase.AirQualityBatchResult result = airQualityBatchService.processRealTimeBatch();
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.success()).isFalse();
            assertThat(result.errorMessage()).contains("실시간 대기질 정보 배치 처리 중 예외 발생");
            
            verify(repository, never()).saveAll(anyList());
        }
        
        @Test
        @DisplayName("실시간 대기질 배치 처리 - 저장 실패")
        void processRealTimeBatch_SaveFailure() {
            // given
            when(apiClient.fetchRealTimeAirQuality()).thenReturn(mockApiResponse);
            when(repository.findByStationAndDateTime(anyString(), any(LocalDateTime.class)))
                    .thenReturn(Optional.empty());
            when(repository.saveAll(anyList()))
                    .thenThrow(new RuntimeException("DB 저장 실패"));
            
            // when
            AirQualityBatchUseCase.AirQualityBatchResult result = airQualityBatchService.processRealTimeBatch();
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.success()).isFalse();
            assertThat(result.errorMessage()).contains("DB 저장 실패");
        }
        
        @Test
        @DisplayName("실시간 대기질 배치 처리 - 부분 데이터 처리 실패")
        void processRealTimeBatch_PartialProcessingFailure() {
            // given
            List<AirQualityApiResponse.AirQualityRow> mixedRows = Arrays.asList(
                createMockAirQualityRow("중구", "시청역", "2024-01-20 10:00", "35", "20", "0.030", "0.020", "0.5", "0.002", "55", "2", "40", "25"),
                createMockAirQualityRow("강남구", "강남역", "2024-01-20 10:00", "invalid", "25", "0.035", "0.025", "0.6", "0.003", "65", "2", "50", "30")
            );
            
            AirQualityApiResponse mixedResponse = mock(AirQualityApiResponse.class);
            AirQualityApiResponse.RealtimeCityAir mixedRealtimeCityAir = mock(AirQualityApiResponse.RealtimeCityAir.class);
            
            when(apiClient.fetchRealTimeAirQuality()).thenReturn(mixedResponse);
            when(mixedResponse.getRealtimeCityAir()).thenReturn(mixedRealtimeCityAir);
            when(mixedRealtimeCityAir.getRow()).thenReturn(mixedRows);
            when(mixedRealtimeCityAir.getListTotalCount()).thenReturn(2);
            
            when(repository.findByStationAndDateTime(eq("시청역"), any(LocalDateTime.class)))
                    .thenReturn(Optional.empty());
            
            // when
            AirQualityBatchUseCase.AirQualityBatchResult result = airQualityBatchService.processRealTimeBatch();
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.success()).isFalse();
            assertThat(result.errorMessage()).contains("실시간 대기질 정보 배치 처리 중 예외 발생");
        }
    }
    
    @Nested
    @DisplayName("일일 대기질 배치 처리 테스트")
    class ProcessDailyBatchTest {
        
        @Test
        @DisplayName("일일 대기질 배치 처리 - 구현되지 않은 기능")
        void processDailyBatch_NotImplemented() {
            // given
            String dataDate = "2024-01-20";
            
            // when
            AirQualityBatchUseCase.AirQualityBatchResult result = airQualityBatchService.processDailyBatch(dataDate);
            
            // then
            assertThat(result).isNull();
            verifyNoInteractions(apiClient, repository);
        }
    }
    
    @Nested
    @DisplayName("시간대별 대기질 배치 처리 테스트")
    class ProcessTimeBatchTest {
        
        @Test
        @DisplayName("시간대별 대기질 배치 처리 - 구현되지 않은 기능")
        void processTimeBatch_NotImplemented() {
            // given
            LocalDateTime startTime = LocalDateTime.of(2024, 1, 20, 9, 0);
            LocalDateTime endTime = LocalDateTime.of(2024, 1, 20, 11, 0);
            
            // when
            AirQualityBatchUseCase.AirQualityBatchResult result = airQualityBatchService.processTimeBatch(startTime, endTime);
            
            // then
            assertThat(result).isNull();
            verifyNoInteractions(apiClient, repository);
        }
    }
    
    // Helper methods
    private AirQualityApiResponse.AirQualityRow createMockAirQualityRow(
            String region, String station, String measureTime,
            String pm10, String pm25, String o3, String no2, String co, String so2,
            String khai, String khaiGrade, String pm10_24h, String pm25_24h) {
        
        AirQualityApiResponse.AirQualityRow row = mock(AirQualityApiResponse.AirQualityRow.class);
        when(row.getMSRRGNNM()).thenReturn(region);
        when(row.getMSRSTENNM()).thenReturn(station);
        when(row.getMSRDT()).thenReturn(measureTime);
        when(row.getPM10()).thenReturn(pm10);
        when(row.getPM25()).thenReturn(pm25);
        when(row.getO3()).thenReturn(o3);
        when(row.getNO2()).thenReturn(no2);
        when(row.getCO()).thenReturn(co);
        when(row.getSO2()).thenReturn(so2);
        when(row.getKHAI()).thenReturn(khai);
        when(row.getKHAIGRADE()).thenReturn(khaiGrade);
        when(row.getPM10_24H()).thenReturn(pm10_24h);
        when(row.getPM25_24H()).thenReturn(pm25_24h);
        return row;
    }
    
    private AirQuality createMockAirQuality(String region, String station, LocalDateTime measureTime) {
        return AirQuality.builder()
                .msrDt(measureTime)
                .msrRgnNm(region)
                .msrSteNm(station)
                .pm10Value(35)
                .pm25Value(20)
                .o3Value(0.030)
                .no2Value(0.020)
                .coValue(0.5)
                .so2Value(0.002)
                .khaiValue(55)
                .khaiGrade("2")
                .pm1024hAvg(40)
                .pm2524hAvg(25)
                .build();
    }
}