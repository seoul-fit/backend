package com.seoulfit.backend.user.domain;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 알림 설정 엔티티
 * <p>
 * 사용자별 알림 채널, 알림 타입, 트리거 조건별 설정을 관리
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Entity
@Table(name = "notification_settings",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "notification_type", "trigger_condition"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSetting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_condition")
    private TriggerCondition triggerCondition;
    
    @Column(name = "push_enabled", nullable = false)
    private Boolean pushEnabled = true;
    
    @Column(name = "webhook_enabled", nullable = false)
    private Boolean webhookEnabled = false;
    
    @Column(name = "email_enabled", nullable = false)
    private Boolean emailEnabled = false;
    
    @Column(name = "sms_enabled", nullable = false)
    private Boolean smsEnabled = false;
    
    @Column(name = "device_token")
    private String deviceToken;
    
    @Column(name = "webhook_url")
    private String webhookUrl;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "priority_threshold")
    private Integer priorityThreshold = 50; // 이 값보다 낮은 우선순위(더 중요한) 알림만 발송
    
    @Column(name = "quiet_hours_start")
    private String quietHoursStart; // 방해 금지 시작 시간 (HH:mm)
    
    @Column(name = "quiet_hours_end")
    private String quietHoursEnd; // 방해 금지 종료 시간 (HH:mm)
    
    @Column(name = "location_radius")
    private Double locationRadius = 5000.0; // 알림 받을 위치 반경 (미터)
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 알림 설정 생성
     * 
     * @param userId 사용자 ID
     * @param notificationType 알림 타입
     * @param triggerCondition 트리거 조건
     * @return 알림 설정
     */
    public static NotificationSetting create(Long userId, NotificationType notificationType, 
                                           TriggerCondition triggerCondition) {
        NotificationSetting setting = new NotificationSetting();
        setting.userId = userId;
        setting.notificationType = notificationType;
        setting.triggerCondition = triggerCondition;
        setting.createdAt = LocalDateTime.now();
        setting.updatedAt = LocalDateTime.now();
        return setting;
    }
    
    /**
     * 기본 알림 설정 생성 (모든 타입 활성화)
     * 
     * @param userId 사용자 ID
     * @return 기본 알림 설정
     */
    public static NotificationSetting createDefault(Long userId) {
        NotificationSetting setting = new NotificationSetting();
        setting.userId = userId;
        setting.notificationType = null; // 모든 타입에 대한 기본 설정
        setting.pushEnabled = true;
        setting.webhookEnabled = false;
        setting.emailEnabled = false;
        setting.smsEnabled = false;
        setting.createdAt = LocalDateTime.now();
        setting.updatedAt = LocalDateTime.now();
        return setting;
    }
    
    /**
     * 푸시 알림 설정 업데이트
     * 
     * @param enabled 활성화 여부
     * @param deviceToken 디바이스 토큰
     */
    public void updatePushSetting(boolean enabled, String deviceToken) {
        this.pushEnabled = enabled;
        this.deviceToken = deviceToken;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 웹훅 설정 업데이트
     * 
     * @param enabled 활성화 여부
     * @param webhookUrl 웹훅 URL
     */
    public void updateWebhookSetting(boolean enabled, String webhookUrl) {
        this.webhookEnabled = enabled;
        this.webhookUrl = webhookUrl;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 이메일 설정 업데이트
     * 
     * @param enabled 활성화 여부
     * @param email 이메일 주소
     */
    public void updateEmailSetting(boolean enabled, String email) {
        this.emailEnabled = enabled;
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * SMS 설정 업데이트
     * 
     * @param enabled 활성화 여부
     * @param phoneNumber 전화번호
     */
    public void updateSmsSetting(boolean enabled, String phoneNumber) {
        this.smsEnabled = enabled;
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 우선순위 임계값 설정
     * 
     * @param priorityThreshold 우선순위 임계값
     */
    public void setPriorityThreshold(int priorityThreshold) {
        this.priorityThreshold = priorityThreshold;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 방해 금지 시간 설정
     * 
     * @param startTime 시작 시간 (HH:mm)
     * @param endTime 종료 시간 (HH:mm)
     */
    public void setQuietHours(String startTime, String endTime) {
        this.quietHoursStart = startTime;
        this.quietHoursEnd = endTime;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 위치 반경 설정
     * 
     * @param locationRadius 위치 반경 (미터)
     */
    public void setLocationRadius(double locationRadius) {
        this.locationRadius = locationRadius;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 설정 활성화/비활성화
     * 
     * @param active 활성화 여부
     */
    public void setActive(boolean active) {
        this.isActive = active;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 현재 시간이 방해 금지 시간인지 확인
     * 
     * @return 방해 금지 시간 여부
     */
    public boolean isInQuietHours() {
        if (quietHoursStart == null || quietHoursEnd == null) {
            return false;
        }
        
        try {
            LocalDateTime now = LocalDateTime.now();
            int currentHour = now.getHour();
            int currentMinute = now.getMinute();
            int currentTime = currentHour * 100 + currentMinute;
            
            String[] startParts = quietHoursStart.split(":");
            int startTime = Integer.parseInt(startParts[0]) * 100 + Integer.parseInt(startParts[1]);
            
            String[] endParts = quietHoursEnd.split(":");
            int endTime = Integer.parseInt(endParts[0]) * 100 + Integer.parseInt(endParts[1]);
            
            if (startTime <= endTime) {
                return currentTime >= startTime && currentTime <= endTime;
            } else {
                // 자정을 넘어가는 경우 (예: 23:00 ~ 07:00)
                return currentTime >= startTime || currentTime <= endTime;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 알림을 받을 수 있는지 확인
     * 
     * @param priority 알림 우선순위
     * @return 알림 수신 가능 여부
     */
    public boolean canReceiveNotification(int priority) {
        if (!isActive) {
            return false;
        }
        
        // 우선순위가 임계값보다 높은(중요한) 경우에만 받음
        if (priority > priorityThreshold) {
            return false;
        }
        
        // 긴급 알림은 방해 금지 시간에도 받음
        if (priority <= 10) {
            return true;
        }
        
        // 일반 알림은 방해 금지 시간에 받지 않음
        return !isInQuietHours();
    }
    
    /**
     * 사용자 소유인지 확인
     * 
     * @param userId 사용자 ID
     * @return 소유 여부
     */
    public boolean belongsTo(Long userId) {
        return this.userId.equals(userId);
    }
}