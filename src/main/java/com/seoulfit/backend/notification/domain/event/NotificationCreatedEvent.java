package com.seoulfit.backend.notification.domain.event;

import com.seoulfit.backend.notification.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 알림 생성 완료 이벤트
 * 
 * 알림이 성공적으로 생성되었을 때 발행되는 도메인 이벤트입니다.
 * 이벤트 기반 아키텍처를 통해 알림 생성과 전송의 책임을 분리합니다.
 * 
 * **이벤트 처리자들:**
 * - NotificationDeliveryService: 실제 알림 전송 처리
 * - NotificationAnalyticsService: 알림 생성 통계 처리  
 * - NotificationAuditService: 감사 로그 기록
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreatedEvent {

    /**
     * 생성된 알림의 고유 ID
     */
    private Long notificationId;

    /**
     * 알림을 받을 사용자 ID
     */
    private Long userId;

    /**
     * 알림 유형
     */
    private NotificationType notificationType;

    /**
     * 알림 제목
     */
    private String title;

    /**
     * 알림 메시지 내용
     */
    private String message;

    /**
     * 트리거 조건 (선택사항)
     */
    private String triggerCondition;

    /**
     * 위치 정보 (선택사항)
     */
    private String locationInfo;

    /**
     * 알림 우선순위 (1-10, 높을수록 우선)
     */
    private Integer priority;

    /**
     * 알림 생성 시간
     */
    private LocalDateTime createdAt;

    /**
     * 예약 발송 시간 (즉시 발송인 경우 null)
     */
    private LocalDateTime scheduledAt;

    /**
     * 편의 메서드: 즉시 발송 알림인지 확인
     * 
     * @return 즉시 발송 여부
     */
    public boolean isImmediateDelivery() {
        return scheduledAt == null || scheduledAt.isBefore(LocalDateTime.now().plusMinutes(1));
    }

    /**
     * 편의 메서드: 고우선순위 알림인지 확인
     * 
     * @return 고우선순위 여부 (우선순위 7 이상)
     */
    public boolean isHighPriority() {
        return priority != null && priority >= 7;
    }

    /**
     * 편의 메서드: 위치 기반 알림인지 확인
     * 
     * @return 위치 정보 포함 여부
     */
    public boolean isLocationBased() {
        return locationInfo != null && !locationInfo.trim().isEmpty();
    }

    /**
     * 편의 메서드: 트리거 기반 알림인지 확인
     * 
     * @return 트리거 조건 포함 여부
     */
    public boolean isTriggerBased() {
        return triggerCondition != null && !triggerCondition.trim().isEmpty();
    }

    /**
     * 이벤트 요약 정보를 문자열로 반환합니다.
     * 로깅 및 디버깅 목적으로 사용됩니다.
     * 
     * @return 이벤트 요약 문자열
     */
    @Override
    public String toString() {
        return String.format(
            "NotificationCreatedEvent{id=%d, userId=%d, type=%s, priority=%d, immediate=%s}", 
            notificationId, userId, notificationType, 
            priority != null ? priority : 0, isImmediateDelivery()
        );
    }
}