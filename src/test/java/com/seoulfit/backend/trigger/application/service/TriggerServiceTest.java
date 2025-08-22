package com.seoulfit.backend.trigger.application.service;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationCommand;
import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationResult;
import com.seoulfit.backend.trigger.application.port.out.PublicDataPort;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import com.seoulfit.backend.trigger.dto.TriggerContext;
import com.seoulfit.backend.trigger.dto.TriggerResult;
import com.seoulfit.backend.trigger.strategy.TriggerStrategy;
import com.seoulfit.backend.user.application.port.out.UserPort;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TriggerService 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class TriggerServiceTest {

    @Mock
    private UserPort userPort;

    @Mock
    private PublicDataPort publicDataPort;

    @Mock
    private TriggerStrategy mockStrategy1;

    @Mock
    private TriggerStrategy mockStrategy2;

    private TriggerService triggerService;

    private User testUser;
    private TriggerEvaluationCommand command;
    private Map<String, Object> publicData;

    @BeforeEach
    void setUp() {
        // TriggerService 초기화
        List<TriggerStrategy> strategies = Arrays.asList(mockStrategy1, mockStrategy2);
        triggerService = new TriggerService(strategies, userPort, publicDataPort);

        // 테스트 사용자 생성
        testUser = createTestUser(1L, "test@example.com", 
                Arrays.asList(InterestCategory.WEATHER, InterestCategory.CULTURE));

        // 트리거 평가 명령 생성
        command = TriggerEvaluationCommand.of(
                1L,
                Arrays.asList(InterestCategory.WEATHER, InterestCategory.CULTURE),
                37.5665,
                126.9780,
                "서울특별시 중구 광화문"
        );

        // 공공데이터 샘플
        publicData = new HashMap<>();
        publicData.put("WEATHER_STTS", Map.of("temperature", 25.0, "pm10", 50));
        publicData.put("BIKE_SHARE", Map.of("availableBikes", 10));
    }

    @Test
    @DisplayName("전체 트리거 평가 - 성공")
    void evaluateAllTriggers_Success() {
        // given
        List<User> activeUsers = Arrays.asList(testUser);
        when(userPort.findAllActiveUsers()).thenReturn(activeUsers);
        when(userPort.findById(1L)).thenReturn(Optional.of(testUser));
        when(publicDataPort.getCityData(anyString())).thenReturn(publicData);
        when(publicDataPort.getBikeShareData()).thenReturn(Map.of("availableBikes", 10));
        when(publicDataPort.getAirQualityData()).thenReturn(Map.of("pm10", 50));
        when(publicDataPort.getRainfallData()).thenReturn(Map.of("rainfall", 0));

        // 전략 설정
        when(mockStrategy1.isEnabled()).thenReturn(true);
        when(mockStrategy1.getPriority()).thenReturn(1);
        when(mockStrategy1.evaluate(any(TriggerContext.class)))
                .thenReturn(TriggerResult.triggered(
                        NotificationType.WEATHER,
                        TriggerCondition.TEMPERATURE_HIGH,
                        "온도 알림",
                        "현재 온도가 25도입니다",
                        "서울특별시 중구"));

        when(mockStrategy2.isEnabled()).thenReturn(false); // 비활성화된 전략

        // when
        List<TriggerEvaluationResult> results = triggerService.evaluateAllTriggers();

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
        verify(userPort).findAllActiveUsers();
        verify(mockStrategy1).evaluate(any(TriggerContext.class));
        verify(mockStrategy2, never()).evaluate(any(TriggerContext.class)); // 비활성화된 전략은 실행되지 않음
    }

    @Test
    @DisplayName("사용자별 트리거 평가 - 트리거 발동")
    void evaluateTriggersForUser_Triggered() {
        // given
        when(userPort.findById(1L)).thenReturn(Optional.of(testUser));
        when(publicDataPort.getCityData(anyString())).thenReturn(publicData);
        when(publicDataPort.getBikeShareData()).thenReturn(Map.of("availableBikes", 10));
        when(publicDataPort.getAirQualityData()).thenReturn(Map.of("pm10", 50));
        when(publicDataPort.getRainfallData()).thenReturn(Map.of("rainfall", 0));

        // 전략 설정 - 둘 다 활성화되고 트리거 발동
        when(mockStrategy1.isEnabled()).thenReturn(true);
        when(mockStrategy1.getPriority()).thenReturn(1);
        when(mockStrategy1.evaluate(any(TriggerContext.class)))
                .thenReturn(TriggerResult.triggered(
                        NotificationType.WEATHER,
                        TriggerCondition.TEMPERATURE_HIGH,
                        "온도 알림",
                        "현재 온도가 25도입니다",
                        "서울특별시 중구"));

        when(mockStrategy2.isEnabled()).thenReturn(true);
        when(mockStrategy2.getPriority()).thenReturn(2);
        when(mockStrategy2.evaluate(any(TriggerContext.class)))
                .thenReturn(TriggerResult.triggered(
                        NotificationType.WEATHER,
                        TriggerCondition.AIR_QUALITY_BAD,
                        "대기질 알림",
                        "미세먼지 농도가 높습니다",
                        "서울특별시 중구"));

        // when
        List<TriggerEvaluationResult> results = triggerService.evaluateTriggersForUser(command);

        // then
        assertThat(results).hasSize(1);
        TriggerEvaluationResult result = results.get(0);
        assertThat(result.isTriggered()).isTrue();
        assertThat(result.getTriggeredList()).hasSize(2);
        assertThat(result.getTotalEvaluated()).isEqualTo(2);
        
        verify(mockStrategy1).evaluate(any(TriggerContext.class));
        verify(mockStrategy2).evaluate(any(TriggerContext.class));
    }

    @Test
    @DisplayName("사용자별 트리거 평가 - 트리거 미발동")
    void evaluateTriggersForUser_NotTriggered() {
        // given
        when(userPort.findById(1L)).thenReturn(Optional.of(testUser));
        when(publicDataPort.getCityData(anyString())).thenReturn(publicData);
        when(publicDataPort.getBikeShareData()).thenReturn(Map.of());
        when(publicDataPort.getAirQualityData()).thenReturn(Map.of());
        when(publicDataPort.getRainfallData()).thenReturn(Map.of());

        // 전략 설정 - 트리거 미발동
        when(mockStrategy1.isEnabled()).thenReturn(true);
        when(mockStrategy1.getPriority()).thenReturn(1);
        when(mockStrategy1.evaluate(any(TriggerContext.class)))
                .thenReturn(TriggerResult.notTriggered());

        when(mockStrategy2.isEnabled()).thenReturn(true);
        when(mockStrategy2.getPriority()).thenReturn(2);
        when(mockStrategy2.evaluate(any(TriggerContext.class)))
                .thenReturn(TriggerResult.notTriggered());

        // when
        List<TriggerEvaluationResult> results = triggerService.evaluateTriggersForUser(command);

        // then
        assertThat(results).hasSize(1);
        TriggerEvaluationResult result = results.get(0);
        assertThat(result.isTriggered()).isFalse();
        assertThat(result.getTriggeredList()).isEmpty();
        assertThat(result.getTotalEvaluated()).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자별 트리거 평가 - 사용자 없음")
    void evaluateTriggersForUser_UserNotFound() {
        // given
        when(userPort.findById(999L)).thenReturn(Optional.empty());

        TriggerEvaluationCommand invalidCommand = TriggerEvaluationCommand.of(
                999L,
                Arrays.asList(InterestCategory.WEATHER),
                37.5665,
                126.9780,
                "서울특별시"
        );

        // when & then
        assertThatThrownBy(() -> triggerService.evaluateTriggersForUser(invalidCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다: 999");

        verify(publicDataPort, never()).getCityData(anyString());
    }

    @Test
    @DisplayName("사용자별 트리거 평가 - 전략 실행 중 예외 처리")
    void evaluateTriggersForUser_StrategyException() {
        // given
        when(userPort.findById(1L)).thenReturn(Optional.of(testUser));
        when(publicDataPort.getCityData(anyString())).thenReturn(publicData);
        when(publicDataPort.getBikeShareData()).thenReturn(Map.of());
        when(publicDataPort.getAirQualityData()).thenReturn(Map.of());
        when(publicDataPort.getRainfallData()).thenReturn(Map.of());

        // 전략 설정 - 하나는 예외 발생, 하나는 정상
        when(mockStrategy1.isEnabled()).thenReturn(true);
        when(mockStrategy1.getPriority()).thenReturn(1);
        when(mockStrategy1.evaluate(any(TriggerContext.class)))
                .thenThrow(new RuntimeException("전략 실행 오류"));

        when(mockStrategy2.isEnabled()).thenReturn(true);
        when(mockStrategy2.getPriority()).thenReturn(2);
        when(mockStrategy2.evaluate(any(TriggerContext.class)))
                .thenReturn(TriggerResult.triggered(
                        NotificationType.WEATHER,
                        TriggerCondition.WEATHER_CHANGE,
                        "정상 알림",
                        "정상적으로 실행됨",
                        "서울특별시"));

        // when
        List<TriggerEvaluationResult> results = triggerService.evaluateTriggersForUser(command);

        // then
        assertThat(results).hasSize(1);
        TriggerEvaluationResult result = results.get(0);
        assertThat(result.isTriggered()).isTrue();
        assertThat(result.getTriggeredList()).hasSize(1); // 정상 실행된 전략만
        assertThat(result.getTriggeredList().get(0).getTitle()).isEqualTo("정상 알림");
    }

    @Test
    @DisplayName("실시간 트리거 평가 - 성공")
    void evaluateRealtimeTriggers_Success() {
        // given
        List<User> weatherInterestedUsers = Arrays.asList(testUser);
        when(userPort.findUsersByInterest(InterestCategory.WEATHER))
                .thenReturn(weatherInterestedUsers);
        when(userPort.findById(1L)).thenReturn(Optional.of(testUser));
        when(publicDataPort.getCityData(anyString())).thenReturn(publicData);
        when(publicDataPort.getBikeShareData()).thenReturn(Map.of("availableBikes", 10));
        when(publicDataPort.getAirQualityData()).thenReturn(Map.of("pm10", 50));
        when(publicDataPort.getRainfallData()).thenReturn(Map.of("rainfall", 0));

        when(mockStrategy1.isEnabled()).thenReturn(true);
        when(mockStrategy1.getPriority()).thenReturn(1);
        when(mockStrategy1.evaluate(any(TriggerContext.class)))
                .thenReturn(TriggerResult.triggered(
                        NotificationType.WEATHER,
                        TriggerCondition.TEMPERATURE_HIGH,
                        "실시간 온도",
                        "현재 온도 25도",
                        "서울특별시"));

        when(mockStrategy2.isEnabled()).thenReturn(false);

        // when
        List<TriggerEvaluationResult> results = triggerService.evaluateRealtimeTriggers();

        // then
        assertThat(results).isNotEmpty();
        verify(userPort).findUsersByInterest(InterestCategory.WEATHER);
        verify(mockStrategy1).evaluate(any(TriggerContext.class));
    }

    @Test
    @DisplayName("문화행사 트리거 평가 - 성공")
    void evaluateCulturalEventTriggers_Success() {
        // given
        User culturalUser = createTestUser(2L, "culture@example.com", 
                Arrays.asList(InterestCategory.CULTURE));
        List<User> cultureInterestedUsers = Arrays.asList(culturalUser);
        
        when(userPort.findUsersByInterest(InterestCategory.CULTURE))
                .thenReturn(cultureInterestedUsers);
        when(userPort.findById(2L)).thenReturn(Optional.of(culturalUser));
        when(publicDataPort.getCityData(anyString())).thenReturn(Map.of());
        when(publicDataPort.getBikeShareData()).thenReturn(Map.of());
        when(publicDataPort.getAirQualityData()).thenReturn(Map.of());
        when(publicDataPort.getRainfallData()).thenReturn(Map.of());

        when(mockStrategy1.isEnabled()).thenReturn(true);
        when(mockStrategy1.getPriority()).thenReturn(1);
        when(mockStrategy1.evaluate(any(TriggerContext.class)))
                .thenReturn(TriggerResult.triggered(
                        NotificationType.CULTURE,
                        TriggerCondition.CULTURAL_EVENT,
                        "문화행사",
                        "새로운 전시회가 있습니다",
                        "서울특별시"));

        when(mockStrategy2.isEnabled()).thenReturn(false);

        // when
        List<TriggerEvaluationResult> results = triggerService.evaluateCulturalEventTriggers();

        // then
        assertThat(results).isNotEmpty();
        verify(userPort).findUsersByInterest(InterestCategory.CULTURE);
        verify(mockStrategy1).evaluate(any(TriggerContext.class));
    }

    @Test
    @DisplayName("트리거 평가 - 공공데이터 조회 실패 시에도 계속 진행")
    void evaluateTriggersForUser_PublicDataFailure() {
        // given
        when(userPort.findById(1L)).thenReturn(Optional.of(testUser));
        
        // 공공데이터 조회 실패
        when(publicDataPort.getCityData(anyString()))
                .thenThrow(new RuntimeException("API 호출 실패"));
        when(publicDataPort.getBikeShareData())
                .thenThrow(new RuntimeException("API 호출 실패"));
        when(publicDataPort.getAirQualityData())
                .thenThrow(new RuntimeException("API 호출 실패"));
        when(publicDataPort.getRainfallData())
                .thenThrow(new RuntimeException("API 호출 실패"));

        when(mockStrategy1.isEnabled()).thenReturn(true);
        when(mockStrategy1.getPriority()).thenReturn(1);
        when(mockStrategy1.evaluate(any(TriggerContext.class)))
                .thenReturn(TriggerResult.notTriggered());

        when(mockStrategy2.isEnabled()).thenReturn(false);

        // when
        List<TriggerEvaluationResult> results = triggerService.evaluateTriggersForUser(command);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).isTriggered()).isFalse();
        // 공공데이터 조회 실패해도 트리거 평가는 진행됨
        verify(mockStrategy1).evaluate(any(TriggerContext.class));
    }

    @Test
    @DisplayName("트리거 전략 우선순위 정렬 확인")
    void evaluateTriggersForUser_PriorityOrdering() {
        // given
        when(userPort.findById(1L)).thenReturn(Optional.of(testUser));
        when(publicDataPort.getCityData(anyString())).thenReturn(publicData);
        when(publicDataPort.getBikeShareData()).thenReturn(Map.of());
        when(publicDataPort.getAirQualityData()).thenReturn(Map.of());
        when(publicDataPort.getRainfallData()).thenReturn(Map.of());

        // 전략 우선순위 설정 (strategy2가 더 높은 우선순위)
        when(mockStrategy1.isEnabled()).thenReturn(true);
        when(mockStrategy1.getPriority()).thenReturn(10); // 낮은 우선순위

        when(mockStrategy2.isEnabled()).thenReturn(true);
        when(mockStrategy2.getPriority()).thenReturn(1); // 높은 우선순위

        // 실행 순서 확인을 위한 Answer 설정
        List<String> executionOrder = new ArrayList<>();
        when(mockStrategy1.evaluate(any(TriggerContext.class))).thenAnswer(invocation -> {
            executionOrder.add("strategy1");
            return TriggerResult.notTriggered();
        });
        when(mockStrategy2.evaluate(any(TriggerContext.class))).thenAnswer(invocation -> {
            executionOrder.add("strategy2");
            return TriggerResult.notTriggered();
        });

        // when
        triggerService.evaluateTriggersForUser(command);

        // then
        assertThat(executionOrder).containsExactly("strategy2", "strategy1"); // 우선순위 순서대로 실행
    }

    // 헬퍼 메서드
    private User createTestUser(Long id, String email, List<InterestCategory> interests) {
        User user = User.builder()
                .email(email)
                .nickname("testuser" + id)
                .locationLatitude(37.5665)
                .locationLongitude(126.9780)
                .locationAddress("서울특별시 중구 광화문")
                .status(UserStatus.ACTIVE)
                .build();
        
        // Reflection으로 ID와 관심사 설정
        setUserId(user, id);
        setUserInterests(user, interests);
        
        return user;
    }

    private void setUserId(User user, Long id) {
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set user ID", e);
        }
    }

    private void setUserInterests(User user, List<InterestCategory> interests) {
        try {
            java.lang.reflect.Field interestsField = User.class.getDeclaredField("interestCategories");
            interestsField.setAccessible(true);
            interestsField.set(user, interests);
        } catch (Exception e) {
            // 필드가 없을 수 있으므로 무시
        }
    }
}