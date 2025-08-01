package com.seoulfit.backend.notification.domain;

import com.seoulfit.backend.trigger.domain.TriggerCondition;
import com.seoulfit.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 알림 히스토리 도메인 모델
 * <p>
 * 사용자에게 발송된 알림의 이력을 관리하는 Aggregate Root
 * 알림 생성, 발송, 읽음 처리 등의 비즈니스 로직을 포함
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Entity
@Table(name = "notification_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class NotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 ID (User 도메인과 연결)
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_condition", nullable = false)
    private TriggerCondition triggerCondition;

    @Column(name = "location_info", length = 500)
    private String locationInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.SENT;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Builder
    public NotificationHistory(Long userId, NotificationType notificationType, String title,
                              String message, TriggerCondition triggerCondition, String locationInfo) {
        this.userId = userId;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.triggerCondition = triggerCondition;
        this.locationInfo = locationInfo;
        this.status = NotificationStatus.SENT;
    }

    /**
     * 알림 히스토리 생성
     *
     * @param userId           사용자 ID
     * @param notificationType 알림 타입
     * @param title            제목
     * @param message          메시지
     * @param triggerCondition 트리거 조건
     * @param locationInfo     위치 정보
     * @return 알림 히스토리
     */
    public static NotificationHistory create(Long userId, NotificationType notificationType,
                                           String title, String message, TriggerCondition triggerCondition,
                                           String locationInfo) {
        return NotificationHistory.builder()
                .userId(userId)
                .notificationType(notificationType)
                .title(title)
                .message(message)
                .triggerCondition(triggerCondition)
                .locationInfo(locationInfo)
                .build();
    }

    /**
     * 알림 읽음 처리
     */
    public void markAsRead() {
        if (this.readAt == null) {
            this.readAt = LocalDateTime.now();
            this.status = NotificationStatus.READ;
        }
    }

    /**
     * 읽음 여부 확인
     *
     * @return 읽음 여부
     */
    public boolean isRead() {
        return this.readAt != null;
    }

    /**
     * 읽지 않음 여부 확인
     *
     * @return 읽지 않음 여부
     */
    public boolean isUnread() {
        return this.readAt == null;
    }

    /**
     * 알림 발송 실패 처리
     */
    public void markAsFailed() {
        this.status = NotificationStatus.FAILED;
    }

    /**
     * 알림 만료 처리
     */
    public void markAsExpired() {
        this.status = NotificationStatus.EXPIRED;
    }

    /**
     * 특정 사용자의 알림인지 확인
     *
     * @param userId 확인할 사용자 ID
     * @return 해당 사용자의 알림 여부
     */
    public boolean belongsTo(Long userId) {
        return this.userId.equals(userId);
    }

    /**
     * 특정 알림 타입인지 확인
     *
     * @param type 확인할 알림 타입
     * @return 해당 타입 여부
     */
    public boolean isType(NotificationType type) {
        return this.notificationType == type;
    }

    /**
     * 특정 트리거 조건인지 확인
     *
     * @param condition 확인할 트리거 조건
     * @return 해당 조건 여부
     */
    public boolean isTriggeredBy(TriggerCondition condition) {
        return this.triggerCondition == condition;
    }

    /**
     * 알림 우선순위 반환
     *
     * @return 우선순위 (낮을수록 높은 우선순위)
     */
    public int getPriority() {
        return this.notificationType.getPriority();
    }

    /**
     * 긴급 알림 여부 확인
     *
     * @return 긴급 알림 여부
     */
    public boolean isUrgent() {
        return this.notificationType.isUrgent();
    }
}
