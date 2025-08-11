package com.seoulfit.backend.notification.application.port.out;

import com.seoulfit.backend.notification.domain.NotificationHistory;
import com.seoulfit.backend.notification.domain.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 알림 히스토리 출력 포트
 * <p>
 * 헥사고날 아키텍처의 출력 포트
 * 알림 히스토리 데이터 접근을 위한 인터페이스
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
public interface NotificationHistoryPort {

    /**
     * 알림 히스토리 저장
     *
     * @param notificationHistory 알림 히스토리
     * @return 저장된 알림 히스토리
     */
    NotificationHistory save(NotificationHistory notificationHistory);

    /**
     * ID로 알림 히스토리 조회
     *
     * @param id 알림 히스토리 ID
     * @return 알림 히스토리
     */
    Optional<NotificationHistory> findById(Long id);

    /**
     * 사용자별 알림 히스토리 조회 (페이징)
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 알림 히스토리 페이지
     */
    Page<NotificationHistory> findByUserId(Long userId, Pageable pageable);

    /**
     * 사용자별 타입별 알림 히스토리 조회 (페이징)
     *
     * @param userId           사용자 ID
     * @param notificationType 알림 타입
     * @param pageable         페이징 정보
     * @return 알림 히스토리 페이지
     */
    Page<NotificationHistory> findByUserIdAndType(Long userId, NotificationType notificationType, Pageable pageable);

    /**
     * 사용자별 기간별 알림 히스토리 조회 (페이징)
     *
     * @param userId    사용자 ID
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @param pageable  페이징 정보
     * @return 알림 히스토리 페이지
     */
    Page<NotificationHistory> findByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 사용자별 읽지 않은 알림 조회
     *
     * @param userId 사용자 ID
     * @return 읽지 않은 알림 목록
     */
    List<NotificationHistory> findUnreadByUserId(Long userId);

    /**
     * 사용자별 읽지 않은 알림 개수 조회
     *
     * @param userId 사용자 ID
     * @return 읽지 않은 알림 개수
     */
    long countUnreadByUserId(Long userId);

    /**
     * 사용자별 모든 알림 읽음 처리
     *
     * @param userId 사용자 ID
     */
    void markAllAsReadByUserId(Long userId);

    /**
     * 알림 히스토리 삭제
     *
     * @param id 알림 히스토리 ID
     */
    void deleteById(Long id);
}
