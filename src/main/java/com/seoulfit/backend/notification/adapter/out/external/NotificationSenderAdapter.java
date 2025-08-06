package com.seoulfit.backend.notification.adapter.out.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoulfit.backend.notification.application.port.out.NotificationSenderPort;
import com.seoulfit.backend.notification.domain.NotificationHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 알림 발송 어댑터
 * <p>
 * 헥사고날 아키텍처의 출력 어댑터
 * 다양한 채널을 통한 알림 발송을 담당
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSenderAdapter implements NotificationSenderPort {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${urbanping.notification.fcm.server-key:}")
    private String fcmServerKey;
    
    @Value("${urbanping.notification.fcm.url:https://fcm.googleapis.com/fcm/send}")
    private String fcmUrl;
    
    @Value("${urbanping.notification.webhook.timeout:10}")
    private int webhookTimeoutSeconds;
    
    @Value("${urbanping.notification.email.enabled:false}")
    private boolean emailEnabled;
    
    @Value("${urbanping.notification.sms.enabled:false}")
    private boolean smsEnabled;

    // 발송 상태 캐시 (실제 구현에서는 Redis 등을 사용)
    private final Map<Long, String> deliveryStatusCache = new ConcurrentHashMap<>();

    @Override
    public boolean sendPushNotification(NotificationHistory notification, String deviceToken) {
        if (deviceToken == null || deviceToken.trim().isEmpty()) {
            log.warn("디바이스 토큰이 없어 푸시 알림을 발송할 수 없습니다: notificationId={}", 
                    notification.getId());
            return false;
        }

        if (fcmServerKey.isEmpty()) {
            log.warn("FCM 서버 키가 설정되지 않아 푸시 알림을 발송할 수 없습니다: notificationId={}", 
                    notification.getId());
            return false;
        }

        try {
            Map<String, Object> fcmPayload = buildFcmPayload(notification, deviceToken);
            
            String response = webClient.post()
                    .uri(fcmUrl)
                    .header(HttpHeaders.AUTHORIZATION, "key=" + fcmServerKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(fcmPayload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            log.info("푸시 알림 발송 성공: notificationId={}, deviceToken={}, response={}", 
                    notification.getId(), deviceToken.substring(0, 10) + "...", response);
            
            deliveryStatusCache.put(notification.getId(), "SENT");
            return true;

        } catch (Exception e) {
            log.error("푸시 알림 발송 실패: notificationId={}, deviceToken={}", 
                    notification.getId(), deviceToken.substring(0, 10) + "...", e);
            deliveryStatusCache.put(notification.getId(), "FAILED");
            return false;
        }
    }

    @Override
    public boolean sendWebhook(NotificationHistory notification, String webhookUrl) {
        if (webhookUrl == null || webhookUrl.trim().isEmpty()) {
            log.warn("웹훅 URL이 없어 웹훅을 발송할 수 없습니다: notificationId={}", 
                    notification.getId());
            return false;
        }

        try {
            Map<String, Object> webhookPayload = buildWebhookPayload(notification);
            
            String response = webClient.post()
                    .uri(webhookUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(webhookPayload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(webhookTimeoutSeconds))
                    .block();

            log.info("웹훅 발송 성공: notificationId={}, webhookUrl={}, response={}", 
                    notification.getId(), webhookUrl, response);
            
            deliveryStatusCache.put(notification.getId(), "SENT");
            return true;

        } catch (Exception e) {
            log.error("웹훅 발송 실패: notificationId={}, webhookUrl={}", 
                    notification.getId(), webhookUrl, e);
            deliveryStatusCache.put(notification.getId(), "FAILED");
            return false;
        }
    }

    @Override
    public boolean sendEmailNotification(NotificationHistory notification, String email) {
        if (!emailEnabled) {
            log.info("이메일 알림이 비활성화되어 있습니다: notificationId={}", notification.getId());
            return false;
        }

        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            log.warn("유효하지 않은 이메일 주소: notificationId={}, email={}", 
                    notification.getId(), email);
            return false;
        }

        try {
            // 이메일 발송 로직 구현 (예: JavaMailSender 사용)
            log.info("이메일 알림 발송 (구현 필요): notificationId={}, email={}, title={}", 
                    notification.getId(), email, notification.getTitle());
            
            // TODO: 실제 이메일 발송 구현
            deliveryStatusCache.put(notification.getId(), "SENT");
            return true;

        } catch (Exception e) {
            log.error("이메일 알림 발송 실패: notificationId={}, email={}", 
                    notification.getId(), email, e);
            deliveryStatusCache.put(notification.getId(), "FAILED");
            return false;
        }
    }

    @Override
    public boolean sendSmsNotification(NotificationHistory notification, String phoneNumber) {
        if (!smsEnabled) {
            log.info("SMS 알림이 비활성화되어 있습니다: notificationId={}", notification.getId());
            return false;
        }

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            log.warn("전화번호가 없어 SMS를 발송할 수 없습니다: notificationId={}", 
                    notification.getId());
            return false;
        }

        try {
            // SMS 발송 로직 구현 (예: AWS SNS, Twilio 등 사용)
            log.info("SMS 알림 발송 (구현 필요): notificationId={}, phoneNumber={}, message={}", 
                    notification.getId(), phoneNumber, notification.getMessage());
            
            // TODO: 실제 SMS 발송 구현
            deliveryStatusCache.put(notification.getId(), "SENT");
            return true;

        } catch (Exception e) {
            log.error("SMS 알림 발송 실패: notificationId={}, phoneNumber={}", 
                    notification.getId(), phoneNumber, e);
            deliveryStatusCache.put(notification.getId(), "FAILED");
            return false;
        }
    }

    @Override
    public String getDeliveryStatus(Long notificationId) {
        return deliveryStatusCache.getOrDefault(notificationId, "UNKNOWN");
    }

    /**
     * FCM 페이로드를 생성합니다.
     * 
     * @param notification 알림 정보
     * @param deviceToken 디바이스 토큰
     * @return FCM 페이로드
     */
    private Map<String, Object> buildFcmPayload(NotificationHistory notification, String deviceToken) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("to", deviceToken);

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", notification.getTitle());
        notificationData.put("body", notification.getMessage());
        notificationData.put("icon", "ic_notification");
        notificationData.put("sound", "default");

        Map<String, Object> data = new HashMap<>();
        data.put("notificationId", notification.getId().toString());
        data.put("notificationType", notification.getNotificationType().name());
        data.put("triggerCondition", notification.getTriggerCondition().name());
        if (notification.getLocationInfo() != null) {
            data.put("locationInfo", notification.getLocationInfo());
        }

        payload.put("notification", notificationData);
        payload.put("data", data);

        return payload;
    }

    /**
     * 웹훅 페이로드를 생성합니다.
     * 
     * @param notification 알림 정보
     * @return 웹훅 페이로드
     */
    private Map<String, Object> buildWebhookPayload(NotificationHistory notification) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", notification.getId());
        payload.put("userId", notification.getUserId());
        payload.put("type", notification.getNotificationType().name());
        payload.put("title", notification.getTitle());
        payload.put("message", notification.getMessage());
        payload.put("triggerCondition", notification.getTriggerCondition().name());
        payload.put("locationInfo", notification.getLocationInfo());
        payload.put("sentAt", notification.getSentAt());
        payload.put("timestamp", System.currentTimeMillis());

        return payload;
    }
}