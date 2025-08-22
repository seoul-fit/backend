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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * HeavyRainTriggerStrategy 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HeavyRainTriggerStrategy 단위 테스트")
class HeavyRainTriggerStrategyTest {

    @InjectMocks
    private HeavyRainTriggerStrategy heavyRainTriggerStrategy;
    
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
        
        // 기본 임계값 설정
        ReflectionTestUtils.setField(heavyRainTriggerStrategy, "heavyRainThreshold", 15.0);
        ReflectionTestUtils.setField(heavyRainTriggerStrategy, "warningRainThreshold", 30.0);
    }
    
    @Nested
    @DisplayName("폭우 트리거 평가 테스트")
    class EvaluateTest {
        
        @Test
        @DisplayName("트리거 발동 - 호우경보 수준")
        void evaluate_TriggerOnWarningLevel() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = createWeatherData(35.0, 150.0);
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            publicApiData.put("locationInfo", "서울특별시 중구");
            
            // when
            TriggerResult result = heavyRainTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getNotificationType()).isEqualTo(NotificationType.WEATHER);
            assertThat(result.getTriggerCondition()).isEqualTo(TriggerCondition.HEAVY_RAIN);
            assertThat(result.getTitle()).isEqualTo("호우 경보");
            assertThat(result.getMessage()).contains("35.0mm");
            assertThat(result.getMessage()).contains("대피하세요");
            assertThat(result.getMessage()).contains("지하차도");
            assertThat(result.getPriority()).isEqualTo(5);
        }
        
        @Test
        @DisplayName("트리거 발동 - 호우주의보 수준")
        void evaluate_TriggerOnHeavyRainLevel() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = createWeatherData(20.0, 80.0);
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            publicApiData.put("locationInfo", "서울특별시 강남구");
            
            // when
            TriggerResult result = heavyRainTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getTitle()).isEqualTo("호우 주의보");
            assertThat(result.getMessage()).contains("20.0mm");
            assertThat(result.getMessage()).contains("우산을 준비");
            assertThat(result.getMessage()).contains("물이 고이기 쉬운 곳");
            assertThat(result.getLocationInfo()).isEqualTo("서울특별시 강남구");
        }
        
        @Test
        @DisplayName("트리거 미발동 - 강수량 미달")
        void evaluate_NoTriggerBelowThreshold() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = createWeatherData(10.0, 40.0);
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = heavyRainTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
        }
        
        @Test
        @DisplayName("트리거 미발동 - 강수량 0")
        void evaluate_NoTriggerNoRain() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = createWeatherData(0.0, 0.0);
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = heavyRainTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
        }
        
        @Test
        @DisplayName("트리거 미발동 - 사용자 관심사 없음")
        void evaluate_NoTriggerWhenNoWeatherInterest() {
            // given
            userInterests.add(InterestCategory.CULTURE);  // WEATHER 관심사 없음
            
            Map<String, Object> weatherData = createWeatherData(50.0, 200.0);
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = heavyRainTriggerStrategy.evaluate(context);
            
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
            TriggerResult result = heavyRainTriggerStrategy.evaluate(context);
            
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
            TriggerResult result = heavyRainTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
        }
        
        @Test
        @DisplayName("대체 강수량 데이터 사용 - rainInfo")
        void evaluate_UseAlternativeRainData() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            // WEATHER_STTS 없고 rainInfo 사용
            Map<String, Object> rainData = new HashMap<>();
            rainData.put("RAINFALL1H", 25.0);
            rainData.put("RAINFALL24H", 100.0);
            List<Map<String, Object>> rainList = Arrays.asList(rainData);
            publicApiData.put("rainInfo", rainList);
            
            // when
            TriggerResult result = heavyRainTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getTitle()).isEqualTo("호우 주의보");
            assertThat(result.getMessage()).contains("25.0mm");
        }
        
        @Test
        @DisplayName("대체 키 사용 - RF1H, RF24H")
        void evaluate_UseAlternativeKeys() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> rainData = new HashMap<>();
            rainData.put("RF1H", 18.0);  // 대체 키
            rainData.put("RF24H", 75.0);
            List<Map<String, Object>> rainList = Arrays.asList(rainData);
            publicApiData.put("rainInfo", rainList);
            
            // when
            TriggerResult result = heavyRainTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getMessage()).contains("18.0mm");
        }
        
        @Test
        @DisplayName("경계값 테스트 - 정확히 임계값")
        void evaluate_ExactThreshold() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            // 정확히 15.0mm (주의보 임계값)
            Map<String, Object> weatherData = createWeatherData(15.0, 60.0);
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = heavyRainTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getTitle()).isEqualTo("호우 주의보");
        }
        
        @Test
        @DisplayName("경계값 테스트 - 임계값 바로 아래")
        void evaluate_JustBelowThreshold() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            // 14.9mm (주의보 임계값 바로 아래)
            Map<String, Object> weatherData = createWeatherData(14.9, 60.0);
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = heavyRainTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("우선순위 테스트")
    class PriorityTest {
        
        @Test
        @DisplayName("경보 수준 - 최고 우선순위")
        void warningLevelHighestPriority() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = createWeatherData(40.0, 200.0);
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = heavyRainTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.getPriority()).isEqualTo(5);  // 최고 우선순위
        }
        
        @Test
        @DisplayName("주의보 수준 - 기본 우선순위")
        void heavyRainLevelNormalPriority() {
            // given
            userInterests.add(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = createWeatherData(20.0, 80.0);
            List<Map<String, Object>> weatherList = Arrays.asList(weatherData);
            publicApiData.put("WEATHER_STTS", weatherList);
            
            // when
            TriggerResult result = heavyRainTriggerStrategy.evaluate(context);
            
            // then
            // 주의보 수준은 기본 우선순위 (TriggerResult.triggered 메서드의 기본값)
            assertThat(result.getPriority()).isGreaterThan(0);
            assertThat(result.getPriority()).isLessThan(100);
        }
    }
    
    @Nested
    @DisplayName("트리거 메타데이터 테스트")
    class MetadataTest {
        
        @Test
        @DisplayName("지원 트리거 타입 확인")
        void getSupportedTriggerType() {
            // when
            String type = heavyRainTriggerStrategy.getSupportedTriggerType();
            
            // then
            assertThat(type).isEqualTo("HEAVY_RAIN");
        }
        
        @Test
        @DisplayName("우선순위 확인")
        void getPriority() {
            // when
            int priority = heavyRainTriggerStrategy.getPriority();
            
            // then
            assertThat(priority).isEqualTo(5);  // 매우 높은 우선순위
        }
        
        @Test
        @DisplayName("설명 확인")
        void getDescription() {
            // when
            String description = heavyRainTriggerStrategy.getDescription();
            
            // then
            assertThat(description).contains("강수량");
            assertThat(description).contains("임계값");
            assertThat(description).contains("알림");
        }
    }
    
    // Helper methods
    private Map<String, Object> createWeatherData(double hourlyRain, double dailyRain) {
        Map<String, Object> weatherData = new HashMap<>();
        weatherData.put("RAIN_HOUR", hourlyRain);
        weatherData.put("RAIN_DAY", dailyRain);
        return weatherData;
    }
    
    private Map<String, Object> createWeatherDataWithAlternativeKeys(double hourlyRain, double dailyRain) {
        Map<String, Object> weatherData = new HashMap<>();
        weatherData.put("RAINFALL_1H", hourlyRain);
        weatherData.put("RAINFALL_24H", dailyRain);
        return weatherData;
    }
}