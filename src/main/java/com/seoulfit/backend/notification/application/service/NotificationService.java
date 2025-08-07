package com.seoulfit.backend.notification.application.service;

import com.seoulfit.backend.notification.application.port.in.ManageNotificationUseCase;
import com.seoulfit.backend.notification.application.port.in.dto.CreateNotificationCommand;
import com.seoulfit.backend.notification.application.port.in.dto.NotificationHistoryQuery;
import com.seoulfit.backend.notification.application.port.in.dto.NotificationHistoryResult;
import com.seoulfit.backend.notification.application.port.out.NotificationHistoryPort;
import com.seoulfit.backend.notification.application.port.out.NotificationSenderPort;
import com.seoulfit.backend.notification.domain.NotificationHistory;
import com.seoulfit.backend.user.domain.NotificationSetting;
import com.seoulfit.backend.user.infrastructure.NotificationSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 알림 서비스
 * <p>
 * 헥사고날 아키텍처의 애플리케이션 서비스 알림 생성, 조회, 읽음 처리 등의 비즈니스 로직을 처리
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService implements ManageNotificationUseCase {

    private final NotificationHistoryPort notificationHistoryPort;
    private final NotificationSenderPort notificationSenderPort;
    private final NotificationSettingRepository notificationSettingRepository;

    @Override
    @Transactional
    public NotificationHistoryResult createNotification(CreateNotificationCommand command) {
        NotificationHistory notificationHistory = NotificationHistory.create(
                command.userId(),
                command.getNotificationType(),
                command.title(),
                command.message(),
                command.triggerCondition(),
                command.locationInfo()
        );

        NotificationHistory savedNotification = notificationHistoryPort.save(notificationHistory);
        log.info("알림 생성 완료: userId={}, type={}, title={}",
                command.userId(), command.getNotificationType(), command.title());

        // 실제 알림 발송
//        sendNotificationAsync(savedNotification, command.priority() != null ? command.priority() : 50);

        return NotificationHistoryResult.from(savedNotification);
    }
    
    /**
     * 알림을 비동기적으로 발송합니다.
     * 
     * @param notification 알림 정보
     * @param priority 우선순위
     */
    private void sendNotificationAsync(NotificationHistory notification, int priority) {
        // 비동기 처리를 위해 별도 스레드에서 실행
        // 실제 프로덕션에서는 @Async나 메시지 큐를 사용
        new Thread(() -> {
            try {
                sendNotification(notification, priority);
            } catch (Exception e) {
                log.error("알림 발송 중 오류 발생: notificationId={}", notification.getId(), e);
            }
        }).start();
    }
    
    /**
     * 사용자 설정에 따라 알림을 발송합니다.
     * 
     * @param notification 알림 정보
     * @param priority 우선순위
     */
    private void sendNotification(NotificationHistory notification, int priority) {
        List<NotificationSetting> settings = notificationSettingRepository
                .findByUserIdAndNotificationTypeAndIsActive(
                        notification.getUserId(), 
                        notification.getNotificationType(), 
                        true
                );
        
        if (settings.isEmpty()) {
            // 기본 설정 확인
            List<NotificationSetting> defaultSettings = notificationSettingRepository
                    .findByUserIdAndNotificationTypeIsNullAndIsActive(notification.getUserId(), true);
            settings = defaultSettings;
        }
        
        for (NotificationSetting setting : settings) {
            if (!setting.canReceiveNotification(priority)) {
                log.debug("알림 수신 조건 미충족: settingId={}, priority={}", setting.getId(), priority);
                continue;
            }
            
            // 푸시 알림 발송
            if (setting.getPushEnabled() && setting.getDeviceToken() != null) {
                boolean success = notificationSenderPort.sendPushNotification(
                        notification, setting.getDeviceToken());
                log.info("푸시 알림 발송 {}: notificationId={}, userId={}", 
                        success ? "성공" : "실패", notification.getId(), notification.getUserId());
            }
            
            // 웹훅 발송
            if (setting.getWebhookEnabled() && setting.getWebhookUrl() != null) {
                boolean success = notificationSenderPort.sendWebhook(
                        notification, setting.getWebhookUrl());
                log.info("웹훅 발송 {}: notificationId={}, userId={}", 
                        success ? "성공" : "실패", notification.getId(), notification.getUserId());
            }
            
            // 이메일 발송
            if (setting.getEmailEnabled() && setting.getEmail() != null) {
                boolean success = notificationSenderPort.sendEmailNotification(
                        notification, setting.getEmail());
                log.info("이메일 발송 {}: notificationId={}, userId={}", 
                        success ? "성공" : "실패", notification.getId(), notification.getUserId());
            }
            
            // SMS 발송
            if (setting.getSmsEnabled() && setting.getPhoneNumber() != null) {
                boolean success = notificationSenderPort.sendSmsNotification(
                        notification, setting.getPhoneNumber());
                log.info("SMS 발송 {}: notificationId={}, userId={}", 
                        success ? "성공" : "실패", notification.getId(), notification.getUserId());
            }
        }
    }

    @Override
    public Page<NotificationHistoryResult> getNotificationHistory(NotificationHistoryQuery query) {
        Page<NotificationHistory> historyPage;

        if (query.getNotificationType() != null) {
            historyPage = notificationHistoryPort.findByUserIdAndType(
                    query.userId(), query.getNotificationType(), query.pageable());
        } else if (query.startDate() != null && query.endDate() != null) {
            historyPage = notificationHistoryPort.findByUserIdAndDateRange(
                    query.userId(), query.startDate(), query.endDate(), query.pageable());
        } else {
            historyPage = notificationHistoryPort.findByUserId(query.userId(), query.pageable());
        }

        return historyPage.map(NotificationHistoryResult::from);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        NotificationHistory notification = notificationHistoryPort.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));

        if (!notification.belongsTo(userId)) {
            throw new IllegalArgumentException("해당 사용자의 알림이 아닙니다.");
        }

        notification.markAsRead();
        notificationHistoryPort.save(notification);

        log.info("알림 읽음 처리 완료: notificationId={}, userId={}", notificationId, userId);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationHistoryPort.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationHistoryPort.markAllAsReadByUserId(userId);
        log.info("모든 알림 읽음 처리 완료: userId={}", userId);
    }
}
