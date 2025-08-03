package com.seoulfit.backend.notification.application.port.in.dto;

import com.seoulfit.backend.notification.domain.NotificationHistory;
import com.seoulfit.backend.notification.domain.NotificationStatus;
import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;

import java.time.LocalDateTime;

/**
 * 알림 히스토리 결과
 */
public record NotificationHistoryResult(
    Long id,
    Long userId,
    NotificationType type,
    String title,
    String message,
    String data,
    TriggerCondition triggerCondition,
    String locationInfo,
    NotificationStatus status,
    LocalDateTime sentAt,
    LocalDateTime readAt
) {
    
    public static NotificationHistoryResult from(NotificationHistory history) {
        return new NotificationHistoryResult(
            history.getId(),
            history.getUserId(),
            history.getNotificationType(),
            history.getTitle(),
            history.getMessage(),
            null, // data 필드는 현재 NotificationHistory에 없음
            history.getTriggerCondition(),
            history.getLocationInfo(),
            history.getStatus(),
            history.getSentAt(),
            history.getReadAt()
        );
    }
    
    // 기존 코드와의 호환성을 위한 별칭 메서드들
    public NotificationType getNotificationType() {
        return type;
    }
    
    public LocalDateTime getCreatedAt() {
        return sentAt;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public boolean isRead() {
        return readAt != null;
    }
}
