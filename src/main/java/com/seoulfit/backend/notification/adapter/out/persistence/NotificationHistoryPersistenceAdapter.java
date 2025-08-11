package com.seoulfit.backend.notification.adapter.out.persistence;

import com.seoulfit.backend.notification.application.port.out.NotificationHistoryPort;
import com.seoulfit.backend.notification.domain.NotificationHistory;
import com.seoulfit.backend.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 알림 히스토리 영속성 어댑터
 * <p>
 * 헥사고날 아키텍처의 출력 어댑터
 * 알림 히스토리 데이터 접근을 담당
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class NotificationHistoryPersistenceAdapter implements NotificationHistoryPort {

    private final NotificationHistoryJpaRepository notificationHistoryJpaRepository;

    @Override
    public NotificationHistory save(NotificationHistory notificationHistory) {
        return notificationHistoryJpaRepository.save(notificationHistory);
    }

    @Override
    public Optional<NotificationHistory> findById(Long id) {
        return notificationHistoryJpaRepository.findById(id);
    }

    @Override
    public Page<NotificationHistory> findByUserId(Long userId, Pageable pageable) {
        return notificationHistoryJpaRepository.findByUserIdOrderBySentAtDesc(userId, pageable);
    }

    @Override
    public Page<NotificationHistory> findByUserIdAndType(Long userId, NotificationType notificationType, Pageable pageable) {
        return notificationHistoryJpaRepository.findByUserIdAndNotificationTypeOrderBySentAtDesc(
                userId, notificationType, pageable);
    }

    @Override
    public Page<NotificationHistory> findByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return notificationHistoryJpaRepository.findByUserIdAndSentAtBetweenOrderBySentAtDesc(
                userId, startDate, endDate, pageable);
    }

    @Override
    public List<NotificationHistory> findUnreadByUserId(Long userId) {
        return notificationHistoryJpaRepository.findByUserIdAndReadAtIsNullOrderBySentAtDesc(userId);
    }

    @Override
    public long countUnreadByUserId(Long userId) {
        return notificationHistoryJpaRepository.countByUserIdAndReadAtIsNull(userId);
    }

    @Override
    public void markAllAsReadByUserId(Long userId) {
        List<NotificationHistory> unreadNotifications = findUnreadByUserId(userId);
        unreadNotifications.forEach(NotificationHistory::markAsRead);
        notificationHistoryJpaRepository.saveAll(unreadNotifications);
    }

    @Override
    public void deleteById(Long id) {
        notificationHistoryJpaRepository.deleteById(id);
    }
}
