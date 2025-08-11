package com.seoulfit.backend.trigger;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import com.seoulfit.backend.trigger.dto.TriggerContext;
import com.seoulfit.backend.trigger.dto.TriggerResult;
import com.seoulfit.backend.trigger.strategy.TriggerStrategy;
import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TriggerManager 테스트 클래스입니다.
 * 
 * @author Seoul Fit
 * @since 2025-01-01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TriggerManager 테스트")
class TriggerManagerTest {

    @Mock
    private TriggerStrategy mockStrategy;

    private TriggerManager triggerManager;
    private User testUser;
    private TriggerContext testContext;

    @BeforeEach
    void setUp() {
        List<TriggerStrategy> strategies = List.of(mockStrategy);
        triggerManager = new TriggerManager(strategies);
        
        // 테스트용 사용자 생성
        testUser = User.createOAuthUser(
            AuthProvider.KAKAO,
            "12345",
            "테스트사용자",
            "test@example.com",
            "https://example.com/profile.jpg"
        );
        testUser.addInterest(InterestCategory.WEATHER);
        
        // 테스트용 컨텍스트 생성
        testContext = TriggerContext.builder()
            .user(testUser)
            .userInterests(List.of(InterestCategory.WEATHER))
            .userLatitude(37.5665)
            .userLongitude(126.9780)
            .publicApiData(Map.of(
                "temperature", 35.5,
                "location", "서울시 강남구"
            ))
            .build();
    }

    @Test
    @DisplayName("트리거 발동 시 결과 반환")
    void evaluateAll_TriggeredResult_ReturnsResult() {
        // given
        TriggerResult triggeredResult = TriggerResult.builder()
            .triggered(true)
            .notificationType(NotificationType.WEATHER)
            .triggerCondition(TriggerCondition.TEMPERATURE_HIGH)
            .title("폭염 경보")
            .message("현재 온도가 35도를 초과했습니다.")
            .locationInfo("서울시 강남구")
            .build();

        when(mockStrategy.isEnabled()).thenReturn(true);
        when(mockStrategy.getPriority()).thenReturn(1);
        when(mockStrategy.evaluate(testContext)).thenReturn(triggeredResult);

        // when
        Optional<TriggerResult> result = triggerManager.evaluateAll(testContext);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().isTriggered()).isTrue();
        assertThat(result.get().getTitle()).isEqualTo("폭염 경보");
    }

    @Test
    @DisplayName("전략이 비활성화되면 빈 결과 반환")
    void evaluateAll_StrategyDisabled_ReturnsEmpty() {
        // given
        when(mockStrategy.isEnabled()).thenReturn(false);

        // when
        Optional<TriggerResult> result = triggerManager.evaluateAll(testContext);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("트리거가 발동되지 않으면 빈 결과 반환")
    void evaluateAll_NotTriggered_ReturnsEmpty() {
        // given
        TriggerResult notTriggeredResult = TriggerResult.notTriggered();

        when(mockStrategy.isEnabled()).thenReturn(true);
        when(mockStrategy.getPriority()).thenReturn(1);
        when(mockStrategy.evaluate(testContext)).thenReturn(notTriggeredResult);

        // when
        Optional<TriggerResult> result = triggerManager.evaluateAll(testContext);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("특정 타입 트리거 평가 성공")
    void evaluateByType_Success() {
        // given
        String targetType = "TEMPERATURE";
        TriggerResult expectedResult = TriggerResult.builder()
            .triggered(true)
            .notificationType(NotificationType.WEATHER)
            .triggerCondition(TriggerCondition.TEMPERATURE_HIGH)
            .title("고온 경보")
            .message("현재 온도가 35도를 초과했습니다.")
            .locationInfo("서울시 강남구")
            .build();

        when(mockStrategy.getSupportedTriggerType()).thenReturn(targetType);
        when(mockStrategy.isEnabled()).thenReturn(true);
        when(mockStrategy.evaluate(testContext)).thenReturn(expectedResult);

        // when
        Optional<TriggerResult> result = triggerManager.evaluateByType(testContext, targetType);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("해당 타입의 전략이 없으면 빈 결과 반환")
    void evaluateByType_NoMatchingStrategy_ReturnsEmpty() {
        // given
        String targetType = "NON_EXISTENT_TYPE";
        when(mockStrategy.getSupportedTriggerType()).thenReturn("TEMPERATURE");

        // when
        Optional<TriggerResult> result = triggerManager.evaluateByType(testContext, targetType);

        // then
        assertThat(result).isEmpty();
    }
}
