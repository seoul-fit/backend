package com.seoulfit.backend.event;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 알림 이벤트
 * 
 * Spring의 ApplicationEvent를 확장한 알림 이벤트
 * Observer Pattern을 통한 느슨한 결합 구현
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Getter
public class NotificationEvent extends ApplicationEvent {
    
    private final Long userId;
    private final NotificationType type;
    private final TriggerCondition triggerCondition;
    private final String title;
    private final String message;
    private final String locationInfo;
    private final int priority;
    
    @Builder
    public NotificationEvent(Object source, Long userId, NotificationType type,
                           TriggerCondition triggerCondition, String title, String message,
                           String locationInfo, int priority) {
        super(source != null ? source : "system");
        this.userId = userId;
        this.type = type;
        this.triggerCondition = triggerCondition;
        this.title = title;
        this.message = message;
        this.locationInfo = locationInfo;
        this.priority = priority;
    }
    
    /**
     * 알림 이벤트 생성
     * 
     * @param source 이벤트 소스
     * @param userId 사용자 ID
     * @param type 알림 타입
     * @param triggerCondition 트리거 조건
     * @param title 제목
     * @param message 메시지
     * @param locationInfo 위치 정보
     * @return 알림 이벤트
     */
    public static NotificationEvent of(Object source, Long userId, NotificationType type,
                                     TriggerCondition triggerCondition, String title, String message,
                                     String locationInfo) {
        return NotificationEvent.builder()
                .source(source)
                .userId(userId)
                .type(type)
                .triggerCondition(triggerCondition)
                .title(title)
                .message(message)
                .locationInfo(locationInfo)
                .priority(50) // 기본 우선순위
                .build();
    }
}
