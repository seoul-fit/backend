package com.seoulfit.backend.shared.security.audit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 감사 로깅 AOP
 * 
 * @Auditable 어노테이션이 붙은 메서드의 실행을 자동으로 감사 로깅합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;

    /**
     * @Auditable 어노테이션이 붙은 메서드 실행 전후 로깅
     */
    @Around("@annotation(auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        
        String username = getCurrentUsername();
        String ipAddress = getClientIpAddress();
        String resource = auditable.resource().isEmpty() ? 
            joinPoint.getSignature().toShortString() : auditable.resource();
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 메서드 실행
            Object result = joinPoint.proceed();
            
            // 성공 로그
            auditLogService.logAuditEvent(AuditEvent.builder()
                    .type(auditable.eventType())
                    .username(username)
                    .ipAddress(ipAddress)
                    .resource(resource)
                    .action(auditable.action())
                    .result(AuditResult.SUCCESS)
                    .metadata(Map.of(
                        "method", joinPoint.getSignature().getName(),
                        "args", sanitizeArguments(joinPoint.getArgs()),
                        "executionTime", System.currentTimeMillis() - startTime
                    ))
                    .build());
            
            return result;
            
        } catch (Exception e) {
            // 실패 로그
            auditLogService.logAuditEvent(AuditEvent.builder()
                    .type(auditable.eventType())
                    .username(username)
                    .ipAddress(ipAddress)
                    .resource(resource)
                    .action(auditable.action())
                    .result(AuditResult.FAILURE)
                    .errorMessage(e.getMessage())
                    .metadata(Map.of(
                        "method", joinPoint.getSignature().getName(),
                        "exception", e.getClass().getSimpleName(),
                        "executionTime", System.currentTimeMillis() - startTime
                    ))
                    .build());
            
            throw e;
        }
    }

    /**
     * 민감 데이터 접근 로깅
     */
    @Before("@annotation(sensitiveDataAccess)")
    public void auditSensitiveDataAccess(JoinPoint joinPoint, SensitiveDataAccess sensitiveDataAccess) {
        String username = getCurrentUsername();
        String dataType = sensitiveDataAccess.dataType();
        String resource = joinPoint.getSignature().toShortString();
        
        auditLogService.logSensitiveDataAccess(username, dataType, resource);
    }

    /**
     * 관리자 작업 로깅
     */
    @Around("@annotation(adminAction)")
    public Object auditAdminAction(ProceedingJoinPoint joinPoint, AdminAction adminAction) throws Throwable {
        
        String username = getCurrentUsername();
        String ipAddress = getClientIpAddress();
        String action = adminAction.value();
        
        log.warn("ADMIN_ACTION: User {} from IP {} performing: {}", 
                username, ipAddress, action);
        
        try {
            Object result = joinPoint.proceed();
            
            auditLogService.logAuditEvent(AuditEvent.builder()
                    .type(AuditEventType.ADMIN_ACTION)
                    .username(username)
                    .ipAddress(ipAddress)
                    .action(action)
                    .result(AuditResult.SUCCESS)
                    .metadata(Map.of(
                        "method", joinPoint.getSignature().getName(),
                        "args", sanitizeArguments(joinPoint.getArgs())
                    ))
                    .build());
            
            return result;
            
        } catch (Exception e) {
            auditLogService.logAuditEvent(AuditEvent.builder()
                    .type(AuditEventType.ADMIN_ACTION)
                    .username(username)
                    .ipAddress(ipAddress)
                    .action(action)
                    .result(AuditResult.FAILURE)
                    .errorMessage(e.getMessage())
                    .build());
            
            throw e;
        }
    }

    /**
     * 현재 사용자명 조회
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "anonymous";
    }

    /**
     * 클라이언트 IP 주소 조회
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            
            String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
            };
            
            for (String header : headers) {
                String ip = request.getHeader(header);
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    return ip.split(",")[0].trim();
                }
            }
            
            return request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * 인자 살균 (민감 정보 제거)
     */
    private String sanitizeArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        
        return Arrays.stream(args)
            .map(arg -> {
                if (arg == null) {
                    return "null";
                }
                
                String className = arg.getClass().getSimpleName();
                
                // 민감 정보가 포함될 수 있는 객체는 클래스명만 표시
                if (className.contains("Password") || 
                    className.contains("Token") || 
                    className.contains("Secret") ||
                    className.contains("Credential")) {
                    return className + "[REDACTED]";
                }
                
                // 기본 타입은 그대로 표시
                if (arg instanceof String || arg instanceof Number || arg instanceof Boolean) {
                    return arg.toString();
                }
                
                // 복잡한 객체는 클래스명만 표시
                return className;
            })
            .toList()
            .toString();
    }
}

/**
 * 감사 로깅 대상 표시 어노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Auditable {
    AuditEventType eventType();
    String action() default "";
    String resource() default "";
}

/**
 * 민감 데이터 접근 표시 어노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface SensitiveDataAccess {
    String dataType();
}

/**
 * 관리자 작업 표시 어노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface AdminAction {
    String value();
}