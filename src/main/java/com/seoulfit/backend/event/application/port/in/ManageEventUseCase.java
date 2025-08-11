package com.seoulfit.backend.event.application.port.in;

import com.seoulfit.backend.event.application.port.in.dto.PublishEventCommand;
import com.seoulfit.backend.event.application.port.in.dto.EventResult;

/**
 * 이벤트 관리 Use Case
 * <p>
 * 헥사고날 아키텍처의 입력 포트
 * 도메인 이벤트 발행 및 처리와 관련된 비즈니스 로직을 정의
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
public interface ManageEventUseCase {

    /**
     * 이벤트 발행
     *
     * @param command 이벤트 발행 명령
     * @return 이벤트 결과
     */
    EventResult publishEvent(PublishEventCommand command);

    /**
     * 알림 이벤트 발행
     *
     * @param userId           사용자 ID
     * @param notificationType 알림 타입
     * @param title            제목
     * @param message          메시지
     * @param triggerCondition 트리거 조건
     * @param locationInfo     위치 정보
     * @return 이벤트 결과
     */
    EventResult publishNotificationEvent(Long userId, String notificationType, String title, 
                                       String message, String triggerCondition, String locationInfo);
}
