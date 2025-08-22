package com.seoulfit.backend.publicdata.env.application.service;

import com.seoulfit.backend.publicdata.env.application.port.in.AirQualityQueryUseCase;
import com.seoulfit.backend.publicdata.env.application.port.out.AirQualityRepository;
import com.seoulfit.backend.publicdata.env.domain.AirQuality;
import com.seoulfit.backend.publicdata.env.domain.AirQualityStatus;
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
 * AirQualityQueryService 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AirQualityQueryService 단위 테스트")
class AirQualityQueryServiceTest {

    @Mock
    private AirQualityRepository repository;
    
    @InjectMocks
    private AirQualityQueryService airQualityQueryService;
    
    private List<AirQuality> mockAirQualityList;
    private AirQuality airQuality1;
    private AirQuality airQuality2;
    private AirQuality airQuality3;
    private LocalDateTime testDateTime;
    
    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.of(2024, 1, 20, 10, 0);
        
        airQuality1 = createAirQuality("중구", "시청역", testDateTime, 35, 20, 55, "2");
        airQuality2 = createAirQuality("강남구", "강남역", testDateTime, 45, 25, 65, "2");
        airQuality3 = createAirQuality("서초구", "서초역", testDateTime, 120, 55, 155, "4");
        
        mockAirQualityList = Arrays.asList(airQuality1, airQuality2, airQuality3);
    }
    
    @Nested
    @DisplayName("최신 대기질 정보 조회 테스트")
    class GetLatestAirQualityTest {
        
        @Test
        @DisplayName("최신 대기질 정보 조회 - 성공")
        void getLatestAirQuality_Success() {
            // given
            when(repository.findLatest()).thenReturn(mockAirQualityList);
            
            // when
            List<AirQuality> result = airQualityQueryService.getLatestAirQuality();
            
            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            assertThat(result).containsExactly(airQuality1, airQuality2, airQuality3);
            verify(repository).findLatest();
        }
        
        @Test
        @DisplayName("최신 대기질 정보 조회 - 빈 결과")
        void getLatestAirQuality_EmptyResult() {
            // given
            when(repository.findLatest()).thenReturn(Collections.emptyList());
            
            // when
            List<AirQuality> result = airQualityQueryService.getLatestAirQuality();
            
            // then
            assertThat(result).isEmpty();
            verify(repository).findLatest();
        }
    }
    
    @Nested
    @DisplayName("측정소별 최신 대기질 정보 조회 테스트")
    class GetLatestAirQualityByStationTest {
        
        @Test
        @DisplayName("측정소별 최신 대기질 정보 조회 - 데이터 존재")
        void getLatestAirQualityByStation_Found() {
            // given
            String stationName = "시청역";
            when(repository.findLatestByStation(stationName)).thenReturn(Optional.of(airQuality1));
            
            // when
            Optional<AirQuality> result = airQualityQueryService.getLatestAirQualityByStation(stationName);
            
            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(airQuality1);
            verify(repository).findLatestByStation(stationName);
        }
        
        @Test
        @DisplayName("측정소별 최신 대기질 정보 조회 - 데이터 없음")
        void getLatestAirQualityByStation_NotFound() {
            // given
            String stationName = "없는측정소";
            when(repository.findLatestByStation(stationName)).thenReturn(Optional.empty());
            
            // when
            Optional<AirQuality> result = airQualityQueryService.getLatestAirQualityByStation(stationName);
            
            // then
            assertThat(result).isEmpty();
            verify(repository).findLatestByStation(stationName);
        }
    }
    
    @Nested
    @DisplayName("지역별 최신 대기질 정보 조회 테스트")
    class GetLatestAirQualityByRegionTest {
        
        @Test
        @DisplayName("지역별 최신 대기질 정보 조회 - 성공")
        void getLatestAirQualityByRegion_Success() {
            // given
            String regionName = "중구";
            List<AirQuality> regionData = Arrays.asList(airQuality1);
            when(repository.findLatestByRegion(regionName)).thenReturn(regionData);
            
            // when
            List<AirQuality> result = airQualityQueryService.getLatestAirQualityByRegion(regionName);
            
            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(airQuality1);
            verify(repository).findLatestByRegion(regionName);
        }
        
        @Test
        @DisplayName("지역별 최신 대기질 정보 조회 - 빈 결과")
        void getLatestAirQualityByRegion_EmptyResult() {
            // given
            String regionName = "없는지역";
            when(repository.findLatestByRegion(regionName)).thenReturn(Collections.emptyList());
            
            // when
            List<AirQuality> result = airQualityQueryService.getLatestAirQualityByRegion(regionName);
            
            // then
            assertThat(result).isEmpty();
            verify(repository).findLatestByRegion(regionName);
        }
    }
    
    @Nested
    @DisplayName("기간별 대기질 정보 조회 테스트")
    class GetAirQualityByPeriodTest {
        
        @Test
        @DisplayName("기간별 대기질 정보 조회 - 성공")
        void getAirQualityByPeriod_Success() {
            // given
            LocalDateTime startTime = testDateTime.minusHours(1);
            LocalDateTime endTime = testDateTime.plusHours(1);
            when(repository.findByPeriod(startTime, endTime)).thenReturn(mockAirQualityList);
            
            // when
            List<AirQuality> result = airQualityQueryService.getAirQualityByPeriod(startTime, endTime);
            
            // then
            assertThat(result).hasSize(3);
            verify(repository).findByPeriod(startTime, endTime);
        }
        
        @Test
        @DisplayName("기간별 대기질 정보 조회 - 긴 기간")
        void getAirQualityByPeriod_LongPeriod() {
            // given
            LocalDateTime startTime = testDateTime.minusDays(30);
            LocalDateTime endTime = testDateTime;
            when(repository.findByPeriod(startTime, endTime)).thenReturn(mockAirQualityList);
            
            // when
            List<AirQuality> result = airQualityQueryService.getAirQualityByPeriod(startTime, endTime);
            
            // then
            assertThat(result).hasSize(3);
            verify(repository).findByPeriod(startTime, endTime);
        }
    }
    
    @Nested
    @DisplayName("상태별 대기질 정보 조회 테스트")
    class GetAirQualityByStatusTest {
        
        @Test
        @DisplayName("상태별 대기질 정보 조회 - UNHEALTHY 이상")
        void getAirQualityByStatus_Unhealthy() {
            // given
            AirQualityStatus minStatus = AirQualityStatus.UNHEALTHY;
            List<AirQuality> unhealthyData = Arrays.asList(airQuality3);
            when(repository.findByStatus(minStatus)).thenReturn(unhealthyData);
            
            // when
            List<AirQuality> result = airQualityQueryService.getAirQualityByStatus(minStatus);
            
            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(airQuality3);
            verify(repository).findByStatus(minStatus);
        }
        
        @Test
        @DisplayName("상태별 대기질 정보 조회 - GOOD 이상 (모든 데이터)")
        void getAirQualityByStatus_Good() {
            // given
            AirQualityStatus minStatus = AirQualityStatus.GOOD;
            when(repository.findByStatus(minStatus)).thenReturn(mockAirQualityList);
            
            // when
            List<AirQuality> result = airQualityQueryService.getAirQualityByStatus(minStatus);
            
            // then
            assertThat(result).hasSize(3);
            verify(repository).findByStatus(minStatus);
        }
    }
    
    @Nested
    @DisplayName("쿼리 기반 대기질 정보 조회 테스트")
    class GetAirQualityByQueryTest {
        
        @Test
        @DisplayName("쿼리 기반 대기질 정보 조회 - 성공")
        void getAirQuality_WithQuery() {
            // given
            AirQualityQueryUseCase.AirQualityQuery query = new AirQualityQueryUseCase.AirQualityQuery(
                "시청역", "중구", testDateTime.minusHours(1), testDateTime.plusHours(1),
                AirQualityStatus.MODERATE, 50, 30
            );
            when(repository.findByQuery(query)).thenReturn(Arrays.asList(airQuality1));
            
            // when
            List<AirQuality> result = airQualityQueryService.getAirQuality(query);
            
            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(airQuality1);
            verify(repository).findByQuery(query);
        }
    }
    
    @Nested
    @DisplayName("대기질 통계 정보 조회 테스트")
    class GetAirQualityStatisticsTest {
        
        @Test
        @DisplayName("대기질 통계 정보 조회 - 성공")
        void getAirQualityStatistics_Success() {
            // given
            LocalDateTime startTime = testDateTime.minusHours(24);
            LocalDateTime endTime = testDateTime;
            AirQualityQueryUseCase.AirQualityStatistics mockStatistics = 
                new AirQualityQueryUseCase.AirQualityStatistics(
                    50.0, 30.0, 25.0, 15L, 100L, 10L, 80L, LocalDateTime.now()
                );
            when(repository.getStatistics(startTime, endTime)).thenReturn(mockStatistics);
            
            // when
            AirQualityQueryUseCase.AirQualityStatistics result = 
                airQualityQueryService.getAirQualityStatistics(startTime, endTime);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.avgPm10()).isEqualTo(50.0);
            assertThat(result.avgPm25()).isEqualTo(30.0);
            verify(repository).getStatistics(startTime, endTime);
        }
    }
    
    @Nested
    @DisplayName("알림 필요 대기질 정보 조회 테스트")
    class GetAirQualityRequiringNotificationTest {
        
        @Test
        @DisplayName("알림 필요 대기질 정보 조회 - 성공")
        void getAirQualityRequiringNotification_Success() {
            // given
            List<AirQuality> unhealthyData = Arrays.asList(airQuality3);
            when(repository.findByStatus(AirQualityStatus.UNHEALTHY_FOR_SENSITIVE))
                    .thenReturn(unhealthyData);
            
            // when
            List<AirQuality> result = airQualityQueryService.getAirQualityRequiringNotification();
            
            // then
            assertThat(result).hasSize(1);
            verify(repository).findByStatus(AirQualityStatus.UNHEALTHY_FOR_SENSITIVE);
        }
    }
    
    @Nested
    @DisplayName("대기질 변화 추세 조회 테스트")
    class GetAirQualityTrendTest {
        
        @Test
        @DisplayName("대기질 변화 추세 조회 - 24시간")
        void getAirQualityTrend_24Hours() {
            // given
            String stationName = "시청역";
            int hours = 24;
            when(repository.findByQuery(any(AirQualityQueryUseCase.AirQualityQuery.class)))
                    .thenReturn(mockAirQualityList);
            
            // when
            List<AirQuality> result = airQualityQueryService.getAirQualityTrend(stationName, hours);
            
            // then
            assertThat(result).hasSize(3);
            verify(repository).findByQuery(any(AirQualityQueryUseCase.AirQualityQuery.class));
        }
        
        @Test
        @DisplayName("대기질 변화 추세 조회 - 1시간")
        void getAirQualityTrend_1Hour() {
            // given
            String stationName = "강남역";
            int hours = 1;
            when(repository.findByQuery(any(AirQualityQueryUseCase.AirQualityQuery.class)))
                    .thenReturn(Arrays.asList(airQuality2));
            
            // when
            List<AirQuality> result = airQualityQueryService.getAirQualityTrend(stationName, hours);
            
            // then
            assertThat(result).hasSize(1);
            verify(repository).findByQuery(any(AirQualityQueryUseCase.AirQualityQuery.class));
        }
    }
    
    @Nested
    @DisplayName("서울시 대기질 현황 요약 테스트")
    class GetSeoulAirQualitySummaryTest {
        
        @Test
        @DisplayName("서울시 대기질 현황 요약 - 다양한 상태")
        void getSeoulAirQualitySummary_VariousStatuses() {
            // given
            List<AirQuality> mixedStatusData = Arrays.asList(
                createAirQuality("중구", "시청역", testDateTime, 30, 15, 45, "1"), // GOOD
                createAirQuality("강남구", "강남역", testDateTime, 55, 30, 70, "2"), // MODERATE
                createAirQuality("서초구", "서초역", testDateTime, 85, 45, 120, "3"), // UNHEALTHY_FOR_SENSITIVE
                createAirQuality("송파구", "잠실역", testDateTime, 155, 80, 180, "4"), // UNHEALTHY
                createAirQuality("강동구", "천호역", testDateTime, 255, 150, 280, "5"), // VERY_UNHEALTHY
                createAirQuality("노원구", "노원역", testDateTime, 355, 250, 380, "6") // HAZARDOUS
            );
            when(repository.findLatest()).thenReturn(mixedStatusData);
            
            // when
            AirQualityQueryService.AirQualitySummary result = 
                airQualityQueryService.getSeoulAirQualitySummary();
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.goodCount()).isEqualTo(1);
            assertThat(result.moderateCount()).isEqualTo(1);
            assertThat(result.unhealthyForSensitiveCount()).isEqualTo(1);
            assertThat(result.unhealthyCount()).isEqualTo(1);
            assertThat(result.veryUnhealthyCount()).isEqualTo(1);
            assertThat(result.hazardousCount()).isEqualTo(1);
            assertThat(result.totalStations()).isEqualTo(6);
            verify(repository).findLatest();
        }
        
        @Test
        @DisplayName("서울시 대기질 현황 요약 - 데이터 없음")
        void getSeoulAirQualitySummary_NoData() {
            // given
            when(repository.findLatest()).thenReturn(Collections.emptyList());
            
            // when
            AirQualityQueryService.AirQualitySummary result = 
                airQualityQueryService.getSeoulAirQualitySummary();
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.goodCount()).isEqualTo(0);
            assertThat(result.moderateCount()).isEqualTo(0);
            assertThat(result.unhealthyForSensitiveCount()).isEqualTo(0);
            assertThat(result.unhealthyCount()).isEqualTo(0);
            assertThat(result.veryUnhealthyCount()).isEqualTo(0);
            assertThat(result.hazardousCount()).isEqualTo(0);
            assertThat(result.totalStations()).isEqualTo(0);
            verify(repository).findLatest();
        }
        
        @Test
        @DisplayName("서울시 대기질 현황 요약 - 모두 좋음 상태")
        void getSeoulAirQualitySummary_AllGood() {
            // given
            List<AirQuality> goodData = Arrays.asList(
                createAirQuality("중구", "시청역", testDateTime, 30, 15, 45, "1"),
                createAirQuality("강남구", "강남역", testDateTime, 25, 12, 40, "1"),
                createAirQuality("서초구", "서초역", testDateTime, 20, 10, 35, "1")
            );
            when(repository.findLatest()).thenReturn(goodData);
            
            // when
            AirQualityQueryService.AirQualitySummary result = 
                airQualityQueryService.getSeoulAirQualitySummary();
            
            // then
            assertThat(result.goodCount()).isEqualTo(3);
            assertThat(result.moderateCount()).isEqualTo(0);
            assertThat(result.totalStations()).isEqualTo(3);
            verify(repository).findLatest();
        }
    }
    
    // Helper methods
    private AirQuality createAirQuality(String region, String station, LocalDateTime measureTime, 
                                       int pm10, int pm25, int khai, String khaiGrade) {
        return AirQuality.builder()
                .msrDt(measureTime)
                .msrRgnNm(region)
                .msrSteNm(station)
                .pm10Value(pm10)
                .pm25Value(pm25)
                .o3Value(0.030)
                .no2Value(0.020)
                .coValue(0.5)
                .so2Value(0.002)
                .khaiValue(khai)
                .khaiGrade(khaiGrade)
                .pm1024hAvg(pm10 + 5)
                .pm2524hAvg(pm25 + 5)
                .build();
    }
}