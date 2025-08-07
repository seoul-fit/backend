package com.seoulfit.backend.notification.adapter.in.web.dto.request;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 생성 요청 DTO
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Getter
@NoArgsConstructor
public class CreateNotificationRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "알림 타입은 필수입니다.")
    private NotificationType notificationType;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "메시지는 필수입니다.")
    private String message;

    @NotNull(message = "트리거 조건은 필수입니다.")
    private TriggerCondition triggerCondition;

    private String locationInfo;
}
