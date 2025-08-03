package com.seoulfit.backend.notification.application.port.in.dto;

import com.seoulfit.backend.notification.domain.NotificationStatus;
import com.seoulfit.backend.notification.domain.NotificationType;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;

/**
 * 알림 히스토리 조회 쿼리
 */
public record NotificationHistoryQuery(
        Long userId,
        NotificationType type,
        NotificationStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
) {

    public NotificationHistoryQuery {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("페이지 정보는 필수입니다.");
        }
    }

    /**
     * 기본 쿼리 생성
     */
    public static NotificationHistoryQuery of(Long userId, Pageable pageable) {
        return new NotificationHistoryQuery(userId, null, null, null, null, pageable);
    }

    /**
     * 빌더 패턴을 위한 정적 메서드
     */
    public static NotificationHistoryQueryBuilder builder() {
        return new NotificationHistoryQueryBuilder();
    }

    // 기존 코드와의 호환성을 위한 별칭 메서드들
    public NotificationType getNotificationType() {
        return type;
    }

    public static class NotificationHistoryQueryBuilder {

        private Long userId;
        private NotificationType type;
        private NotificationStatus status;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Pageable pageable;

        public NotificationHistoryQueryBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public NotificationHistoryQueryBuilder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public NotificationHistoryQueryBuilder status(NotificationStatus status) {
            this.status = status;
            return this;
        }

        public NotificationHistoryQueryBuilder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public NotificationHistoryQueryBuilder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public NotificationHistoryQueryBuilder pageable(Pageable pageable) {
            this.pageable = pageable;
            return this;
        }

        public NotificationHistoryQuery build() {
            return new NotificationHistoryQuery(userId, type, status, startDate, endDate, pageable);
        }
    }
}
