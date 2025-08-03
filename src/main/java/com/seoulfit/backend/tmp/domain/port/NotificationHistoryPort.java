package com.seoulfit.backend.tmp.domain.port;

import com.seoulfit.backend.notification.domain.NotificationHistory;
import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 알림 히스토리 도메인 포트
 * Hexagonal Architecture의 아웃바운드 포트
 */
public interface NotificationHistoryPort {
    
//    /**
//     * 알림 히스토리 저장
//     * @param notificationHistory 저장할 알림 히스토리
//     * @return 저장된 알림 히스토리
//     */
//    NotificationHistory save(NotificationHistory notificationHistory);
//
//    /**
//     * ID로 알림 히스토리 조회
//     * @param id 알림 히스토리 ID
//     * @return 알림 히스토리 Optional
//     */
//    Optional<NotificationHistory> findById(Long id);
//
//    /**
//     * 사용자의 알림 히스토리 페이징 조회
//     * @param user 사용자
//     * @param pageable 페이징 정보
//     * @return 알림 히스토리 페이지
//     */
//    Page<NotificationHistory> findByUser(User user, Pageable pageable);
//
//    /**
//     * 사용자의 특정 타입 알림 히스토리 조회
//     * @param user 사용자
//     * @param notificationType 알림 타입
//     * @param pageable 페이징 정보
//     * @return 알림 히스토리 페이지
//     */
//    Page<NotificationHistory> findByUserAndNotificationType(User user, NotificationType notificationType, Pageable pageable);
//
//    /**
//     * 사용자의 읽지 않은 알림 개수 조회
//     * @param user 사용자
//     * @return 읽지 않은 알림 개수
//     */
//    long countUnreadByUser(User user);
//
//    /**
//     * 사용자의 특정 기간 알림 히스토리 조회
//     * @param user 사용자
//     * @param startDate 시작 날짜
//     * @param endDate 종료 날짜
//     * @param pageable 페이징 정보
//     * @return 알림 히스토리 페이지
//     */
//    Page<NotificationHistory> findByUserAndSentAtBetween(User user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
//
//    /**
//     * 사용자의 최근 알림 히스토리 조회
//     * @param user 사용자
//     * @param limit 조회 개수
//     * @return 최근 알림 히스토리 목록
//     */
//    List<NotificationHistory> findRecentByUser(User user, int limit);
}
