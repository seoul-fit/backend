package com.seoulfit.backend.event;

import com.seoulfit.backend.notification.application.port.in.ManageNotificationUseCase;
import com.seoulfit.backend.notification.application.port.in.dto.CreateNotificationCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 알림 이벤트 리스너
 * 
 * Observer Pattern을 통한 이벤트 기반 알림 처리
 * 비동기 처리로 성능 최적화
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    
    private final ManageNotificationUseCase manageNotificationHistoryUseCase;
    
    /**
     * 알림 이벤트를 처리합니다.
     * 
     * @param event 알림 이벤트
     */
    @Async
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("알림 이벤트 처리 시작: userId={}, type={}, condition={}", 
                event.getUserId(), event.getType(), event.getTriggerCondition());
        
        try {
            // 알림 히스토리 생성
            CreateNotificationCommand command = CreateNotificationCommand.of(
                    event.getUserId(),
                    event.getType(),
                    event.getTitle(),
                    event.getMessage(),
                    event.getTriggerCondition(),
                    event.getLocationInfo()
            );
            
            manageNotificationHistoryUseCase.createNotification(command);
            
            log.info("알림 이벤트 처리 완료: userId={}, type={}", 
                    event.getUserId(), event.getType());
            
        } catch (Exception e) {
            log.error("알림 이벤트 처리 중 오류 발생: userId={}, type={}", 
                    event.getUserId(), event.getType(), e);
        }
    }
}
