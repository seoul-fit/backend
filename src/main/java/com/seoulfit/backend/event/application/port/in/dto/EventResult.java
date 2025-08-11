package com.seoulfit.backend.event.application.port.in.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 이벤트 결과
 * <p>
 * 헥사고날 아키텍처의 Result 객체
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
@Builder
public class EventResult {

    private final String eventId;
    private final String eventType;
    private final Long userId;
    private final boolean published;
    private final LocalDateTime publishedAt;
    private final String message;

    /**
     * 성공한 이벤트 결과 생성
     *
     * @param eventId     이벤트 ID
     * @param eventType   이벤트 타입
     * @param userId      사용자 ID
     * @param publishedAt 발행 시간
     * @return 이벤트 결과
     */
    public static EventResult success(String eventId, String eventType, Long userId, LocalDateTime publishedAt) {
        return EventResult.builder()
                .eventId(eventId)
                .eventType(eventType)
                .userId(userId)
                .published(true)
                .publishedAt(publishedAt)
                .message("이벤트가 성공적으로 발행되었습니다.")
                .build();
    }

    /**
     * 실패한 이벤트 결과 생성
     *
     * @param eventType 이벤트 타입
     * @param userId    사용자 ID
     * @param message   실패 메시지
     * @return 이벤트 결과
     */
    public static EventResult failure(String eventType, Long userId, String message) {
        return EventResult.builder()
                .eventType(eventType)
                .userId(userId)
                .published(false)
                .message(message)
                .build();
    }
}
