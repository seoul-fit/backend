package com.seoulfit.backend.event.application.port.in.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * 이벤트 발행 명령
 * <p>
 * 헥사고날 아키텍처의 Command 객체
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
@Builder
public class PublishEventCommand {

    private final String eventType;
    private final Long userId;
    private final Map<String, Object> eventData;

    /**
     * 이벤트 발행 명령 생성
     *
     * @param eventType 이벤트 타입
     * @param userId    사용자 ID
     * @param eventData 이벤트 데이터
     * @return 이벤트 발행 명령
     */
    public static PublishEventCommand of(String eventType, Long userId, Map<String, Object> eventData) {
        return PublishEventCommand.builder()
                .eventType(eventType)
                .userId(userId)
                .eventData(eventData)
                .build();
    }
}
