package com.seoulfit.backend.notification.application.port.out;

import com.seoulfit.backend.notification.domain.NotificationHistory;

/**
 * 알림 발송 포트
 * <p>
 * 헥사고날 아키텍처의 출력 포트
 * 실제 알림 발송 (푸시, 웹훅, 이메일 등)을 담당
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
public interface NotificationSenderPort {
    
    /**
     * 푸시 알림을 발송합니다.
     * 
     * @param notification 알림 정보
     * @param deviceToken 디바이스 토큰
     * @return 발송 성공 여부
     */
    boolean sendPushNotification(NotificationHistory notification, String deviceToken);
    
    /**
     * 웹훅을 발송합니다.
     * 
     * @param notification 알림 정보
     * @param webhookUrl 웹훅 URL
     * @return 발송 성공 여부
     */
    boolean sendWebhook(NotificationHistory notification, String webhookUrl);
    
    /**
     * 이메일 알림을 발송합니다.
     * 
     * @param notification 알림 정보
     * @param email 이메일 주소
     * @return 발송 성공 여부
     */
    boolean sendEmailNotification(NotificationHistory notification, String email);
    
    /**
     * SMS 알림을 발송합니다.
     * 
     * @param notification 알림 정보
     * @param phoneNumber 전화번호
     * @return 발송 성공 여부
     */
    boolean sendSmsNotification(NotificationHistory notification, String phoneNumber);
    
    /**
     * 알림 발송 상태를 확인합니다.
     * 
     * @param notificationId 알림 ID
     * @return 발송 상태
     */
    String getDeliveryStatus(Long notificationId);
}