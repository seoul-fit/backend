package com.seoulfit.backend.infra;

import com.seoulfit.backend.domain.port.NotificationHistoryPort;
import com.seoulfit.backend.notification.domain.NotificationHistory;
import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 알림 히스토리 Repository 어댑터
 * Hexagonal Architecture의 아웃바운드 어댑터
 */
@Repository
@RequiredArgsConstructor
public class NotificationHistoryRepositoryAdapter implements NotificationHistoryPort {
//
//    private final NotificationHistoryRepository notificationHistoryRepository;
//
//    @Override
//    public NotificationHistory save(NotificationHistory notificationHistory) {
//        return notificationHistoryRepository.save(notificationHistory);
//    }
//
//    @Override
//    public Optional<NotificationHistory> findById(Long id) {
//        return notificationHistoryRepository.findById(id);
//    }
//
//    @Override
//    public Page<NotificationHistory> findByUser(User user, Pageable pageable) {
//        return notificationHistoryRepository.findByUserIdOrderBySentAtDesc(user, pageable);
//    }
//
//    @Override
//    public Page<NotificationHistory> findByUserAndNotificationType(User user, NotificationType notificationType, Pageable pageable) {
//        return notificationHistoryRepository.findByUserIdAndNotificationTypeOrderBySentAtDesc(user, notificationType, pageable);
//    }
//
//    @Override
//    public long countUnreadByUser(User user) {
//        return notificationHistoryRepository.countByUserIdAndReadAtIsNull(user);
//    }
//
//    @Override
//    public Page<NotificationHistory> findByUserAndSentAtBetween(User user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
//        return notificationHistoryRepository.findByUserAndSentAtBetweenOrderBySentAtDesc(user, startDate, endDate, pageable);
//    }
//
//    @Override
//    public List<NotificationHistory> findRecentByUser(User user, int limit) {
//        Pageable pageable = PageRequest.of(0, limit);
//        return notificationHistoryRepository.findRecentByUser(user, pageable);
//    }
}
