package com.seoulfit.backend.notification.application.port.in.dto;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 알림 생성 커맨드 V2
 */
@Data
@Builder
public class CreateNotificationV2Command {
    
    @NotBlank(message = "제목은 필수입니다")
    private String title;
    
    @NotBlank(message = "메시지는 필수입니다")
    private String message;
    
    @NotNull(message = "알림 타입은 필수입니다")
    private NotificationType type;
    
    // 타겟팅 정보
    private List<Long> targetUserIds;
    private List<String> targetDistricts;
    private List<String> targetInterests;
    
    // 스케줄링 정보
    private LocalDateTime scheduledAt;
    private Boolean immediate;
    
    // 추가 데이터
    private Map<String, Object> metadata;
    private String deepLink;
    private String imageUrl;
    
    // 우선순위
    private Priority priority;
    
    // 만료 시간
    private LocalDateTime expiresAt;
    
    public enum NotificationType {
        INFO,
        WARNING,
        ALERT,
        EVENT,
        PROMOTION,
        SYSTEM
    }
    
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
}