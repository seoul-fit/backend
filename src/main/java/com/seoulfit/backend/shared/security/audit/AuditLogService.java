package com.seoulfit.backend.shared.security.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 보안 감사 로그 서비스
 * 
 * 보안 관련 이벤트를 추적하고 기록합니다.
 * 
 * **감사 대상 이벤트:**
 * - 로그인/로그아웃
 * - 권한 변경
 * - 민감 데이터 접근
 * - 보안 정책 위반
 * - 관리자 작업
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * 감사 로그 기록
     */
    public void logAuditEvent(AuditEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                AuditLog auditLog = AuditLog.builder()
                        .eventType(event.getType())
                        .userId(event.getUserId())
                        .username(event.getUsername())
                        .ipAddress(event.getIpAddress())
                        .userAgent(event.getUserAgent())
                        .resource(event.getResource())
                        .action(event.getAction())
                        .result(event.getResult())
                        .errorMessage(event.getErrorMessage())
                        .metadata(objectMapper.writeValueAsString(event.getMetadata()))
                        .timestamp(LocalDateTime.now())
                        .build();
                
                auditLogRepository.save(auditLog);
                
                // 중요 이벤트는 별도 로그 파일에도 기록
                if (event.getType().isCritical()) {
                    logCriticalEvent(event);
                }
                
            } catch (Exception e) {
                log.error("Failed to save audit log", e);
            }
        });
    }

    /**
     * 인증 성공 로그
     */
    public void logAuthenticationSuccess(String username, String ipAddress, String userAgent) {
        logAuditEvent(AuditEvent.builder()
                .type(AuditEventType.LOGIN_SUCCESS)
                .username(username)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .action("LOGIN")
                .result(AuditResult.SUCCESS)
                .build());
    }

    /**
     * 인증 실패 로그
     */
    public void logAuthenticationFailure(String username, String ipAddress, String reason) {
        logAuditEvent(AuditEvent.builder()
                .type(AuditEventType.LOGIN_FAILURE)
                .username(username)
                .ipAddress(ipAddress)
                .action("LOGIN")
                .result(AuditResult.FAILURE)
                .errorMessage(reason)
                .metadata(Map.of("failureReason", reason))
                .build());
    }

    /**
     * 권한 접근 거부 로그
     */
    public void logAccessDenied(String username, String resource, String requiredRole) {
        logAuditEvent(AuditEvent.builder()
                .type(AuditEventType.ACCESS_DENIED)
                .username(username)
                .resource(resource)
                .action("ACCESS")
                .result(AuditResult.DENIED)
                .metadata(Map.of("requiredRole", requiredRole))
                .build());
    }

    /**
     * 민감 데이터 접근 로그
     */
    public void logSensitiveDataAccess(String username, String dataType, String resource) {
        logAuditEvent(AuditEvent.builder()
                .type(AuditEventType.SENSITIVE_DATA_ACCESS)
                .username(username)
                .resource(resource)
                .action("READ")
                .result(AuditResult.SUCCESS)
                .metadata(Map.of("dataType", dataType))
                .build());
    }

    /**
     * 데이터 변경 로그
     */
    public void logDataModification(String username, String entity, String action, 
                                   Object oldValue, Object newValue) {
        logAuditEvent(AuditEvent.builder()
                .type(AuditEventType.DATA_MODIFICATION)
                .username(username)
                .resource(entity)
                .action(action)
                .result(AuditResult.SUCCESS)
                .metadata(Map.of(
                    "oldValue", oldValue != null ? oldValue.toString() : "null",
                    "newValue", newValue != null ? newValue.toString() : "null"
                ))
                .build());
    }

    /**
     * 보안 위반 로그
     */
    public void logSecurityViolation(String ipAddress, String violationType, String details) {
        logAuditEvent(AuditEvent.builder()
                .type(AuditEventType.SECURITY_VIOLATION)
                .ipAddress(ipAddress)
                .action("VIOLATION")
                .result(AuditResult.BLOCKED)
                .errorMessage(details)
                .metadata(Map.of("violationType", violationType))
                .build());
        
        // 보안 위반은 즉시 알림
        notifySecurityTeam(violationType, details, ipAddress);
    }

    /**
     * Rate Limit 초과 로그
     */
    public void logRateLimitExceeded(String ipAddress, String endpoint, int limit) {
        logAuditEvent(AuditEvent.builder()
                .type(AuditEventType.RATE_LIMIT_EXCEEDED)
                .ipAddress(ipAddress)
                .resource(endpoint)
                .action("REQUEST")
                .result(AuditResult.BLOCKED)
                .metadata(Map.of("limit", limit))
                .build());
    }

    /**
     * 중요 이벤트 별도 로깅
     */
    private void logCriticalEvent(AuditEvent event) {
        log.error("CRITICAL_AUDIT_EVENT: type={}, user={}, ip={}, resource={}, result={}",
                event.getType(), event.getUsername(), event.getIpAddress(), 
                event.getResource(), event.getResult());
    }

    /**
     * 보안팀 알림 (실제 구현 시 이메일/슬랙 등으로 전송)
     */
    private void notifySecurityTeam(String violationType, String details, String ipAddress) {
        log.error("SECURITY_ALERT: Violation detected - Type: {}, IP: {}, Details: {}",
                violationType, ipAddress, details);
        // TODO: 실제 알림 구현 (이메일, Slack 등)
    }

    /**
     * 감사 로그 조회
     */
    public Page<AuditLog> getAuditLogs(AuditLogSearchCriteria criteria, Pageable pageable) {
        return auditLogRepository.findByCriteria(criteria, pageable);
    }

    /**
     * 사용자별 감사 로그 조회
     */
    public List<AuditLog> getUserAuditLogs(String username, LocalDateTime from, LocalDateTime to) {
        return auditLogRepository.findByUsernameAndTimestampBetween(username, from, to);
    }

    /**
     * 보안 위반 통계
     */
    public Map<String, Long> getSecurityViolationStats(LocalDateTime from, LocalDateTime to) {
        return auditLogRepository.getViolationStatsByType(from, to);
    }
}