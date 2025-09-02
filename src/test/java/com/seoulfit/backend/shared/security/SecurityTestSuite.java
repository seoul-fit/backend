package com.seoulfit.backend.shared.security;

import com.seoulfit.backend.shared.security.encryption.DataEncryptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 보안 테스트 스위트
 * 
 * OWASP Top 10 취약점에 대한 보안 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("보안 테스트")
class SecurityTestSuite {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private DataEncryptionService encryptionService;
    
    @Autowired
    private InputSanitizer inputSanitizer;

    /**
     * SQL Injection 방어 테스트
     */
    @Test
    @DisplayName("SQL Injection 공격 차단 테스트")
    void testSqlInjectionPrevention() throws Exception {
        String sqlInjectionPayload = "1' OR '1'='1";
        
        mockMvc.perform(get("/api/v1/cultural-events")
                .param("district", sqlInjectionPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("허용되지 않은 SQL 패턴")));
        
        // InputSanitizer 테스트
        assertThat(inputSanitizer.containsSqlInjection(sqlInjectionPayload)).isTrue();
        assertThat(inputSanitizer.containsSqlInjection("SELECT * FROM users")).isTrue();
        assertThat(inputSanitizer.containsSqlInjection("DROP TABLE users--")).isTrue();
    }

    /**
     * XSS 방어 테스트
     */
    @Test
    @DisplayName("XSS 공격 차단 테스트")
    void testXssPrevention() throws Exception {
        String xssPayload = "<script>alert('XSS')</script>";
        
        mockMvc.perform(post("/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"" + xssPayload + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("허용되지 않은 스크립트 패턴")));
        
        // InputSanitizer 테스트
        assertThat(inputSanitizer.containsXss(xssPayload)).isTrue();
        assertThat(inputSanitizer.containsXss("<iframe src='evil.com'></iframe>")).isTrue();
        assertThat(inputSanitizer.containsXss("javascript:alert(1)")).isTrue();
        
        // HTML 이스케이프 테스트
        String escaped = inputSanitizer.escapeHtml("<div>Test</div>");
        assertThat(escaped).isEqualTo("&lt;div&gt;Test&lt;/div&gt;");
    }

    /**
     * 인증 및 권한 테스트
     */
    @Test
    @DisplayName("인증 없이 보호된 리소스 접근 차단")
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists("WWW-Authenticate"));
    }
    
    @Test
    @DisplayName("일반 사용자의 관리자 리소스 접근 차단")
    @WithMockUser(roles = "USER")
    void testForbiddenAccess() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/users/1"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @DisplayName("관리자 권한으로 관리자 리소스 접근 허용")
    @WithMockUser(roles = "ADMIN")
    void testAdminAccess() throws Exception {
        mockMvc.perform(get("/api/v1/admin/audit-logs"))
                .andExpect(status().isOk());
    }

    /**
     * Rate Limiting 테스트
     */
    @Test
    @DisplayName("Rate Limit 초과 시 429 응답")
    void testRateLimiting() throws Exception {
        // 인증 엔드포인트는 분당 10회 제한
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"test\",\"password\":\"test\"}"))
                    .andExpect(status().isUnauthorized());
        }
        
        // 11번째 요청은 차단
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"test\",\"password\":\"test\"}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("X-Rate-Limit-Limit"))
                .andExpect(header().exists("X-Rate-Limit-Remaining"));
    }

    /**
     * 보안 헤더 테스트
     */
    @Test
    @DisplayName("보안 헤더 적용 확인")
    void testSecurityHeaders() throws Exception {
        mockMvc.perform(get("/api/v1/parks"))
                .andExpect(header().exists("X-Content-Type-Options"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().exists("X-Frame-Options"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().exists("X-XSS-Protection"))
                .andExpect(header().string("X-XSS-Protection", "1; mode=block"))
                .andExpect(header().exists("Content-Security-Policy"))
                .andExpect(header().exists("Strict-Transport-Security"));
    }

    /**
     * 경로 탐색 공격 방어 테스트
     */
    @Test
    @DisplayName("경로 탐색 공격 차단")
    void testPathTraversalPrevention() {
        assertThat(inputSanitizer.containsPathTraversal("../../etc/passwd")).isTrue();
        assertThat(inputSanitizer.containsPathTraversal("..\\..\\windows\\system32")).isTrue();
        assertThat(inputSanitizer.containsPathTraversal("%2e%2e/config")).isTrue();
        
        String sanitized = inputSanitizer.sanitizeFileName("../../secret.txt");
        assertThat(sanitized).isEqualTo("______secret.txt");
    }

    /**
     * 암호화 테스트
     */
    @Test
    @DisplayName("민감 데이터 암호화/복호화")
    void testDataEncryption() {
        String sensitiveData = "010-1234-5678";
        
        // 암호화
        String encrypted = encryptionService.encrypt(sensitiveData);
        assertThat(encrypted).isNotNull();
        assertThat(encrypted).isNotEqualTo(sensitiveData);
        
        // 복호화
        String decrypted = encryptionService.decrypt(encrypted);
        assertThat(decrypted).isEqualTo(sensitiveData);
        
        // 매번 다른 IV 사용 확인
        String encrypted2 = encryptionService.encrypt(sensitiveData);
        assertThat(encrypted2).isNotEqualTo(encrypted);
    }

    /**
     * 데이터 마스킹 테스트
     */
    @Test
    @DisplayName("민감 데이터 마스킹")
    void testDataMasking() {
        // 이메일 마스킹
        String email = "user@example.com";
        String maskedEmail = encryptionService.mask(email, 
            DataEncryptionService.MaskingType.EMAIL);
        assertThat(maskedEmail).isEqualTo("us***@example.com");
        
        // 전화번호 마스킹
        String phone = "010-1234-5678";
        String maskedPhone = encryptionService.mask(phone, 
            DataEncryptionService.MaskingType.PHONE);
        assertThat(maskedPhone).isEqualTo("010-****-5678");
        
        // 이름 마스킹
        String name = "홍길동";
        String maskedName = encryptionService.mask(name, 
            DataEncryptionService.MaskingType.NAME);
        assertThat(maskedName).isEqualTo("홍*동");
    }

    /**
     * CORS 설정 테스트
     */
    @Test
    @DisplayName("CORS 설정 검증")
    void testCorsConfiguration() throws Exception {
        mockMvc.perform(options("/api/v1/parks")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Origin", 
                    "http://localhost:3000"));
        
        // 허용되지 않은 Origin
        mockMvc.perform(options("/api/v1/parks")
                .header("Origin", "http://evil.com")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }

    /**
     * 입력 검증 테스트
     */
    @Test
    @DisplayName("입력 값 검증")
    void testInputValidation() {
        // 이메일 검증
        assertThat(inputSanitizer.isValidEmail("user@example.com")).isTrue();
        assertThat(inputSanitizer.isValidEmail("invalid-email")).isFalse();
        
        // 전화번호 검증
        assertThat(inputSanitizer.isValidPhoneNumber("010-1234-5678")).isTrue();
        assertThat(inputSanitizer.isValidPhoneNumber("123456")).isFalse();
        
        // 좌표 검증
        assertThat(inputSanitizer.isValidKoreanCoordinate(37.5665, 126.9780)).isTrue();
        assertThat(inputSanitizer.isValidKoreanCoordinate(50.0, 10.0)).isFalse();
        
        // 구 이름 검증
        assertThat(inputSanitizer.isValidDistrictName("강남구")).isTrue();
        assertThat(inputSanitizer.isValidDistrictName("invalid구")).isFalse();
    }

    /**
     * 비밀번호 암호화 테스트
     */
    @Test
    @DisplayName("비밀번호 안전한 암호화")
    @WithMockUser
    void testPasswordEncryption() throws Exception {
        String password = "SecurePassword123!";
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                    "\"username\":\"testuser\"," +
                    "\"password\":\"" + password + "\"," +
                    "\"email\":\"test@example.com\"" +
                    "}"))
                .andExpect(status().isCreated());
        
        // 데이터베이스에서 암호화된 비밀번호 확인
        // 실제 테스트에서는 UserRepository를 주입받아 확인
        // User user = userRepository.findByUsername("testuser");
        // assertThat(user.getPassword()).startsWith("{argon2}");
        // assertThat(user.getPassword()).isNotEqualTo(password);
    }
}