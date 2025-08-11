package com.seoulfit.backend.event.application.service;

import com.seoulfit.backend.event.application.port.in.ManageEventUseCase;
import com.seoulfit.backend.event.application.port.in.dto.EventResult;
import com.seoulfit.backend.event.application.port.in.dto.PublishEventCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 이벤트 서비스
 * <p>
 * 헥사고날 아키텍처의 애플리케이션 서비스
 * 도메인 이벤트 발행 및 처리와 관련된 비즈니스 로직을 처리
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventService implements ManageEventUseCase {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public EventResult publishEvent(PublishEventCommand command) {
        try {
            String eventId = UUID.randomUUID().toString();
            LocalDateTime publishedAt = LocalDateTime.now();

            // TODO: 실제 도메인 이벤트 객체 생성 및 발행
            // DomainEvent domainEvent = createDomainEvent(command, eventId, publishedAt);
            // eventPublisher.publishEvent(domainEvent);

            log.info("이벤트 발행 완료: eventId={}, eventType={}, userId={}", 
                    eventId, command.getEventType(), command.getUserId());

            return EventResult.success(eventId, command.getEventType(), command.getUserId(), publishedAt);
        } catch (Exception e) {
            log.error("이벤트 발행 실패: eventType={}, userId={}, error={}", 
                    command.getEventType(), command.getUserId(), e.getMessage());
            return EventResult.failure(command.getEventType(), command.getUserId(), e.getMessage());
        }
    }

    @Override
    public EventResult publishNotificationEvent(Long userId, String notificationType, String title, 
                                              String message, String triggerCondition, String locationInfo) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("notificationType", notificationType);
        eventData.put("title", title);
        eventData.put("message", message);
        eventData.put("triggerCondition", triggerCondition);
        eventData.put("locationInfo", locationInfo);

        PublishEventCommand command = PublishEventCommand.of("NOTIFICATION", userId, eventData);
        return publishEvent(command);
    }
}
