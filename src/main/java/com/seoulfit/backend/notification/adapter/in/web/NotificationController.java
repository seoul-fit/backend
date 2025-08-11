package com.seoulfit.backend.notification.adapter.in.web;

import com.seoulfit.backend.notification.application.port.in.ManageNotificationUseCase;
import com.seoulfit.backend.notification.application.port.in.dto.CreateNotificationCommand;
import com.seoulfit.backend.notification.application.port.in.dto.NotificationHistoryQuery;
import com.seoulfit.backend.notification.application.port.in.dto.NotificationHistoryResult;
import com.seoulfit.backend.notification.adapter.in.web.dto.request.CreateNotificationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 알림 컨트롤러
 * <p>
 * 헥사고날 아키텍처의 입력 어댑터
 * 알림과 관련된 HTTP 요청을 처리
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 관리 API")
public class NotificationController {

    private final ManageNotificationUseCase manageNotificationUseCase;

    @Operation(summary = "알림 생성", description = "새로운 알림을 생성합니다.")
    @PostMapping
    public ResponseEntity<NotificationHistoryResult> createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {
        
        CreateNotificationCommand command = CreateNotificationCommand.of(
                request.getUserId(),
                request.getNotificationType(),
                request.getTitle(),
                request.getMessage(),
                request.getTriggerCondition(),
                request.getLocationInfo()
        );
        
        NotificationHistoryResult result = manageNotificationUseCase.createNotification(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "내 알림 히스토리 조회", description = "사용자의 알림 히스토리를 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<NotificationHistoryResult>> getMyNotifications(
            @RequestParam Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        NotificationHistoryQuery query = NotificationHistoryQuery.of(userId, pageable);
        Page<NotificationHistoryResult> result = manageNotificationUseCase.getNotificationHistory(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        
        manageNotificationUseCase.markAsRead(notificationId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "읽지 않은 알림 개수", description = "사용자의 읽지 않은 알림 개수를 조회합니다.")
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@RequestParam Long userId) {
        long count = manageNotificationUseCase.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 알림을 읽음 처리합니다.")
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@RequestParam Long userId) {
        manageNotificationUseCase.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
