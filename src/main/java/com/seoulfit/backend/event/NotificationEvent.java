package com.seoulfit.backend.event;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 알림 이벤트 클래스입니다.
 * 
 * <p>Spring의 ApplicationEvent를 확장하여 알림 시스템에서 사용되는 이벤트를 정의합니다.
 * Observer Pattern을 통해 느슨한 결합을 구현하여 알림 발송과 비즈니스 로직을 분리합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>트리거 조건에 따른 알림 이벤트 생성</li>
 *   <li>사용자별 맞춤 알림 정보 전달</li>
 *   <li>우선순위 기반 알림 처리</li>
 *   <li>위치 정보를 포함한 상세 알림</li>
 * </ul>
 * 
 * <p>이 이벤트는 트리거 시스템에서 발행되어 알림 처리 시스템으로 전달됩니다.
 * 이벤트 기반 아키텍처를 통해 시스템 간의 결합도를 낮추고 확장성을 높입니다.</p>
 * 
 * @author Seoul Fit
 * @version 1.0.0
 * @since 2025-01-01
 * @see ApplicationEvent
 * @see NotificationType
 * @see TriggerCondition
 */
@Getter
public class NotificationEvent extends ApplicationEvent {
    
    /**
     * 알림을 받을 사용자의 ID입니다.
     */
    private final Long userId;
    
    /**
     * 알림의 타입입니다.
     * 날씨, 따릉이, 문화행사 등의 카테고리를 나타냅니다.
     */
    private final NotificationType type;
    
    /**
     * 알림을 발생시킨 트리거 조건입니다.
     */
    private final TriggerCondition triggerCondition;
    
    /**
     * 알림의 제목입니다.
     */
    private final String title;
    
    /**
     * 알림의 본문 메시지입니다.
     */
    private final String message;
    
    /**
     * 알림과 관련된 위치 정보입니다.
     */
    private final String locationInfo;
    
    /**
     * 알림의 우선순위입니다.
     * 낮은 값일수록 높은 우선순위를 가집니다.
     */
    private final int priority;
    
    /**
     * NotificationEvent의 빌더 생성자입니다.
     * 
     * @param source 이벤트를 발생시킨 소스 객체
     * @param userId 알림을 받을 사용자 ID
     * @param type 알림 타입
     * @param triggerCondition 트리거 조건
     * @param title 알림 제목
     * @param message 알림 메시지
     * @param locationInfo 위치 정보
     * @param priority 우선순위
     */
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
     * 기본 우선순위로 알림 이벤트를 생성합니다.
     * 
     * <p>정적 팩토리 메서드로서, 기본 우선순위(50)를 사용하여
     * 알림 이벤트를 간편하게 생성할 수 있습니다.</p>
     * 
     * @param source 이벤트를 발생시킨 소스 객체
     * @param userId 알림을 받을 사용자 ID
     * @param type 알림 타입
     * @param triggerCondition 트리거 조건
     * @param title 알림 제목
     * @param message 알림 메시지
     * @param locationInfo 위치 정보
     * @return 생성된 알림 이벤트 인스턴스
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
