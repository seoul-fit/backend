package com.seoulfit.backend.trigger.strategy.impl;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import com.seoulfit.backend.trigger.dto.TriggerContext;
import com.seoulfit.backend.trigger.dto.TriggerResult;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * AirQualityTriggerStrategy 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AirQualityTriggerStrategy 단위 테스트")
class AirQualityTriggerStrategyTest {

    @InjectMocks
    private AirQualityTriggerStrategy airQualityTriggerStrategy;
    
    @Mock
    private User mockUser;
    
    private TriggerContext context;
    private Map<String, Object> publicApiData;
    private List<InterestCategory> userInterests;
    
    @BeforeEach
    void setUp() {
        when(mockUser.getId()).thenReturn(1L);
        
        publicApiData = new HashMap<>();
        userInterests = new ArrayList<>();
        
        context = TriggerContext.builder()
                .user(mockUser)
                .publicApiData(publicApiData)
                .userInterests(userInterests)
                .build();
    }
    
    @Nested
    @DisplayName("대기질 트리거 평가 테스트")
    class EvaluateTest {
        
        @Test
        @DisplayName("트리거 발동 - PM10 나쁨")
        void evaluate_TriggerOnBadPM10() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = createWeatherData("나쁨", "보통", "보통", "80", "30");
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            publicApiData.put("locationInfo", "서울특별시 중구");
            
            // when
            TriggerResult result = airQualityTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getNotificationType()).isEqualTo(NotificationType.WEATHER);
            assertThat(result.getTriggerCondition()).isEqualTo(TriggerCondition.AIR_QUALITY_BAD);
            assertThat(result.getTitle()).isEqualTo("대기질 주의보");
            assertThat(result.getMessage()).contains("미세먼지: 나쁨(80㎍/㎥)");
            assertThat(result.getMessage()).contains("마스크 착용을 권장합니다");
            assertThat(result.getLocationInfo()).isEqualTo("서울특별시 중구");
        }
        
        @Test
        @DisplayName("트리거 발동 - PM2.5 매우나쁨")
        void evaluate_TriggerOnVeryBadPM25() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = createWeatherData("보통", "매우나쁨", "보통", "50", "100");
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = airQualityTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getMessage()).contains("초미세먼지: 매우나쁨(100㎍/㎥)");
        }
        
        @Test
        @DisplayName("트리거 발동 - 통합지수 나쁨")
        void evaluate_TriggerOnBadAirIndex() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = createWeatherData("보통", "보통", "나쁨", "50", "25");
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = airQualityTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getTriggerCondition()).isEqualTo(TriggerCondition.AIR_QUALITY_BAD);
        }
        
        @Test
        @DisplayName("트리거 발동 - 복합 나쁨 상태")
        void evaluate_TriggerOnMultipleBadConditions() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = createWeatherData("나쁨", "매우나쁨", "나쁨", "85", "90");
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = airQualityTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getMessage()).contains("미세먼지: 나쁨");
            assertThat(result.getMessage()).contains("초미세먼지: 매우나쁨");
        }
        
        @Test
        @DisplayName("트리거 미발동 - 대기질 좋음")
        void evaluate_NoTriggerOnGoodAirQuality() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = createWeatherData("좋음", "좋음", "좋음", "30", "15");
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = airQualityTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
        }
        
        @Test
        @DisplayName("트리거 미발동 - 대기질 보통")
        void evaluate_NoTriggerOnModerateAirQuality() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = createWeatherData("보통", "보통", "보통", "50", "25");
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = airQualityTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
        }
        
        @Test
        @DisplayName("트리거 미발동 - 사용자 관심사 없음")
        void evaluate_NoTriggerWhenNoWeatherInterest() {
            // given
            userInterests.add(InterestCategory.CULTURE);  // WEATHER 관심사 없음
            
            Map<String, Object> weatherData = createWeatherData("나쁨", "나쁨", "나쁨", "80", "50");
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = airQualityTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
        }
        
        @Test
        @DisplayName("트리거 미발동 - 날씨 데이터 없음")
        void evaluate_NoTriggerWhenNoWeatherData() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            // publicApiData에 WEATHER_STTS 없음
            
            // when
            TriggerResult result = airQualityTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
        }
        
        @Test
        @DisplayName("트리거 미발동 - 빈 날씨 리스트")
        void evaluate_NoTriggerWhenEmptyWeatherList() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            publicApiData.put("WEATHER_STTS", Collections.emptyList());
            
            // when
            TriggerResult result = airQualityTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
        }
        
        @Test
        @DisplayName("트리거 미발동 - 잘못된 데이터 타입")
        void evaluate_NoTriggerWhenInvalidDataType() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            publicApiData.put("WEATHER_STTS", "잘못된 데이터");  // List가 아님
            
            // when
            TriggerResult result = airQualityTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("트리거 메타데이터 테스트")
    class MetadataTest {
        
        @Test
        @DisplayName("지원 트리거 타입 확인")
        void getSupportedTriggerType() {
            // when
            String type = airQualityTriggerStrategy.getSupportedTriggerType();
            
            // then
            assertThat(type).isEqualTo("AIR_QUALITY");
        }
        
        @Test
        @DisplayName("우선순위 확인")
        void getPriority() {
            // when
            int priority = airQualityTriggerStrategy.getPriority();
            
            // then
            assertThat(priority).isEqualTo(20);
        }
        
        @Test
        @DisplayName("설명 확인")
        void getDescription() {
            // when
            String description = airQualityTriggerStrategy.getDescription();
            
            // then
            assertThat(description).contains("미세먼지");
            assertThat(description).contains("초미세먼지");
            assertThat(description).contains("대기질");
            assertThat(description).contains("나쁨");
        }
    }
    
    @Nested
    @DisplayName("메시지 생성 테스트")
    class MessageBuildingTest {
        
        @Test
        @DisplayName("PM10만 나쁨인 경우 메시지")
        void buildMessage_OnlyPM10Bad() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = createWeatherData("나쁨", "보통", "보통", "82", "20");
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = airQualityTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.getMessage()).contains("미세먼지: 나쁨(82㎍/㎥)");
            assertThat(result.getMessage()).doesNotContain("초미세먼지");
        }
        
        @Test
        @DisplayName("PM2.5만 나쁨인 경우 메시지")
        void buildMessage_OnlyPM25Bad() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = createWeatherData("보통", "매우나쁨", "보통", "40", "85");
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = airQualityTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.getMessage()).doesNotContain("미세먼지");
            assertThat(result.getMessage()).contains("초미세먼지: 매우나쁨(85㎍/㎥)");
        }
        
        @Test
        @DisplayName("모든 지표 나쁨인 경우 메시지")
        void buildMessage_AllBad() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = createWeatherData("매우나쁨", "나쁨", "나쁨", "151", "76");
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = airQualityTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.getMessage()).contains("미세먼지: 매우나쁨(151㎍/㎥)");
            assertThat(result.getMessage()).contains("초미세먼지: 나쁨(76㎍/㎥)");
            assertThat(result.getMessage()).contains("마스크 착용을 권장합니다");
        }
    }
    
    // Helper methods
    private Map<String, Object> createWeatherData(String pm10Index, String pm25Index, 
                                                  String airIndex, String pm10Value, String pm25Value) {
        Map<String, Object> weatherData = new HashMap<>();
        weatherData.put("PM10_INDEX", pm10Index);
        weatherData.put("PM25_INDEX", pm25Index);
        weatherData.put("AIR_IDX", airIndex);
        weatherData.put("PM10", pm10Value);
        weatherData.put("PM25", pm25Value);
        return weatherData;
    }
}