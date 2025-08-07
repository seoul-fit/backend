package com.seoulfit.backend.notification.adapter.in.web.dto.response;

import com.seoulfit.backend.notification.application.port.in.dto.NotificationHistoryResult;
import com.seoulfit.backend.notification.domain.NotificationStatus;
import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 알림 히스토리 응답 DTO Hexagonal Architecture의 인바운드 어댑터 DTO
 */
@Schema(description = "알림 히스토리 응답")
@Getter
@Builder
public class NotificationHistoryResponse {

    @Schema(description = "알림 ID", example = "1")
    private final Long id;

    @Schema(description = "사용자 ID", example = "1")
    private final Long userId;

    @Schema(description = "알림 타입", example = "WEATHER")
    private final NotificationType notificationType;

    @Schema(description = "제목", example = "폭염 주의보 발령")
    private final String title;

    @Schema(description = "메시지", example = "현재 기온이 35도를 넘었습니다. 외출 시 주의하세요.")
    private final String message;

    @Schema(description = "트리거 조건", example = "TEMPERATURE_HIGH")
    private final TriggerCondition triggerCondition;

    @Schema(description = "위치 정보", example = "서울시 강남구")
    private final String locationInfo;

    @Schema(description = "알림 상태", example = "SENT")
    private final NotificationStatus status;

    @Schema(description = "발송 시간", example = "2024-08-01T10:30:00")
    private final LocalDateTime sentAt;

    @Schema(description = "읽은 시간", example = "2024-08-01T10:35:00")
    private final LocalDateTime readAt;

    @Schema(description = "읽음 여부", example = "false")
    private final boolean isRead;

    /**
     * NotificationHistoryResult로부터 응답 DTO 생성
     *
     * @param result 알림 히스토리 결과
     * @return 알림 히스토리 응답 DTO
     */
    public static NotificationHistoryResponse from(NotificationHistoryResult result) {
        return NotificationHistoryResponse.builder()
                .id(result.id())
                .userId(result.userId())
                .notificationType(result.getNotificationType())
                .title(result.title())
                .message(result.message())
                .triggerCondition(result.triggerCondition())
                .locationInfo(result.locationInfo())
                .status(result.status())
                .sentAt(result.sentAt())
                .readAt(result.readAt())
                .isRead(result.isRead())
                .build();
    }
}
