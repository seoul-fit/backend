package com.seoulfit.backend.shared.security.audit;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 감사 로그 엔티티
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_username", columnList = "username"),
    @Index(name = "idx_audit_event_type", columnList = "event_type"),
    @Index(name = "idx_audit_ip_address", columnList = "ip_address")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private AuditEventType eventType;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "resource", length = 255)
    private String resource;

    @Column(name = "action", length = 50)
    private String action;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 20)
    private AuditResult result;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}

/**
 * 감사 이벤트 타입
 */
enum AuditEventType {
    // 인증 관련
    LOGIN_SUCCESS(true),
    LOGIN_FAILURE(true),
    LOGOUT(false),
    PASSWORD_CHANGE(true),
    
    // 권한 관련
    ACCESS_GRANTED(false),
    ACCESS_DENIED(true),
    PERMISSION_CHANGED(true),
    
    // 데이터 관련
    DATA_CREATE(false),
    DATA_READ(false),
    DATA_UPDATE(false),
    DATA_DELETE(true),
    SENSITIVE_DATA_ACCESS(true),
    DATA_MODIFICATION(false),
    
    // 보안 위반
    SECURITY_VIOLATION(true),
    RATE_LIMIT_EXCEEDED(true),
    INVALID_INPUT(true),
    SQL_INJECTION_ATTEMPT(true),
    XSS_ATTEMPT(true),
    
    // 시스템 관련
    SYSTEM_CONFIG_CHANGE(true),
    ADMIN_ACTION(true);
    
    private final boolean critical;
    
    AuditEventType(boolean critical) {
        this.critical = critical;
    }
    
    public boolean isCritical() {
        return critical;
    }
}

/**
 * 감사 결과
 */
enum AuditResult {
    SUCCESS,
    FAILURE,
    DENIED,
    BLOCKED,
    ERROR
}

/**
 * 감사 이벤트 빌더
 */
@Builder
@Getter
class AuditEvent {
    private AuditEventType type;
    private Long userId;
    private String username;
    private String ipAddress;
    private String userAgent;
    private String resource;
    private String action;
    private AuditResult result;
    private String errorMessage;
    private Map<String, Object> metadata;
}