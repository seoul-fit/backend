package com.seoulfit.backend.notification.application.port.in.dto;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;

/**
 * 알림 생성 명령
 */
public record CreateNotificationCommand(
        Long userId,
        NotificationType type,
        String title,
        String message,
        String data,
        TriggerCondition triggerCondition,
        String locationInfo,
        Integer priority
) {

    public CreateNotificationCommand {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        if (type == null) {
            throw new IllegalArgumentException("알림 타입은 필수입니다.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("알림 제목은 필수입니다.");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("알림 메시지는 필수입니다.");
        }
    }

    /**
     * 알림 생성 명령 생성
     */
    public static CreateNotificationCommand of(Long userId, NotificationType type, String title, String message,
            TriggerCondition triggerCondition, String locationInfo) {
        return new CreateNotificationCommand(userId, type, title, message, null, triggerCondition, locationInfo, null);
    }
    
    /**
     * 우선순위가 포함된 알림 생성 명령 생성
     */
    public static CreateNotificationCommand of(Long userId, NotificationType type, String title, String message,
            TriggerCondition triggerCondition, String locationInfo, Integer priority) {
        return new CreateNotificationCommand(userId, type, title, message, null, triggerCondition, locationInfo, priority);
    }

    // 기존 코드와의 호환성을 위한 별칭 메서드들
    public NotificationType getNotificationType() {
        return type;
    }
}
