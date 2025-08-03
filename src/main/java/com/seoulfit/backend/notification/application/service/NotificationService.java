package com.seoulfit.backend.notification.application.service;

import com.seoulfit.backend.notification.application.port.in.ManageNotificationUseCase;
import com.seoulfit.backend.notification.application.port.in.dto.CreateNotificationCommand;
import com.seoulfit.backend.notification.application.port.in.dto.NotificationHistoryQuery;
import com.seoulfit.backend.notification.application.port.in.dto.NotificationHistoryResult;
import com.seoulfit.backend.notification.application.port.out.NotificationHistoryPort;
import com.seoulfit.backend.notification.domain.NotificationHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return NotificationHistoryResult.from(savedNotification);
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
