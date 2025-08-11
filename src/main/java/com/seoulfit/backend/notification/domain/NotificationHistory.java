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
 * 알림 히스토리 도메인 모델을 나타내는 엔티티 클래스입니다.
 * 
 * <p>사용자에게 발송된 알림의 이력을 관리하는 Aggregate Root로서,
 * 알림의 생성, 발송, 읽음 처리 등의 비즈니스 로직을 포함합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>알림 발송 이력 관리</li>
 *   <li>알림 읽음/읽지 않음 상태 관리</li>
 *   <li>알림 타입별 우선순위 관리</li>
 *   <li>트리거 조건과 위치 정보 저장</li>
 *   <li>알림 상태 변경 (발송, 읽음, 실패, 만료)</li>
 * </ul>
 * 
 * <p>이 클래스는 JPA 엔티티로서 데이터베이스의 notification_histories 테이블과 매핑되며,
 * JPA Auditing을 통해 발송 시간이 자동으로 관리됩니다.</p>
 * 
 * @author Seoul Fit
 * @version 1.0.0
 * @since 2025-01-01
 * @see NotificationType
 * @see NotificationStatus
 * @see TriggerCondition
 * @see User
 */
@Entity
@Table(name = "notification_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class NotificationHistory {

    /**
     * 알림 히스토리의 고유 식별자입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 알림을 받은 사용자의 ID입니다.
     * User 도메인과 연결되어 있습니다.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 알림의 타입입니다.
     * 날씨, 따릉이, 문화행사 등의 카테고리를 나타냅니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    /**
     * 알림의 제목입니다.
     * 최대 200자까지 저장할 수 있습니다.
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 알림의 본문 메시지입니다.
     * 최대 1000자까지 저장할 수 있습니다.
     */
    @Column(nullable = false, length = 1000)
    private String message;

    /**
     * 알림을 발생시킨 트리거 조건입니다.
     * 온도, 대기질, 따릉이 부족 등의 조건을 나타냅니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_condition", nullable = false)
    private TriggerCondition triggerCondition;

    /**
     * 알림과 관련된 위치 정보입니다.
     * 선택적으로 저장되며, 최대 500자까지 저장할 수 있습니다.
     */
    @Column(name = "location_info", length = 500)
    private String locationInfo;

    /**
     * 알림의 현재 상태입니다.
     * 기본값은 SENT(발송됨)입니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.SENT;

    /**
     * 알림을 읽은 시간입니다.
     * 읽지 않은 알림의 경우 null입니다.
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * 알림이 발송된 시간입니다.
     * JPA Auditing에 의해 자동으로 설정됩니다.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;

    /**
     * NotificationHistory 엔티티의 빌더 생성자입니다.
     * 
     * @param userId 사용자 ID
     * @param notificationType 알림 타입
     * @param title 알림 제목
     * @param message 알림 메시지
     * @param triggerCondition 트리거 조건
     * @param locationInfo 위치 정보
     */
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
     * 새로운 알림 히스토리를 생성합니다.
     * 
     * <p>정적 팩토리 메서드로서, 알림 히스토리 생성 시 필요한 모든 정보를 받아
     * 새로운 인스턴스를 생성합니다.</p>
     *
     * @param userId 알림을 받을 사용자 ID
     * @param notificationType 알림 타입
     * @param title 알림 제목
     * @param message 알림 메시지
     * @param triggerCondition 알림을 발생시킨 트리거 조건
     * @param locationInfo 관련 위치 정보 (선택사항)
     * @return 생성된 알림 히스토리 인스턴스
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
     * 알림을 읽음 상태로 변경합니다.
     * 
     * <p>이미 읽은 알림의 경우 중복 처리되지 않습니다.
     * 읽음 처리 시 현재 시간이 readAt에 설정되고 상태가 READ로 변경됩니다.</p>
     */
    public void markAsRead() {
        if (this.readAt == null) {
            this.readAt = LocalDateTime.now();
            this.status = NotificationStatus.READ;
        }
    }

    /**
     * 알림이 읽힌 상태인지 확인합니다.
     *
     * @return 읽힌 상태 여부 (true: 읽음, false: 읽지 않음)
     */
    public boolean isRead() {
        return this.readAt != null;
    }

    /**
     * 알림이 읽히지 않은 상태인지 확인합니다.
     *
     * @return 읽지 않은 상태 여부 (true: 읽지 않음, false: 읽음)
     */
    public boolean isUnread() {
        return this.readAt == null;
    }

    /**
     * 알림 발송을 실패 상태로 변경합니다.
     * 
     * <p>네트워크 오류나 기타 이유로 알림 발송이 실패한 경우 호출됩니다.</p>
     */
    public void markAsFailed() {
        this.status = NotificationStatus.FAILED;
    }

    /**
     * 알림을 만료 상태로 변경합니다.
     * 
     * <p>일정 시간이 지나 더 이상 유효하지 않은 알림의 경우 호출됩니다.</p>
     */
    public void markAsExpired() {
        this.status = NotificationStatus.EXPIRED;
    }

    /**
     * 특정 사용자의 알림인지 확인합니다.
     *
     * @param userId 확인할 사용자 ID
     * @return 해당 사용자의 알림 여부 (true: 해당 사용자, false: 다른 사용자)
     */
    public boolean belongsTo(Long userId) {
        return this.userId.equals(userId);
    }

    /**
     * 특정 알림 타입인지 확인합니다.
     *
     * @param type 확인할 알림 타입
     * @return 해당 타입 여부 (true: 일치, false: 불일치)
     */
    public boolean isType(NotificationType type) {
        return this.notificationType == type;
    }

    /**
     * 특정 트리거 조건에 의해 발생한 알림인지 확인합니다.
     *
     * @param condition 확인할 트리거 조건
     * @return 해당 조건 여부 (true: 일치, false: 불일치)
     */
    public boolean isTriggeredBy(TriggerCondition condition) {
        return this.triggerCondition == condition;
    }

    /**
     * 알림의 우선순위를 반환합니다.
     * 
     * <p>알림 타입에 따라 결정되며, 낮은 값일수록 높은 우선순위를 가집니다.</p>
     *
     * @return 우선순위 값 (낮을수록 높은 우선순위)
     */
    public int getPriority() {
        return this.notificationType.getPriority();
    }

    /**
     * 긴급 알림인지 확인합니다.
     * 
     * <p>알림 타입에 따라 결정되며, 긴급 알림은 즉시 처리되어야 합니다.</p>
     *
     * @return 긴급 알림 여부 (true: 긴급, false: 일반)
     */
    public boolean isUrgent() {
        return this.notificationType.isUrgent();
    }
}
