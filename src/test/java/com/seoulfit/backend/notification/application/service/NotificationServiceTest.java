package com.seoulfit.backend.notification.application.service;

import com.seoulfit.backend.notification.application.port.in.dto.CreateNotificationCommand;
import com.seoulfit.backend.notification.application.port.in.dto.NotificationHistoryQuery;
import com.seoulfit.backend.notification.application.port.in.dto.NotificationHistoryResult;
import com.seoulfit.backend.notification.application.port.out.NotificationHistoryPort;
import com.seoulfit.backend.notification.application.port.out.NotificationSenderPort;
import com.seoulfit.backend.notification.domain.NotificationHistory;
import com.seoulfit.backend.notification.domain.NotificationStatus;
import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import com.seoulfit.backend.user.infrastructure.NotificationSettingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * NotificationService 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService 단위 테스트")
class NotificationServiceTest {

    @Mock
    private NotificationHistoryPort notificationHistoryPort;

    @Mock
    private NotificationSenderPort notificationSenderPort;

    @Mock
    private NotificationSettingRepository notificationSettingRepository;

    @InjectMocks
    private NotificationService notificationService;

    private CreateNotificationCommand createCommand;
    private NotificationHistory notificationHistory;

    @BeforeEach
    void setUp() {
        // 알림 생성 명령
        createCommand = CreateNotificationCommand.of(
                1L,
                NotificationType.WEATHER,
                "날씨 알림",
                "오늘 비가 예상됩니다",
                TriggerCondition.HEAVY_RAIN,
                "서울특별시 중구",
                10);

        // 알림 이력
        notificationHistory = NotificationHistory.create(
                1L,
                NotificationType.WEATHER,
                "날씨 알림",
                "오늘 비가 예상됩니다",
                TriggerCondition.HEAVY_RAIN,
                "서울특별시 중구"
        );
        setNotificationId(notificationHistory, 100L);
        
        // Mock 기본 설정 - 알림 발송 관련 메서드는 항상 빈 리스트 반환
        lenient().when(notificationSettingRepository.findByUserIdAndNotificationTypeAndIsActive(
                anyLong(), any(), anyBoolean()))
                .thenReturn(Collections.emptyList());
        lenient().when(notificationSettingRepository.findByUserIdAndNotificationTypeIsNullAndIsActive(
                anyLong(), anyBoolean()))
                .thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("알림 생성 - 성공")
    void createNotification_Success() {
        // given
        when(notificationHistoryPort.save(any(NotificationHistory.class)))
                .thenReturn(notificationHistory);

        // when
        NotificationHistoryResult result = notificationService.createNotification(createCommand);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("날씨 알림");
        assertThat(result.message()).isEqualTo("오늘 비가 예상됩니다");
        
        verify(notificationHistoryPort).save(any(NotificationHistory.class));
    }

    @Test
    @DisplayName("알림 이력 조회 - 타입별 조회")
    void getNotificationHistory_ByType() {
        // given
        NotificationHistoryQuery historyQuery = NotificationHistoryQuery.builder()
                .userId(1L)
                .type(NotificationType.WEATHER)
                .pageable(PageRequest.of(0, 10))
                .build();
                
        List<NotificationHistory> histories = Arrays.asList(notificationHistory);
        Page<NotificationHistory> page = new PageImpl<>(histories, PageRequest.of(0, 10), 1);
        
        when(notificationHistoryPort.findByUserIdAndType(
                eq(1L), eq(NotificationType.WEATHER), any(Pageable.class)))
                .thenReturn(page);

        // when
        Page<NotificationHistoryResult> results = notificationService.getNotificationHistory(historyQuery);

        // then
        assertThat(results).isNotNull();
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).type()).isEqualTo(NotificationType.WEATHER);
        
        verify(notificationHistoryPort).findByUserIdAndType(
                eq(1L), eq(NotificationType.WEATHER), any(Pageable.class));
    }

    @Test
    @DisplayName("알림 이력 조회 - 날짜 범위 조회")
    void getNotificationHistory_ByDateRange() {
        // given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        NotificationHistoryQuery dateQuery = NotificationHistoryQuery.builder()
                .userId(1L)
                .startDate(startDate)
                .endDate(endDate)
                .pageable(PageRequest.of(0, 10))
                .build();
                
        List<NotificationHistory> histories = Arrays.asList(notificationHistory);
        Page<NotificationHistory> page = new PageImpl<>(histories, PageRequest.of(0, 10), 1);
        
        when(notificationHistoryPort.findByUserIdAndDateRange(
                eq(1L), eq(startDate), eq(endDate), any(Pageable.class)))
                .thenReturn(page);

        // when
        Page<NotificationHistoryResult> results = notificationService.getNotificationHistory(dateQuery);

        // then
        assertThat(results).isNotNull();
        assertThat(results.getContent()).hasSize(1);
        
        verify(notificationHistoryPort).findByUserIdAndDateRange(
                eq(1L), eq(startDate), eq(endDate), any(Pageable.class));
    }

    @Test
    @DisplayName("알림 이력 조회 - 전체 조회")
    void getNotificationHistory_All() {
        // given
        NotificationHistoryQuery query = NotificationHistoryQuery.builder()
                .userId(1L)
                .pageable(PageRequest.of(0, 10))
                .build();
                
        List<NotificationHistory> histories = Arrays.asList(notificationHistory);
        Page<NotificationHistory> page = new PageImpl<>(histories, PageRequest.of(0, 10), 1);
        
        when(notificationHistoryPort.findByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        // when
        Page<NotificationHistoryResult> results = notificationService.getNotificationHistory(query);

        // then
        assertThat(results).isNotNull();
        assertThat(results.getContent()).hasSize(1);
        
        verify(notificationHistoryPort).findByUserId(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("알림 읽음 처리 - 성공")
    void markAsRead_Success() {
        // given
        when(notificationHistoryPort.findById(100L))
                .thenReturn(Optional.of(notificationHistory));
        when(notificationHistoryPort.save(any(NotificationHistory.class)))
                .thenReturn(notificationHistory);

        // when
        notificationService.markAsRead(100L, 1L);

        // then
        verify(notificationHistoryPort).findById(100L);
        verify(notificationHistoryPort).save(any(NotificationHistory.class));
    }

    @Test
    @DisplayName("알림 읽음 처리 - 알림 없음")
    void markAsRead_NotFound() {
        // given
        when(notificationHistoryPort.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationService.markAsRead(999L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("알림을 찾을 수 없습니다.");
        
        verify(notificationHistoryPort).findById(999L);
        verify(notificationHistoryPort, never()).save(any());
    }

    @Test
    @DisplayName("알림 읽음 처리 - 다른 사용자의 알림")
    void markAsRead_WrongUser() {
        // given
        when(notificationHistoryPort.findById(100L))
                .thenReturn(Optional.of(notificationHistory));

        // when & then
        assertThatThrownBy(() -> notificationService.markAsRead(100L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 사용자의 알림이 아닙니다.");
        
        verify(notificationHistoryPort).findById(100L);
        verify(notificationHistoryPort, never()).save(any());
    }

    @Test
    @DisplayName("모든 알림 읽음 처리 - 성공")
    void markAllAsRead_Success() {
        // given & when
        notificationService.markAllAsRead(1L);

        // then
        verify(notificationHistoryPort).markAllAsReadByUserId(1L);
    }

    @Test
    @DisplayName("읽지 않은 알림 개수 조회")
    void getUnreadCount() {
        // given
        when(notificationHistoryPort.countUnreadByUserId(1L))
                .thenReturn(5L);

        // when
        long count = notificationService.getUnreadCount(1L);

        // then
        assertThat(count).isEqualTo(5L);
        verify(notificationHistoryPort).countUnreadByUserId(1L);
    }

    @Test
    @DisplayName("알림 생성 및 전송 - 푸시 알림 전송")
    void createNotification_WithPushNotification() {
        // given
        when(notificationHistoryPort.save(any(NotificationHistory.class)))
                .thenReturn(notificationHistory);

        // when
        NotificationHistoryResult result = notificationService.createNotification(createCommand);

        // then
        assertThat(result).isNotNull();
        verify(notificationHistoryPort).save(any(NotificationHistory.class));
    }

    @Test
    @DisplayName("알림 생성 - 빈 설정으로 기본 설정 사용")
    void createNotification_WithDefaultSettings() {
        // given
        when(notificationHistoryPort.save(any(NotificationHistory.class)))
                .thenReturn(notificationHistory);

        // when
        NotificationHistoryResult result = notificationService.createNotification(createCommand);

        // then
        assertThat(result).isNotNull();
        verify(notificationHistoryPort).save(any(NotificationHistory.class));
    }

    // 헬퍼 메서드
    private void setNotificationId(NotificationHistory history, Long id) {
        try {
            java.lang.reflect.Field idField = NotificationHistory.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(history, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set notification ID", e);
        }
    }
}