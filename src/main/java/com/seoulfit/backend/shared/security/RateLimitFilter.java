package com.seoulfit.backend.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.seoulfit.backend.shared.dto.ApiResponse;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate Limiting 필터
 * 
 * DDoS 공격과 API 남용을 방지하기 위한 요청 제한 필터입니다.
 * IP 주소별로 요청 횟수를 제한합니다.
 * 
 * **Rate Limit 정책:**
 * - 일반 API: 분당 100회
 * - 인증 API: 분당 10회
 * - 검색 API: 분당 50회
 * - 관리자 API: 무제한
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Component
public class RateLimitFilter implements Filter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // IP별 요청 카운터 캐시 (1분 TTL)
    private final Cache<String, AtomicInteger> requestCountCache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(Duration.ofMinutes(1))
            .build();
    
    // 블랙리스트 캐시 (1시간 TTL)
    private final Cache<String, LocalDateTime> blacklistCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofHours(1))
            .build();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String clientIp = getClientIp(httpRequest);
        String endpoint = httpRequest.getRequestURI();
        
        // 관리자 엔드포인트는 Rate Limit 제외
        if (endpoint.startsWith("/api/v1/admin") || endpoint.startsWith("/actuator")) {
            chain.doFilter(request, response);
            return;
        }
        
        // 블랙리스트 체크
        if (isBlacklisted(clientIp)) {
            sendTooManyRequestsResponse(httpResponse, "일시적으로 차단된 IP입니다. 1시간 후 다시 시도해주세요.");
            return;
        }
        
        // Rate Limit 체크
        int limit = getRateLimitForEndpoint(endpoint);
        int currentCount = incrementAndGetRequestCount(clientIp, endpoint);
        
        // 헤더에 Rate Limit 정보 추가
        httpResponse.setHeader("X-Rate-Limit-Limit", String.valueOf(limit));
        httpResponse.setHeader("X-Rate-Limit-Remaining", String.valueOf(Math.max(0, limit - currentCount)));
        httpResponse.setHeader("X-Rate-Limit-Reset", String.valueOf(System.currentTimeMillis() + 60000));
        
        if (currentCount > limit) {
            // 과도한 요청 시 블랙리스트 추가
            if (currentCount > limit * 2) {
                addToBlacklist(clientIp);
                log.warn("IP {} has been blacklisted due to excessive requests", clientIp);
            }
            
            sendTooManyRequestsResponse(httpResponse, 
                String.format("요청 횟수 제한을 초과했습니다. (제한: %d회/분)", limit));
            return;
        }
        
        chain.doFilter(request, response);
    }

    /**
     * 클라이언트 IP 추출
     */
    private String getClientIp(HttpServletRequest request) {
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
                // 여러 IP가 있는 경우 첫 번째 IP 사용
                return ip.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 엔드포인트별 Rate Limit 설정
     */
    private int getRateLimitForEndpoint(String endpoint) {
        // 인증 관련 엔드포인트 - 엄격한 제한
        if (endpoint.startsWith("/api/auth") || endpoint.startsWith("/oauth2")) {
            return 10; // 분당 10회
        }
        
        // 검색 엔드포인트 - 중간 제한
        if (endpoint.contains("search") || endpoint.contains("query")) {
            return 50; // 분당 50회
        }
        
        // 데이터 동기화 엔드포인트 - 낮은 제한
        if (endpoint.contains("sync")) {
            return 5; // 분당 5회
        }
        
        // 쓰기 작업 - 중간 제한
        if ("POST".equalsIgnoreCase(endpoint) || "PUT".equalsIgnoreCase(endpoint) || 
            "DELETE".equalsIgnoreCase(endpoint)) {
            return 30; // 분당 30회
        }
        
        // 기본값 - 일반적인 제한
        return 100; // 분당 100회
    }

    /**
     * 요청 카운트 증가 및 조회
     */
    private int incrementAndGetRequestCount(String clientIp, String endpoint) {
        String key = clientIp + ":" + endpoint;
        AtomicInteger count = requestCountCache.get(key, k -> new AtomicInteger(0));
        return count.incrementAndGet();
    }

    /**
     * 블랙리스트 확인
     */
    private boolean isBlacklisted(String clientIp) {
        return blacklistCache.getIfPresent(clientIp) != null;
    }

    /**
     * 블랙리스트 추가
     */
    private void addToBlacklist(String clientIp) {
        blacklistCache.put(clientIp, LocalDateTime.now());
    }

    /**
     * 429 Too Many Requests 응답
     */
    private void sendTooManyRequestsResponse(HttpServletResponse response, String message) 
            throws IOException {
        
        ApiResponse<Object> errorResponse = ApiResponse.error(
            429,
            message
        );
        
        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}