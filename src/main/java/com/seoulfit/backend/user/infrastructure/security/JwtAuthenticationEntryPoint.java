package com.seoulfit.backend.user.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoulfit.backend.shared.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * JWT 인증 실패 시 처리 핸들러
 * 
 * 인증되지 않은 사용자가 보호된 리소스에 접근할 때
 * 일관된 에러 응답을 제공합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        String requestUri = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");
        
        log.warn("Unauthorized access attempt to {} - Auth header present: {}", 
                requestUri, authHeader != null);

        // 에러 응답 구성
        ApiResponse<Object> errorResponse = ApiResponse.error(
            HttpServletResponse.SC_UNAUTHORIZED,
            determineErrorMessage(authHeader, authException),
            requestUri
        );

        // 응답 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        
        // 보안 헤더 추가
        response.setHeader("WWW-Authenticate", "Bearer realm=\"SeoulFit API\"");
        
        // JSON 응답 작성
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    /**
     * 에러 메시지 결정
     */
    private String determineErrorMessage(String authHeader, AuthenticationException exception) {
        if (authHeader == null || authHeader.isEmpty()) {
            return "인증이 필요합니다. Authorization 헤더를 포함해주세요.";
        }
        
        if (!authHeader.startsWith("Bearer ")) {
            return "잘못된 인증 형식입니다. 'Bearer {token}' 형식을 사용해주세요.";
        }
        
        if (exception.getMessage() != null && exception.getMessage().contains("expired")) {
            return "토큰이 만료되었습니다. 다시 로그인해주세요.";
        }
        
        return "유효하지 않은 토큰입니다.";
    }
}