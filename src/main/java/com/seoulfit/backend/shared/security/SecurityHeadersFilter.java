package com.seoulfit.backend.shared.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 보안 헤더 필터
 * 
 * OWASP 권장 보안 헤더를 모든 응답에 추가하여
 * XSS, Clickjacking, MIME Sniffing 등의 공격을 방어합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Component
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // XSS 방어
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Content Type Sniffing 방어
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        
        // Clickjacking 방어
        httpResponse.setHeader("X-Frame-Options", "DENY");
        
        // HTTPS 강제
        httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        
        // Referrer 정책
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // 권한 정책 (Feature Policy)
        httpResponse.setHeader("Permissions-Policy", 
            "geolocation=(self), microphone=(), camera=(), payment=()");
        
        // CSP (Content Security Policy) - 기본 설정
        httpResponse.setHeader("Content-Security-Policy", 
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net; " +
            "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; " +
            "img-src 'self' data: https:; " +
            "font-src 'self' data: https://fonts.gstatic.com; " +
            "connect-src 'self' https://api.seoul.go.kr; " +
            "frame-ancestors 'none'; " +
            "base-uri 'self'; " +
            "form-action 'self'");
        
        chain.doFilter(request, response);
    }
}