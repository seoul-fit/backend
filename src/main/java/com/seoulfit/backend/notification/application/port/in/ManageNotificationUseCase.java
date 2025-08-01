package com.seoulfit.backend.notification.application.port.in;

import com.seoulfit.backend.notification.application.port.in.dto.CreateNotificationCommand;
import com.seoulfit.backend.notification.application.port.in.dto.NotificationHistoryQuery;
import com.seoulfit.backend.notification.application.port.in.dto.NotificationHistoryResult;
import org.springframework.data.domain.Page;

/**
 * 알림 관리 Use Case
 * <p>
 * 헥사고날 아키텍처의 입력 포트
 * 알림 생성, 조회, 읽음 처리 등의 비즈니스 로직을 정의
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
public interface ManageNotificationUseCase {

    /**
     * 알림 생성
     *
     * @param command 알림 생성 명령
     * @return 생성된 알림 결과
     */
    NotificationHistoryResult createNotification(CreateNotificationCommand command);

    /**
     * 알림 히스토리 조회
     *
     * @param query 알림 히스토리 조회 쿼리
     * @return 알림 히스토리 페이지
     */
    Page<NotificationHistoryResult> getNotificationHistory(NotificationHistoryQuery query);

    /**
     * 알림 읽음 처리
     *
     * @param notificationId 알림 ID
     * @param userId         사용자 ID
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 읽지 않은 알림 개수 조회
     *
     * @param userId 사용자 ID
     * @return 읽지 않은 알림 개수
     */
    long getUnreadCount(Long userId);

    /**
     * 모든 알림 읽음 처리
     *
     * @param userId 사용자 ID
     */
    void markAllAsRead(Long userId);
}
