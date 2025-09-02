package com.seoulfit.backend.trigger.application.service;

import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationCommand;
import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationResult;
import com.seoulfit.backend.trigger.application.port.out.PublicDataPort;
import com.seoulfit.backend.trigger.dto.TriggerContext;
import com.seoulfit.backend.trigger.dto.TriggerResult;
import com.seoulfit.backend.trigger.strategy.TriggerStrategy;
import com.seoulfit.backend.user.application.port.out.UserPort;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserStatus;
import com.seoulfit.backend.notification.domain.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

/**
 * TriggerService 단위 테스트
 * 
 * <p>트리거 서비스의 핵심 비즈니스 로직을 검증하는 단위 테스트입니다.
 * 다양한 트리거 전략의 평가와 사용자별 트리거 실행을 테스트합니다.</p>
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TriggerService 단위 테스트")
class TriggerServiceTest {

    @Mock
    private UserPort userPort;
    
    @Mock
    private PublicDataPort publicDataPort;
    
    @Mock
    private TriggerStrategy mockStrategy1;
    
    @Mock
    private TriggerStrategy mockStrategy2;
    
    @Mock
    private TriggerStrategy mockStrategy3;
    
    private TriggerService triggerService;
    
    private User testUser;
    private static final Long TEST_USER_ID = 1L;
    private static final Double TEST_LATITUDE = 37.5665;
    private static final Double TEST_LONGITUDE = 126.9780;
    private static final String TEST_ADDRESS = "서울시 중구";
    
    @BeforeEach
    void setUp() {
        List<TriggerStrategy> strategies = List.of(mockStrategy1, mockStrategy2, mockStrategy3);
        triggerService = new TriggerService(strategies, userPort, publicDataPort);
        
        testUser = createTestUser();
        
        // 기본 Mock 설정
        setupDefaultMockBehavior();
    }
    
    private void setupDefaultMockBehavior() {
        // 전략 기본 설정
        given(mockStrategy1.isEnabled()).willReturn(true);
        given(mockStrategy1.getPriority()).willReturn(10);
        given(mockStrategy1.getSupportedTriggerType()).willReturn("WEATHER");
        
        given(mockStrategy2.isEnabled()).willReturn(true);
        given(mockStrategy2.getPriority()).willReturn(20);
        given(mockStrategy2.getSupportedTriggerType()).willReturn("CULTURE");
        
        given(mockStrategy3.isEnabled()).willReturn(false); // 비활성화된 전략
        given(mockStrategy3.getPriority()).willReturn(30);
        given(mockStrategy3.getSupportedTriggerType()).willReturn("EMERGENCY");
        
        // 공공데이터 기본 설정
        given(publicDataPort.getCityData(any())).willReturn(new HashMap<>());
        given(publicDataPort.getAirQualityData()).willReturn(new HashMap<>());
        given(publicDataPort.getCulturalEventData()).willReturn(new HashMap<>());
    }
    
    @Nested
    @DisplayName("사용자별 트리거 평가")
    class EvaluateTriggersForUserTest {
        
        @Test
        @DisplayName("트리거가 발동되지 않은 경우")
        void shouldReturnNotTriggeredWhenNoStrategyTriggers() {
            // given
            TriggerEvaluationCommand command = createEvaluationCommand();
            given(userPort.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            
            TriggerResult notTriggeredResult = TriggerResult.notTriggered();
            given(mockStrategy1.evaluate(any(TriggerContext.class))).willReturn(notTriggeredResult);
            given(mockStrategy2.evaluate(any(TriggerContext.class))).willReturn(notTriggeredResult);
            
            // when
            List<TriggerEvaluationResult> results = triggerService.evaluateTriggersForUser(command);
            
            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).isTriggered()).isFalse();
            assertThat(results.get(0).getTriggeredCount()).isEqualTo(0);
            assertThat(results.get(0).getTotalEvaluated()).isEqualTo(2); // 활성화된 전략만
            
            then(mockStrategy1).should().evaluate(any(TriggerContext.class));
            then(mockStrategy2).should().evaluate(any(TriggerContext.class));
            then(mockStrategy3).should(never()).evaluate(any()); // 비활성화된 전략은 실행 안됨
        }
        
        @Test
        @DisplayName("하나의 트리거가 발동된 경우")
        void shouldReturnTriggeredWhenOneStrategyTriggers() {
            // given
            TriggerEvaluationCommand command = createEvaluationCommand();
            given(userPort.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            
            TriggerResult triggeredResult = TriggerResult.triggered(
                NotificationType.WEATHER,
                com.seoulfit.backend.trigger.domain.TriggerCondition.TEMPERATURE_HIGH,
                "높은 온도 감지",
                "온도가 35도를 초과했습니다",
                "서울시 중구"
            );
            TriggerResult notTriggeredResult = TriggerResult.notTriggered();
            
            given(mockStrategy1.evaluate(any(TriggerContext.class))).willReturn(triggeredResult);
            given(mockStrategy2.evaluate(any(TriggerContext.class))).willReturn(notTriggeredResult);
            
            // when
            List<TriggerEvaluationResult> results = triggerService.evaluateTriggersForUser(command);
            
            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).isTriggered()).isTrue();
            assertThat(results.get(0).getTriggeredCount()).isEqualTo(1);
            assertThat(results.get(0).getTriggeredList()).hasSize(1);
            assertThat(results.get(0).getTriggeredList().get(0).getMessage())
                .isEqualTo("온도가 35도를 초과했습니다");
        }
        
        @Test
        @DisplayName("여러 트리거가 발동된 경우")
        void shouldReturnMultipleTriggeredWhenMultipleStrategiesTrigger() {
            // given
            TriggerEvaluationCommand command = createEvaluationCommand();
            given(userPort.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            
            TriggerResult triggeredResult1 = TriggerResult.triggered(
                NotificationType.WEATHER,
                com.seoulfit.backend.trigger.domain.TriggerCondition.TEMPERATURE_HIGH,
                "높은 온도",
                "온도가 35도를 초과했습니다",
                "서울"
            );
            TriggerResult triggeredResult2 = TriggerResult.triggered(
                NotificationType.WEATHER,
                com.seoulfit.backend.trigger.domain.TriggerCondition.AIR_QUALITY_BAD,
                "나쁜 대기질",
                "미세먼지 농도가 높습니다",
                "서울"
            );
            
            given(mockStrategy1.evaluate(any(TriggerContext.class))).willReturn(triggeredResult1);
            given(mockStrategy2.evaluate(any(TriggerContext.class))).willReturn(triggeredResult2);
            
            // when
            List<TriggerEvaluationResult> results = triggerService.evaluateTriggersForUser(command);
            
            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).isTriggered()).isTrue();
            assertThat(results.get(0).getTriggeredCount()).isEqualTo(2);
            assertThat(results.get(0).getTriggeredList()).hasSize(2);
        }
        
        @Test
        @DisplayName("사용자를 찾을 수 없는 경우 예외 발생")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            TriggerEvaluationCommand command = createEvaluationCommand();
            given(userPort.findById(TEST_USER_ID)).willReturn(Optional.empty());
            
            // when & then
            assertThatThrownBy(() -> triggerService.evaluateTriggersForUser(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("사용자를 찾을 수 없습니다: " + TEST_USER_ID);
            
            then(mockStrategy1).should(never()).evaluate(any());
            then(mockStrategy2).should(never()).evaluate(any());
        }
        
        @Test
        @DisplayName("트리거 평가 중 예외 발생 시 계속 진행")
        void shouldContinueEvaluationWhenStrategyThrowsException() {
            // given
            TriggerEvaluationCommand command = createEvaluationCommand();
            given(userPort.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            
            // 첫 번째 전략은 예외 발생
            given(mockStrategy1.evaluate(any(TriggerContext.class)))
                    .willThrow(new RuntimeException("전략 실행 오류"));
            
            // 두 번째 전략은 정상 실행
            TriggerResult triggeredResult = TriggerResult.triggered(
                NotificationType.WEATHER,
                com.seoulfit.backend.trigger.domain.TriggerCondition.TEMPERATURE_HIGH,
                "정상 트리거",
                "정상적으로 발동됨",
                "서울"
            );
            given(mockStrategy2.evaluate(any(TriggerContext.class))).willReturn(triggeredResult);
            
            // when
            List<TriggerEvaluationResult> results = triggerService.evaluateTriggersForUser(command);
            
            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).isTriggered()).isTrue();
            assertThat(results.get(0).getTriggeredCount()).isEqualTo(1);
            assertThat(results.get(0).getTriggeredList().get(0).getMessage())
                .isEqualTo("정상적으로 발동됨");
            
            then(mockStrategy1).should().evaluate(any(TriggerContext.class));
            then(mockStrategy2).should().evaluate(any(TriggerContext.class));
        }
    }
    
    @Nested
    @DisplayName("전체 트리거 평가")
    class EvaluateAllTriggersTest {
        
        @Test
        @DisplayName("모든 활성 사용자에 대해 트리거를 평가한다")
        void shouldEvaluateTriggersForAllActiveUsers() {
            // given
            User user1 = createTestUser(1L);
            User user2 = createTestUser(2L);
            List<User> activeUsers = List.of(user1, user2);
            
            given(userPort.findAllActiveUsers()).willReturn(activeUsers);
            given(userPort.findById(1L)).willReturn(Optional.of(user1));
            given(userPort.findById(2L)).willReturn(Optional.of(user2));
            
            TriggerResult notTriggeredResult = TriggerResult.notTriggered();
            given(mockStrategy1.evaluate(any(TriggerContext.class))).willReturn(notTriggeredResult);
            given(mockStrategy2.evaluate(any(TriggerContext.class))).willReturn(notTriggeredResult);
            
            // when
            List<TriggerEvaluationResult> results = triggerService.evaluateAllTriggers();
            
            // then
            assertThat(results).hasSize(2);
            then(userPort).should().findAllActiveUsers();
            then(mockStrategy1).should(times(2)).evaluate(any(TriggerContext.class));
            then(mockStrategy2).should(times(2)).evaluate(any(TriggerContext.class));
        }
        
        @Test
        @DisplayName("활성 사용자가 없는 경우 빈 결과를 반환한다")
        void shouldReturnEmptyWhenNoActiveUsers() {
            // given
            given(userPort.findAllActiveUsers()).willReturn(Collections.emptyList());
            
            // when
            List<TriggerEvaluationResult> results = triggerService.evaluateAllTriggers();
            
            // then
            assertThat(results).isEmpty();
            then(mockStrategy1).should(never()).evaluate(any());
            then(mockStrategy2).should(never()).evaluate(any());
        }
    }
    
    @Nested
    @DisplayName("실시간 트리거 평가")
    class EvaluateRealtimeTriggersTest {
        
        @Test
        @DisplayName("날씨 관심사를 가진 사용자에 대해 실시간 트리거를 평가한다")
        void shouldEvaluateRealtimeTriggersForWeatherInterestedUsers() {
            // given
            List<User> interestedUsers = List.of(testUser);
            given(userPort.findUsersByInterest(InterestCategory.WEATHER)).willReturn(interestedUsers);
            given(userPort.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            
            TriggerResult notTriggeredResult = TriggerResult.notTriggered();
            given(mockStrategy1.evaluate(any(TriggerContext.class))).willReturn(notTriggeredResult);
            given(mockStrategy2.evaluate(any(TriggerContext.class))).willReturn(notTriggeredResult);
            
            // when
            List<TriggerEvaluationResult> results = triggerService.evaluateRealtimeTriggers();
            
            // then
            assertThat(results).hasSize(1);
            then(userPort).should().findUsersByInterest(InterestCategory.WEATHER);
        }
    }
    
    @Nested
    @DisplayName("문화행사 트리거 평가")
    class EvaluateCulturalEventTriggersTest {
        
        @Test
        @DisplayName("문화 관심사를 가진 사용자에 대해 문화행사 트리거를 평가한다")
        void shouldEvaluateCulturalEventTriggersForCultureInterestedUsers() {
            // given
            List<User> interestedUsers = List.of(testUser);
            given(userPort.findUsersByInterest(InterestCategory.CULTURE)).willReturn(interestedUsers);
            given(userPort.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            
            TriggerResult notTriggeredResult = TriggerResult.notTriggered();
            given(mockStrategy1.evaluate(any(TriggerContext.class))).willReturn(notTriggeredResult);
            given(mockStrategy2.evaluate(any(TriggerContext.class))).willReturn(notTriggeredResult);
            
            // when
            List<TriggerEvaluationResult> results = triggerService.evaluateCulturalEventTriggers();
            
            // then
            assertThat(results).hasSize(1);
            then(userPort).should().findUsersByInterest(InterestCategory.CULTURE);
        }
    }
    
    /**
     * 테스트용 User 객체 생성 헬퍼 메서드
     */
    private User createTestUser() {
        return createTestUser(TEST_USER_ID);
    }
    
    private User createTestUser(Long userId) {
        User user = User.builder()
                .email("test" + userId + "@example.com")
                .nickname("테스트유저" + userId)
                .status(UserStatus.ACTIVE)
                .locationLatitude(TEST_LATITUDE)
                .locationLongitude(TEST_LONGITUDE)
                .locationAddress(TEST_ADDRESS)
                .build();
        
        // Reflection으로 ID와 관심사 설정
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, userId);
            
            // 관심사 추가
            user.updateInterests(List.of(
                InterestCategory.WEATHER,
                InterestCategory.CULTURE,
                InterestCategory.SPORTS
            ));
        } catch (Exception e) {
            throw new RuntimeException("테스트 데이터 생성 실패", e);
        }
        
        return user;
    }
    
    /**
     * 테스트용 TriggerEvaluationCommand 생성
     */
    private TriggerEvaluationCommand createEvaluationCommand() {
        return TriggerEvaluationCommand.of(
                TEST_USER_ID,
                List.of(InterestCategory.WEATHER, InterestCategory.CULTURE),
                TEST_LATITUDE,
                TEST_LONGITUDE,
                TEST_ADDRESS
        );
    }
    
    // Mock 전략 클래스 (클래스 이름 확인용)
    static class MockStrategy1 implements TriggerStrategy {
        @Override
        public TriggerResult evaluate(TriggerContext context) {
            return null;
        }
        
        @Override
        public String getDescription() {
            return "Mock Strategy 1";
        }
        
        @Override
        public String getSupportedTriggerType() {
            return "MOCK_TYPE_1";
        }
    }
    
    static class MockStrategy2 implements TriggerStrategy {
        @Override
        public TriggerResult evaluate(TriggerContext context) {
            return null;
        }
        
        @Override
        public String getDescription() {
            return "Mock Strategy 2";
        }
        
        @Override
        public String getSupportedTriggerType() {
            return "MOCK_TYPE_2";
        }
    }
    
    static class MockStrategy3 implements TriggerStrategy {
        @Override
        public TriggerResult evaluate(TriggerContext context) {
            return null;
        }
        
        @Override
        public String getDescription() {
            return "Mock Strategy 3";
        }
        
        @Override
        public String getSupportedTriggerType() {
            return "MOCK_TYPE_3";
        }
    }
}