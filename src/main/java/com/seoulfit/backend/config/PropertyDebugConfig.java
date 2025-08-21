package com.seoulfit.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 환경변수 및 프로퍼티 값 디버깅용 설정
 * 
 * 개발 시에만 사용하며, 민감정보는 마스킹하여 로그에 출력합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Component
public class PropertyDebugConfig implements CommandLineRunner {

    private final Environment environment;

    @Value("${seoul-api.v1.environment.api-key}")
    private String seoulApiKey;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public PropertyDebugConfig(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        log.info("=== 환경변수 및 프로퍼티 값 디버깅 ===");
        
        // 환경변수에서 직접 읽기
        String envSeoulKey = System.getenv("SEOUL_API_KEY");
        String envKakaoId = System.getenv("KAKAO_CLIENT_ID");
        
        // Spring Environment에서 읽기
        String springSeoulKey = environment.getProperty("SEOUL_API_KEY");
        String springKakaoId = environment.getProperty("KAKAO_CLIENT_ID");
        
        log.info("1. OS 환경변수:");
        log.info("   SEOUL_API_KEY: {}", maskValue(envSeoulKey));
        log.info("   KAKAO_CLIENT_ID: {}", maskValue(envKakaoId));
        
        log.info("2. Spring Environment:");
        log.info("   SEOUL_API_KEY: {}", maskValue(springSeoulKey));
        log.info("   KAKAO_CLIENT_ID: {}", maskValue(springKakaoId));
        
        log.info("3. @Value로 주입된 값:");
        log.info("   seoul-api.api-key: {}", maskValue(seoulApiKey));
        log.info("   kakao.client-id: {}", maskValue(kakaoClientId));
        log.info("   jwt.secret: {}", maskValue(jwtSecret));
        
        log.info("4. 값의 출처:");
        if (envSeoulKey != null) {
            log.info("   SEOUL_API_KEY는 OS 환경변수에서 가져옴");
        } else if (springSeoulKey != null) {
            log.info("   SEOUL_API_KEY는 .env 파일에서 가져옴 (spring-dotenv)");
        } else {
            log.info("   SEOUL_API_KEY는 application.yml 기본값 사용");
        }
        
        log.info("=== 디버깅 완료 ===");
    }
    
    /**
     * 민감정보 마스킹
     */
    private String maskValue(String value) {
        if (value == null) {
            return "null";
        }
        if (value.length() <= 8) {
            return "***";
        }
        return value.substring(0, 4) + "***" + value.substring(value.length() - 4);
    }
}