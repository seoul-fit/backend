package com.seoulfit.backend.infra;

import com.seoulfit.backend.notification.domain.NotificationHistory;
import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 알림 히스토리 Repository
 */
public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    
//    /**
//     * 사용자의 알림 히스토리 페이징 조회
//     * @param user 사용자
//     * @param pageable 페이징 정보
//     * @return 알림 히스토리 페이지
//     */
//    Page<NotificationHistory> findByUserIdOrderBySentAtDesc(User user, Pageable pageable);
//
//    /**
//     * 사용자의 특정 타입 알림 히스토리 조회
//     * @param user 사용자
//     * @param notificationType 알림 타입
//     * @param pageable 페이징 정보
//     * @return 알림 히스토리 페이지
//     */
//    Page<NotificationHistory> findByUserIdAndNotificationTypeOrderBySentAtDesc(User user, NotificationType notificationType, Pageable pageable);
//
//    /**
//     * 사용자의 읽지 않은 알림 개수 조회
//     * @param user 사용자
//     * @return 읽지 않은 알림 개수
//     */
//    @Query("SELECT COUNT(nh) FROM NotificationHistory nh WHERE nh.user = :user AND nh.readAt IS NULL")
//    long countByUserIdAndReadAtIsNull(@Param("user") User user);
    
//    /**
//     * 사용자의 특정 기간 알림 히스토리 조회
//     * @param user 사용자
//     * @param startDate 시작 날짜
//     * @param endDate 종료 날짜
//     * @param pageable 페이징 정보
//     * @return 알림 히스토리 페이지
//     */
//    Page<NotificationHistory> findByUserAndSentAtBetweenOrderBySentAtDesc(User user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
//    /**
//     * 사용자의 최근 알림 히스토리 조회
//     * @param user 사용자
//     * @param pageable 페이징 정보 (limit 용도)
//     * @return 최근 알림 히스토리 목록
//     */
//    @Query("SELECT nh FROM NotificationHistory nh WHERE nh.user = :user ORDER BY nh.sentAt DESC")
//    List<NotificationHistory> findRecentByUser(@Param("user") User user, Pageable pageable);
}
