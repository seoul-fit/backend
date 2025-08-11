package com.seoulfit.backend.trigger.dto;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

/**
 * 트리거 평가 결과
 * <p>
 * 트리거 전략의 평가 결과를 담는 객체
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
@Builder
public class TriggerResult {

    /**
     * 트리거 발동 여부
     */
    private final boolean triggered;

    /**
     * 알림 타입
     */
    private final NotificationType notificationType;

    /**
     * 트리거 조건
     */
    private final TriggerCondition triggerCondition;

    /**
     * 알림 제목
     */
    private final String title;

    /**
     * 알림 메시지
     */
    private final String message;

    /**
     * 위치 정보
     */
    private final String locationInfo;

    /**
     * 트리거 우선순위 (낮을수록 높은 우선순위)
     */
    private final int priority;

    /**
     * 추가 데이터
     */
    private final Map<String, Object> additionalData;

    /**
     * 트리거 발동 결과 생성
     *
     * @param notificationType 알림 타입
     * @param triggerCondition 트리거 조건
     * @param title            알림 제목
     * @param message          알림 메시지
     * @param locationInfo     위치 정보
     * @return 트리거 결과
     */
    public static TriggerResult triggered(NotificationType notificationType,
            TriggerCondition triggerCondition,
            String title, String message, String locationInfo) {
        return TriggerResult.builder()
                .triggered(true)
                .notificationType(notificationType)
                .triggerCondition(triggerCondition)
                .title(title)
                .message(message)
                .locationInfo(locationInfo)
                .priority(50) // 기본 우선순위
                .build();
    }

    /**
     * 트리거 미발동 결과 생성
     *
     * @return 트리거 결과
     */
    public static TriggerResult notTriggered() {
        return TriggerResult.builder()
                .triggered(false)
                .build();
    }

    /**
     * 우선순위가 높은 트리거 결과 생성
     *
     * @param notificationType 알림 타입
     * @param triggerCondition 트리거 조건
     * @param title            알림 제목
     * @param message          알림 메시지
     * @param locationInfo     위치 정보
     * @param priority         우선순위
     * @return 트리거 결과
     */
    public static TriggerResult highPriorityTriggered(NotificationType notificationType,
            TriggerCondition triggerCondition,
            String title, String message,
            String locationInfo, int priority) {
        return TriggerResult.builder()
                .triggered(true)
                .notificationType(notificationType)
                .triggerCondition(triggerCondition)
                .title(title)
                .message(message)
                .locationInfo(locationInfo)
                .priority(priority)
                .build();
    }
}
