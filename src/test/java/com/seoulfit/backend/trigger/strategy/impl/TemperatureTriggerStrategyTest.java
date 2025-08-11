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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * TemperatureTriggerStrategy 테스트 클래스입니다.
 * 
 * @author Seoul Fit
 * @since 2025-01-01
 */
@DisplayName("TemperatureTriggerStrategy 테스트")
class TemperatureTriggerStrategyTest {

    private TemperatureTriggerStrategy strategy;
    private User testUser;

    @BeforeEach
    void setUp() {
        strategy = new TemperatureTriggerStrategy();
        
        // 임계값 설정 (리플렉션 사용)
        ReflectionTestUtils.setField(strategy, "highTemperatureThreshold", 35.0);
        ReflectionTestUtils.setField(strategy, "lowTemperatureThreshold", 0.0);
        
        testUser = User.createLocalUser("test@example.com", "password", "테스트사용자");
    }

    @Nested
    @DisplayName("고온 트리거 테스트")
    class HighTemperatureTriggerTest {

        @Test
        @DisplayName("고온 임계값 초과 시 폭염 주의보 발송")
        void evaluate_HighTemperature_TriggersHeatWaveAlert() {
            // given
            testUser.addInterest(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = Map.of(
                "WEATHER_STTS", List.of(
                    Map.of("TEMP", "36.5")
                )
            );
            
            TriggerContext context = TriggerContext.builder()
                    .user(testUser)
                    .userInterests(List.of(InterestCategory.WEATHER))
                    .publicApiData(weatherData)
                    .build();

            // when
            TriggerResult result = strategy.evaluate(context);

            // then
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getNotificationType()).isEqualTo(NotificationType.WEATHER);
            assertThat(result.getTriggerCondition()).isEqualTo(TriggerCondition.TEMPERATURE_HIGH);
            assertThat(result.getTitle()).isEqualTo("폭염 주의보");
            assertThat(result.getMessage()).contains("36.5°C");
            assertThat(result.getMessage()).contains("수분 섭취");
            assertThat(result.getPriority()).isEqualTo(10);
        }

        @Test
        @DisplayName("고온 임계값과 동일한 온도에서 트리거 발동")
        void evaluate_ExactHighThreshold_Triggers() {
            // given
            testUser.addInterest(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = Map.of(
                "WEATHER_STTS", List.of(
                    Map.of("TEMP", "35.0")
                )
            );
            
            TriggerContext context = TriggerContext.builder()
                    .user(testUser)
                    .userInterests(List.of(InterestCategory.WEATHER))
                    .publicApiData(weatherData)
                    .build();

            // when
            TriggerResult result = strategy.evaluate(context);

            // then
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getTriggerCondition()).isEqualTo(TriggerCondition.TEMPERATURE_HIGH);
        }

        @Test
        @DisplayName("고온 임계값 미만에서 트리거 발동하지 않음")
        void evaluate_BelowHighThreshold_DoesNotTrigger() {
            // given
            testUser.addInterest(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = Map.of(
                "WEATHER_STTS", List.of(
                    Map.of("TEMP", "34.9")
                )
            );
            
            TriggerContext context = TriggerContext.builder()
                    .user(testUser)
                    .userInterests(List.of(InterestCategory.WEATHER))
                    .publicApiData(weatherData)
                    .build();

            // when
            TriggerResult result = strategy.evaluate(context);

            // then
            assertThat(result.isTriggered()).isFalse();
        }
    }

    @Nested
    @DisplayName("저온 트리거 테스트")
    class LowTemperatureTriggerTest {

        @Test
        @DisplayName("저온 임계값 미만 시 한파 주의보 발송")
        void evaluate_LowTemperature_TriggersColdWaveAlert() {
            // given
            testUser.addInterest(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = Map.of(
                "WEATHER_STTS", List.of(
                    Map.of("TEMP", "-5.0")
                )
            );
            
            TriggerContext context = TriggerContext.builder()
                    .user(testUser)
                    .userInterests(List.of(InterestCategory.WEATHER))
                    .publicApiData(weatherData)
                    .build();

            // when
            TriggerResult result = strategy.evaluate(context);

            // then
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getNotificationType()).isEqualTo(NotificationType.WEATHER);
            assertThat(result.getTriggerCondition()).isEqualTo(TriggerCondition.TEMPERATURE_LOW);
            assertThat(result.getTitle()).isEqualTo("한파 주의보");
            assertThat(result.getMessage()).contains("-5.0°C");
            assertThat(result.getMessage()).contains("따뜻한 옷차림");
            assertThat(result.getPriority()).isEqualTo(10);
        }

        @Test
        @DisplayName("저온 임계값과 동일한 온도에서 트리거 발동")
        void evaluate_ExactLowThreshold_Triggers() {
            // given
            testUser.addInterest(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = Map.of(
                "WEATHER_STTS", List.of(
                    Map.of("TEMP", "0.0")
                )
            );
            
            TriggerContext context = TriggerContext.builder()
                    .user(testUser)
                    .userInterests(List.of(InterestCategory.WEATHER))
                    .publicApiData(weatherData)
                    .build();

            // when
            TriggerResult result = strategy.evaluate(context);

            // then
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getTriggerCondition()).isEqualTo(TriggerCondition.TEMPERATURE_LOW);
        }

        @Test
        @DisplayName("저온 임계값 초과에서 트리거 발동하지 않음")
        void evaluate_AboveLowThreshold_DoesNotTrigger() {
            // given
            testUser.addInterest(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = Map.of(
                "WEATHER_STTS", List.of(
                    Map.of("TEMP", "0.1")
                )
            );
            
            TriggerContext context = TriggerContext.builder()
                    .user(testUser)
                    .userInterests(List.of(InterestCategory.WEATHER))
                    .publicApiData(weatherData)
                    .build();

            // when
            TriggerResult result = strategy.evaluate(context);

            // then
            assertThat(result.isTriggered()).isFalse();
        }
    }

    @Nested
    @DisplayName("사용자 관심사 필터링 테스트")
    class UserInterestFilterTest {

        @Test
        @DisplayName("날씨에 관심이 없는 사용자는 트리거 발동하지 않음")
        void evaluate_UserNotInterestedInWeather_DoesNotTrigger() {
            // given
            testUser.addInterest(InterestCategory.CULTURE); // 날씨가 아닌 다른 관심사
            
            Map<String, Object> weatherData = Map.of(
                "WEATHER_STTS", List.of(
                    Map.of("TEMP", "40.0") // 매우 높은 온도
                )
            );
            
            TriggerContext context = TriggerContext.builder()
                    .user(testUser)
                    .userInterests(List.of(InterestCategory.CULTURE))
                    .publicApiData(weatherData)
                    .build();

            // when
            TriggerResult result = strategy.evaluate(context);

            // then
            assertThat(result.isTriggered()).isFalse();
        }

        @Test
        @DisplayName("날씨에 관심이 있는 사용자만 트리거 발동")
        void evaluate_UserInterestedInWeather_Triggers() {
            // given
            testUser.addInterest(InterestCategory.WEATHER);
            testUser.addInterest(InterestCategory.CULTURE);
            
            Map<String, Object> weatherData = Map.of(
                "WEATHER_STTS", List.of(
                    Map.of("TEMP", "40.0")
                )
            );
            
            TriggerContext context = TriggerContext.builder()
                    .user(testUser)
                    .userInterests(List.of(InterestCategory.WEATHER, InterestCategory.CULTURE))
                    .publicApiData(weatherData)
                    .build();

            // when
            TriggerResult result = strategy.evaluate(context);

            // then
            assertThat(result.isTriggered()).isTrue();
        }
    }

    @Nested
    @DisplayName("데이터 파싱 테스트")
    class DataParsingTest {

        @Test
        @DisplayName("온도 데이터가 없으면 트리거 발동하지 않음")
        void evaluate_NoTemperatureData_DoesNotTrigger() {
            // given
            testUser.addInterest(InterestCategory.WEATHER);
            
            Map<String, Object> emptyData = Map.of();
            
            TriggerContext context = TriggerContext.builder()
                    .user(testUser)
                    .userInterests(List.of(InterestCategory.WEATHER))
                    .publicApiData(emptyData)
                    .build();

            // when
            TriggerResult result = strategy.evaluate(context);

            // then
            assertThat(result.isTriggered()).isFalse();
        }

        @Test
        @DisplayName("날씨 데이터 리스트가 비어있으면 트리거 발동하지 않음")
        void evaluate_EmptyWeatherList_DoesNotTrigger() {
            // given
            testUser.addInterest(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = Map.of(
                "WEATHER_STTS", List.of()
            );
            
            TriggerContext context = TriggerContext.builder()
                    .user(testUser)
                    .userInterests(List.of(InterestCategory.WEATHER))
                    .publicApiData(weatherData)
                    .build();

            // when
            TriggerResult result = strategy.evaluate(context);

            // then
            assertThat(result.isTriggered()).isFalse();
        }

        @Test
        @DisplayName("온도 데이터가 숫자 타입인 경우 정상 처리")
        void evaluate_NumericTemperature_ProcessedCorrectly() {
            // given
            testUser.addInterest(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = Map.of(
                "WEATHER_STTS", List.of(
                    Map.of("TEMP", 36.5) // Double 타입
                )
            );
            
            TriggerContext context = TriggerContext.builder()
                    .user(testUser)
                    .userInterests(List.of(InterestCategory.WEATHER))
                    .publicApiData(weatherData)
                    .build();

            // when
            TriggerResult result = strategy.evaluate(context);

            // then
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getMessage()).contains("36.5°C");
        }

        @Test
        @DisplayName("온도 데이터 파싱 실패 시 트리거 발동하지 않음")
        void evaluate_InvalidTemperatureFormat_DoesNotTrigger() {
            // given
            testUser.addInterest(InterestCategory.WEATHER);
            
            Map<String, Object> weatherData = Map.of(
                "WEATHER_STTS", List.of(
                    Map.of("TEMP", "invalid_temperature")
                )
            );
            
            TriggerContext context = TriggerContext.builder()
                    .user(testUser)
                    .userInterests(List.of(InterestCategory.WEATHER))
                    .publicApiData(weatherData)
                    .build();

            // when
            TriggerResult result = strategy.evaluate(context);

            // then
            assertThat(result.isTriggered()).isFalse();
        }
    }

    @Nested
    @DisplayName("전략 메타데이터 테스트")
    class StrategyMetadataTest {

        @Test
        @DisplayName("지원하는 트리거 타입 반환")
        void getSupportedTriggerType_ReturnsTemperature() {
            // when & then
            assertThat(strategy.getSupportedTriggerType()).isEqualTo("TEMPERATURE");
        }

        @Test
        @DisplayName("우선순위 반환")
        void getPriority_ReturnsHighPriority() {
            // when & then
            assertThat(strategy.getPriority()).isEqualTo(10);
        }

        @Test
        @DisplayName("설명 반환")
        void getDescription_ReturnsDescription() {
            // when
            String description = strategy.getDescription();

            // then
            assertThat(description).isNotBlank();
            assertThat(description).contains("임계값");
            assertThat(description).contains("알림");
        }

        @Test
        @DisplayName("기본적으로 활성화됨")
        void isEnabled_DefaultTrue() {
            // when & then
            assertThat(strategy.isEnabled()).isTrue();
        }
    }
}
