package com.seoulfit.backend.user.infrastructure;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import com.seoulfit.backend.user.domain.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 알림 설정 Repository
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    
    /**
     * 사용자별 알림 설정 조회
     * 
     * @param userId 사용자 ID
     * @return 알림 설정 목록
     */
    List<NotificationSetting> findByUserId(Long userId);
    
    /**
     * 사용자별 활성화된 알림 설정 조회
     * 
     * @param userId 사용자 ID
     * @param isActive 활성화 여부
     * @return 알림 설정 목록
     */
    List<NotificationSetting> findByUserIdAndIsActive(Long userId, Boolean isActive);
    
    /**
     * 사용자별 알림 타입별 설정 조회
     * 
     * @param userId 사용자 ID
     * @param notificationType 알림 타입
     * @param isActive 활성화 여부
     * @return 알림 설정 목록
     */
    List<NotificationSetting> findByUserIdAndNotificationTypeAndIsActive(
            Long userId, NotificationType notificationType, Boolean isActive);
    
    /**
     * 사용자별 트리거 조건별 설정 조회
     * 
     * @param userId 사용자 ID
     * @param triggerCondition 트리거 조건
     * @param isActive 활성화 여부
     * @return 알림 설정 목록
     */
    List<NotificationSetting> findByUserIdAndTriggerConditionAndIsActive(
            Long userId, TriggerCondition triggerCondition, Boolean isActive);
    
    /**
     * 사용자의 기본 설정 조회 (알림 타입이 null인 설정)
     * 
     * @param userId 사용자 ID
     * @param isActive 활성화 여부
     * @return 기본 알림 설정 목록
     */
    @Query("SELECT ns FROM NotificationSetting ns WHERE ns.userId = :userId " +
           "AND ns.notificationType IS NULL AND ns.isActive = :isActive")
    List<NotificationSetting> findByUserIdAndNotificationTypeIsNullAndIsActive(
            @Param("userId") Long userId, @Param("isActive") Boolean isActive);
    
    /**
     * 특정 설정 조회 (중복 방지용)
     * 
     * @param userId 사용자 ID
     * @param notificationType 알림 타입
     * @param triggerCondition 트리거 조건
     * @return 알림 설정
     */
    Optional<NotificationSetting> findByUserIdAndNotificationTypeAndTriggerCondition(
            Long userId, NotificationType notificationType, TriggerCondition triggerCondition);
    
    /**
     * 푸시 알림이 활성화된 사용자들의 디바이스 토큰 조회
     * 
     * @param notificationType 알림 타입
     * @return 디바이스 토큰 목록
     */
    @Query("SELECT DISTINCT ns.deviceToken FROM NotificationSetting ns " +
           "WHERE ns.pushEnabled = true AND ns.isActive = true " +
           "AND ns.deviceToken IS NOT NULL " +
           "AND (ns.notificationType = :notificationType OR ns.notificationType IS NULL)")
    List<String> findActiveDeviceTokensByNotificationType(@Param("notificationType") NotificationType notificationType);
    
    /**
     * 웹훅이 활성화된 사용자들의 웹훅 URL 조회
     * 
     * @param notificationType 알림 타입
     * @return 웹훅 URL 목록
     */
    @Query("SELECT DISTINCT ns.webhookUrl FROM NotificationSetting ns " +
           "WHERE ns.webhookEnabled = true AND ns.isActive = true " +
           "AND ns.webhookUrl IS NOT NULL " +
           "AND (ns.notificationType = :notificationType OR ns.notificationType IS NULL)")
    List<String> findActiveWebhookUrlsByNotificationType(@Param("notificationType") NotificationType notificationType);
    
    /**
     * 사용자의 디바이스 토큰 업데이트
     * 
     * @param userId 사용자 ID
     * @param deviceToken 디바이스 토큰
     */
    @Query("UPDATE NotificationSetting ns SET ns.deviceToken = :deviceToken, ns.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE ns.userId = :userId AND ns.pushEnabled = true")
    void updateDeviceTokenByUserId(@Param("userId") Long userId, @Param("deviceToken") String deviceToken);
}