package com.seoulfit.backend.notification.adapter.in.web.dto.request;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 생성 요청 DTO
 * Hexagonal Architecture의 인바운드 어댑터 DTO
 */
@Schema(description = "알림 생성 요청")
@Getter
@NoArgsConstructor
public class CreateNotificationV2Request {

    @Schema(description = "알림 타입", example = "WEATHER")
    @NotNull(message = "알림 타입은 필수입니다.")
    private NotificationType notificationType;

    @Schema(description = "제목", example = "폭염 주의보 발령")
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
    private String title;

    @Schema(description = "메시지", example = "현재 기온이 35도를 넘었습니다. 외출 시 주의하세요.")
    @NotBlank(message = "메시지는 필수입니다.")
    @Size(max = 1000, message = "메시지는 1000자 이하여야 합니다.")
    private String message;

    @Schema(description = "트리거 조건", example = "TEMPERATURE_HIGH")
    @NotNull(message = "트리거 조건은 필수입니다.")
    private TriggerCondition triggerCondition;

    @Schema(description = "위치 정보", example = "서울시 강남구")
    @Size(max = 500, message = "위치 정보는 500자 이하여야 합니다.")
    private String locationInfo;

    @Builder
    public CreateNotificationV2Request(NotificationType notificationType, String title, String message,
                                       TriggerCondition triggerCondition, String locationInfo) {
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.triggerCondition = triggerCondition;
        this.locationInfo = locationInfo;
    }
}
