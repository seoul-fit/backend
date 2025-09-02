package com.seoulfit.backend.shared.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 입력값 검증 및 살균 유틸리티
 * 
 * SQL Injection, XSS, Command Injection 등의 공격을 방지하기 위한
 * 입력값 검증 및 살균 기능을 제공합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Component
public class InputSanitizer {

    // SQL Injection 패턴
    private static final Pattern SQL_PATTERN = Pattern.compile(
        "('.+--)|(--)|(\\|\\|)|(;)|(\\*)|(<>)|(!=)|" +
        "(\\b(ALTER|CREATE|DELETE|DROP|EXEC(UTE)?|INSERT( +INTO)?|MERGE|SELECT|UPDATE|UNION( +ALL)?)\\b)",
        Pattern.CASE_INSENSITIVE
    );

    // XSS 패턴
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(<script[^>]*>.*?</script>)|" +
        "(<iframe[^>]*>.*?</iframe>)|" +
        "(javascript:)|" +
        "(on\\w+\\s*=)|" +
        "(<[^>]+style\\s*=\\s*['\"].*?expression\\s*\\([^>]*>)|" +
        "(<[^>]+style\\s*=\\s*['\"].*?javascript\\s*:[^>]*>)",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    // 파일 경로 탐색 패턴
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
        "(\\.\\./)|(\\.\\\\)|(%2e%2e)|(%252e%252e)"
    );

    // 이메일 검증 패턴
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // 한국 전화번호 패턴
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(01[016789])-?([0-9]{3,4})-?([0-9]{4})$"
    );

    /**
     * SQL Injection 검사
     */
    public boolean containsSqlInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        
        boolean hasSqlPattern = SQL_PATTERN.matcher(input).find();
        if (hasSqlPattern) {
            log.warn("SQL Injection pattern detected in input: {}", 
                    input.substring(0, Math.min(input.length(), 50)));
        }
        return hasSqlPattern;
    }

    /**
     * XSS 공격 검사
     */
    public boolean containsXss(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        
        boolean hasXssPattern = XSS_PATTERN.matcher(input).find();
        if (hasXssPattern) {
            log.warn("XSS pattern detected in input: {}", 
                    input.substring(0, Math.min(input.length(), 50)));
        }
        return hasXssPattern;
    }

    /**
     * 경로 탐색 공격 검사
     */
    public boolean containsPathTraversal(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        
        boolean hasPathTraversal = PATH_TRAVERSAL_PATTERN.matcher(input).find();
        if (hasPathTraversal) {
            log.warn("Path traversal pattern detected in input: {}", input);
        }
        return hasPathTraversal;
    }

    /**
     * HTML 태그 제거
     */
    public String stripHtmlTags(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("<[^>]*>", "");
    }

    /**
     * HTML 특수문자 이스케이프
     */
    public String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;");
    }

    /**
     * SQL 특수문자 이스케이프
     */
    public String escapeSql(String input) {
        if (input == null) {
            return null;
        }
        
        return input
            .replace("'", "''")
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_");
    }

    /**
     * 안전한 파일명 생성
     */
    public String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return null;
        }
        
        // 위험한 문자 제거
        String sanitized = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        // 경로 구분자 제거
        sanitized = sanitized.replace("/", "_").replace("\\", "_");
        
        // 길이 제한
        if (sanitized.length() > 255) {
            sanitized = sanitized.substring(0, 255);
        }
        
        return sanitized;
    }

    /**
     * 이메일 주소 검증
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 전화번호 검증
     */
    public boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        String cleaned = phone.replaceAll("[^0-9]", "");
        return PHONE_PATTERN.matcher(cleaned).matches();
    }

    /**
     * 좌표 검증 (한국 범위)
     */
    public boolean isValidKoreanCoordinate(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return false;
        }
        
        // 한국 좌표 범위
        return latitude >= 33.0 && latitude <= 38.9 &&
               longitude >= 124.0 && longitude <= 132.0;
    }

    /**
     * 구 이름 검증
     */
    public boolean isValidDistrictName(String district) {
        if (district == null || district.isEmpty()) {
            return false;
        }
        
        // 허용된 구 이름 목록
        String[] validDistricts = {
            "강남구", "강동구", "강북구", "강서구", "관악구",
            "광진구", "구로구", "금천구", "노원구", "도봉구",
            "동대문구", "동작구", "마포구", "서대문구", "서초구",
            "성동구", "성북구", "송파구", "양천구", "영등포구",
            "용산구", "은평구", "종로구", "중구", "중랑구"
        };
        
        for (String valid : validDistricts) {
            if (valid.equals(district)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 종합 입력 검증
     */
    public ValidationResult validateInput(String input, InputType type) {
        if (input == null || input.isEmpty()) {
            return ValidationResult.invalid("입력값이 비어있습니다.");
        }
        
        // 공통 검사
        if (containsSqlInjection(input)) {
            return ValidationResult.invalid("허용되지 않은 SQL 패턴이 포함되어 있습니다.");
        }
        
        if (containsXss(input)) {
            return ValidationResult.invalid("허용되지 않은 스크립트 패턴이 포함되어 있습니다.");
        }
        
        // 타입별 추가 검사
        switch (type) {
            case EMAIL:
                if (!isValidEmail(input)) {
                    return ValidationResult.invalid("올바른 이메일 형식이 아닙니다.");
                }
                break;
            case PHONE:
                if (!isValidPhoneNumber(input)) {
                    return ValidationResult.invalid("올바른 전화번호 형식이 아닙니다.");
                }
                break;
            case FILE_NAME:
                if (containsPathTraversal(input)) {
                    return ValidationResult.invalid("파일 경로 탐색 패턴이 포함되어 있습니다.");
                }
                break;
            case DISTRICT:
                if (!isValidDistrictName(input)) {
                    return ValidationResult.invalid("유효하지 않은 구 이름입니다.");
                }
                break;
            default:
                // 기본 텍스트는 추가 검사 없음
                break;
        }
        
        return ValidationResult.valid();
    }

    /**
     * 입력 타입 열거형
     */
    public enum InputType {
        TEXT,
        EMAIL,
        PHONE,
        FILE_NAME,
        DISTRICT,
        COORDINATE
    }

    /**
     * 검증 결과
     */
    public record ValidationResult(boolean isValid, String errorMessage) {
        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message);
        }
    }
}