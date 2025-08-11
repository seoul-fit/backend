package com.seoulfit.backend.notification.adapter.out.persistence;

import com.seoulfit.backend.notification.domain.NotificationHistory;
import com.seoulfit.backend.notification.domain.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 알림 히스토리 JPA Repository
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
public interface NotificationHistoryJpaRepository extends JpaRepository<NotificationHistory, Long> {

    /**
     * 사용자별 알림 히스토리 조회 (최신순)
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 알림 히스토리 페이지
     */
    Page<NotificationHistory> findByUserIdOrderBySentAtDesc(Long userId, Pageable pageable);

    /**
     * 사용자별 타입별 알림 히스토리 조회 (최신순)
     *
     * @param userId           사용자 ID
     * @param notificationType 알림 타입
     * @param pageable         페이징 정보
     * @return 알림 히스토리 페이지
     */
    Page<NotificationHistory> findByUserIdAndNotificationTypeOrderBySentAtDesc(
            Long userId, NotificationType notificationType, Pageable pageable);

    /**
     * 사용자별 기간별 알림 히스토리 조회 (최신순)
     *
     * @param userId    사용자 ID
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @param pageable  페이징 정보
     * @return 알림 히스토리 페이지
     */
    Page<NotificationHistory> findByUserIdAndSentAtBetweenOrderBySentAtDesc(
            Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 사용자별 읽지 않은 알림 조회 (최신순)
     *
     * @param userId 사용자 ID
     * @return 읽지 않은 알림 목록
     */
    List<NotificationHistory> findByUserIdAndReadAtIsNullOrderBySentAtDesc(Long userId);

    /**
     * 사용자별 읽지 않은 알림 개수 조회
     *
     * @param userId 사용자 ID
     * @return 읽지 않은 알림 개수
     */
    long countByUserIdAndReadAtIsNull(Long userId);

    /**
     * 타입별 알림 개수 조회
     *
     * @param notificationType 알림 타입
     * @return 알림 개수
     */
    long countByNotificationType(NotificationType notificationType);

    /**
     * 최근 N일간 알림 개수 조회
     *
     * @param days 일수
     * @return 알림 개수
     */
    @Query("SELECT COUNT(nh) FROM NotificationHistory nh WHERE nh.sentAt >= :startDate")
    long countRecentNotifications(@Param("startDate") LocalDateTime startDate);
}
