package com.seoulfit.backend.trigger.strategy.impl;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.publicdata.culture.adapter.out.custom.CulturalEventRepository;
import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import com.seoulfit.backend.trigger.dto.TriggerContext;
import com.seoulfit.backend.trigger.dto.TriggerResult;
import com.seoulfit.backend.trigger.infrastructure.TriggerHistoryRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CulturalEventTriggerStrategy 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CulturalEventTriggerStrategy 단위 테스트")
class CulturalEventTriggerStrategyTest {

    @Mock
    private CulturalEventRepository culturalEventRepository;
    
    @Mock
    private TriggerHistoryRepository triggerHistoryRepository;
    
    @InjectMocks
    private CulturalEventTriggerStrategy culturalEventTriggerStrategy;
    
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
                .userLatitude(37.5665)
                .userLongitude(126.9780)
                .build();
        
        // 기본 설정값 주입
        ReflectionTestUtils.setField(culturalEventTriggerStrategy, "searchRadius", 2.0);
        ReflectionTestUtils.setField(culturalEventTriggerStrategy, "upcomingDays", 3);
    }
    
    @Nested
    @DisplayName("문화행사 트리거 평가 테스트")
    class EvaluateTest {
        
        @Test
        @DisplayName("트리거 발동 - 진행 중인 문화행사")
        void evaluate_TriggerOnOngoingEvent() {
            // given
            userInterests.add(InterestCategory.CULTURE);
            
            CulturalEvent ongoingEvent = createCulturalEvent(
                1L, "서울 음악 페스티벌", "서울 광장",
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(2),
                37.5665, 126.9780, "중구", true
            );
            
            when(culturalEventRepository.findWithInRadius(any(BigDecimal.class), any(BigDecimal.class), eq(2.0)))
                    .thenReturn(Arrays.asList(ongoingEvent));
            when(triggerHistoryRepository.existsByUserIdAndCulturalEventId(1L, "1"))
                    .thenReturn(false);
            
            // when
            TriggerResult result = culturalEventTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getNotificationType()).isEqualTo(NotificationType.CULTURE);
            assertThat(result.getTriggerCondition()).isEqualTo(TriggerCondition.CULTURAL_EVENT);
            assertThat(result.getTitle()).isEqualTo("진행 중인 문화행사");
            assertThat(result.getMessage()).contains("서울 음악 페스티벌");
            assertThat(result.getMessage()).contains("진행 중입니다");
            assertThat(result.getLocationInfo()).contains("중구");
        }
        
        @Test
        @DisplayName("트리거 발동 - 곧 시작될 문화행사 (무료)")
        void evaluate_TriggerOnUpcomingFreeEvent() {
            // given
            userInterests.add(InterestCategory.CULTURE);
            
            CulturalEvent upcomingEvent = createCulturalEvent(
                2L, "한강 불꽃축제", "한강공원",
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(3),
                37.5172, 126.9631, "용산구", true
            );
            
            when(culturalEventRepository.findWithInRadius(any(BigDecimal.class), any(BigDecimal.class), eq(2.0)))
                    .thenReturn(Arrays.asList(upcomingEvent));
            when(triggerHistoryRepository.existsByUserIdAndCulturalEventId(1L, "2"))
                    .thenReturn(false);
            
            // when
            TriggerResult result = culturalEventTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getTriggerCondition()).isEqualTo(TriggerCondition.CULTURAL_EVENT_START);
            assertThat(result.getTitle()).isEqualTo("곧 시작될 문화행사");
            assertThat(result.getMessage()).contains("한강 불꽃축제");
            assertThat(result.getMessage()).contains("2일 후 시작됩니다");
            assertThat(result.getMessage()).contains("(무료)");
        }
        
        @Test
        @DisplayName("트리거 발동 - 곧 시작될 문화행사 (유료)")
        void evaluate_TriggerOnUpcomingPaidEvent() {
            // given
            userInterests.add(InterestCategory.CULTURE);
            
            CulturalEvent paidEvent = createCulturalEvent(
                3L, "뮤지컬 공연", "세종문화회관",
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(10),
                37.5726, 126.9768, "종로구", false
            );
            
            when(culturalEventRepository.findWithInRadius(any(BigDecimal.class), any(BigDecimal.class), eq(2.0)))
                    .thenReturn(Arrays.asList(paidEvent));
            when(triggerHistoryRepository.existsByUserIdAndCulturalEventId(1L, "3"))
                    .thenReturn(false);
            
            // when
            TriggerResult result = culturalEventTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getMessage()).contains("뮤지컬 공연");
            assertThat(result.getMessage()).doesNotContain("(무료)");
        }
        
        @Test
        @DisplayName("트리거 미발동 - 사용자 관심사 없음")
        void evaluate_NoTriggerWhenNoCultureInterest() {
            // given
            userInterests.add(InterestCategory.SPORTS);  // CULTURE 관심사 없음
            
            // when
            TriggerResult result = culturalEventTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
            verify(culturalEventRepository, never()).findWithInRadius(any(), any(), anyDouble());
        }
        
        @Test
        @DisplayName("트리거 미발동 - 위치 정보 없음")
        void evaluate_NoTriggerWhenNoLocation() {
            // given
            userInterests.add(InterestCategory.CULTURE);
            
            context = TriggerContext.builder()
                    .user(mockUser)
                    .publicApiData(publicApiData)
                    .userInterests(userInterests)
                    .userLatitude(null)
                    .userLongitude(null)
                    .build();
            
            // when
            TriggerResult result = culturalEventTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
            verify(culturalEventRepository, never()).findWithInRadius(any(), any(), anyDouble());
        }
        
        @Test
        @DisplayName("트리거 미발동 - 주변에 문화행사 없음")
        void evaluate_NoTriggerWhenNoNearbyEvents() {
            // given
            userInterests.add(InterestCategory.CULTURE);
            
            when(culturalEventRepository.findWithInRadius(any(BigDecimal.class), any(BigDecimal.class), eq(2.0)))
                    .thenReturn(Collections.emptyList());
            
            // when
            TriggerResult result = culturalEventTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
        }
        
        @Test
        @DisplayName("트리거 미발동 - 이미 알림 받은 행사")
        void evaluate_NoTriggerWhenAlreadyNotified() {
            // given
            userInterests.add(InterestCategory.CULTURE);
            
            CulturalEvent event = createCulturalEvent(
                4L, "이미 알림 받은 행사", "장소",
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1),
                37.5665, 126.9780, "중구", true
            );
            
            when(culturalEventRepository.findWithInRadius(any(BigDecimal.class), any(BigDecimal.class), eq(2.0)))
                    .thenReturn(Arrays.asList(event));
            when(triggerHistoryRepository.existsByUserIdAndCulturalEventId(1L, "4"))
                    .thenReturn(true);  // 이미 알림 받음
            
            // when
            TriggerResult result = culturalEventTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
        }
        
        @Test
        @DisplayName("트리거 미발동 - 너무 먼 미래의 행사")
        void evaluate_NoTriggerWhenEventTooFarInFuture() {
            // given
            userInterests.add(InterestCategory.CULTURE);
            
            CulturalEvent farFutureEvent = createCulturalEvent(
                5L, "먼 미래 행사", "장소",
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                37.5665, 126.9780, "중구", true
            );
            
            when(culturalEventRepository.findWithInRadius(any(BigDecimal.class), any(BigDecimal.class), eq(2.0)))
                    .thenReturn(Arrays.asList(farFutureEvent));
            when(triggerHistoryRepository.existsByUserIdAndCulturalEventId(anyLong(), anyString()))
                    .thenReturn(false);
            
            // when
            TriggerResult result = culturalEventTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
        }
        
        @Test
        @DisplayName("트리거 발동 - 여러 행사 중 가장 가까운 행사 선택")
        void evaluate_SelectNearestUpcomingEvent() {
            // given
            userInterests.add(InterestCategory.CULTURE);
            
            CulturalEvent event1 = createCulturalEvent(
                6L, "3일 후 행사", "장소1",
                LocalDate.now().plusDays(3), LocalDate.now().plusDays(5),
                37.5665, 126.9780, "중구", true
            );
            
            CulturalEvent event2 = createCulturalEvent(
                7L, "1일 후 행사", "장소2",
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2),
                37.5665, 126.9780, "중구", true
            );
            
            when(culturalEventRepository.findWithInRadius(any(BigDecimal.class), any(BigDecimal.class), eq(2.0)))
                    .thenReturn(Arrays.asList(event1, event2));
            when(triggerHistoryRepository.existsByUserIdAndCulturalEventId(anyLong(), anyString()))
                    .thenReturn(false);
            
            // when
            TriggerResult result = culturalEventTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isTrue();
            assertThat(result.getMessage()).contains("1일 후 행사");  // 더 가까운 행사가 선택됨
            assertThat(result.getMessage()).contains("1일 후");
        }
    }
    
    @Nested
    @DisplayName("예외 처리 테스트")
    class ExceptionHandlingTest {
        
        @Test
        @DisplayName("Repository 조회 중 예외 발생 - 빈 리스트 반환")
        void findNearbyEvents_ExceptionReturnsEmptyList() {
            // given
            userInterests.add(InterestCategory.CULTURE);
            
            when(culturalEventRepository.findWithInRadius(any(BigDecimal.class), any(BigDecimal.class), eq(2.0)))
                    .thenThrow(new RuntimeException("DB 오류"));
            
            // when
            TriggerResult result = culturalEventTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isFalse();
        }
        
        @Test
        @DisplayName("중복 확인 중 예외 발생 - 알림 허용")
        void isAlreadyNotified_ExceptionAllowsNotification() {
            // given
            userInterests.add(InterestCategory.CULTURE);
            
            CulturalEvent event = createCulturalEvent(
                8L, "테스트 행사", "장소",
                LocalDate.now(), LocalDate.now().plusDays(1),
                37.5665, 126.9780, "중구", true
            );
            
            when(culturalEventRepository.findWithInRadius(any(BigDecimal.class), any(BigDecimal.class), eq(2.0)))
                    .thenReturn(Arrays.asList(event));
            when(triggerHistoryRepository.existsByUserIdAndCulturalEventId(1L, "8"))
                    .thenThrow(new RuntimeException("중복 확인 오류"));
            
            // when
            TriggerResult result = culturalEventTriggerStrategy.evaluate(context);
            
            // then
            assertThat(result.isTriggered()).isTrue();  // 오류 시 알림 허용
        }
    }
    
    @Nested
    @DisplayName("트리거 메타데이터 테스트")
    class MetadataTest {
        
        @Test
        @DisplayName("지원 트리거 타입 확인")
        void getSupportedTriggerType() {
            // when
            String type = culturalEventTriggerStrategy.getSupportedTriggerType();
            
            // then
            assertThat(type).isEqualTo("CULTURAL_EVENT");
        }
        
        @Test
        @DisplayName("우선순위 확인")
        void getPriority() {
            // when
            int priority = culturalEventTriggerStrategy.getPriority();
            
            // then
            assertThat(priority).isEqualTo(40);
        }
        
        @Test
        @DisplayName("설명 확인")
        void getDescription() {
            // when
            String description = culturalEventTriggerStrategy.getDescription();
            
            // then
            assertThat(description).contains("문화행사");
            assertThat(description).contains("데이터베이스");
            assertThat(description).contains("진행 중");
        }
    }
    
    // Helper methods
    private CulturalEvent createCulturalEvent(Long id, String title, String place,
                                             LocalDate startDate, LocalDate endDate,
                                             Double latitude, Double longitude,
                                             String district, boolean isFree) {
        CulturalEvent event = CulturalEvent.builder()
                .externalId("EVT" + id)
                .title(title)
                .place(place)
                .startDate(startDate)
                .endDate(endDate)
                .latitude(BigDecimal.valueOf(latitude))
                .longitude(BigDecimal.valueOf(longitude))
                .district(district)
                .useFee(isFree ? "무료" : "10,000원")
                .isFree(isFree ? "무료" : "유료")
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