package com.seoulfit.backend.user.domain.exception;

/**
 * OAuth 관련 예외
 * 카카오 OAuth API 에러 코드 기준
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
public class OAuthException extends RuntimeException {

    private final String errorCode;
    private final String errorDescription;

    public OAuthException(String errorCode, String errorDescription) {
        super(String.format("[%s] %s", errorCode, errorDescription));
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    public OAuthException(String message) {
        super(message);
        this.errorCode = "UNKNOWN";
        this.errorDescription = message;
    }

    public OAuthException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN";
        this.errorDescription = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    // 카카오 OAuth 주요 에러 코드
    public static class KakaoError {
        public static final String INVALID_REQUEST = "KOE001";
        public static final String INVALID_CLIENT = "KOE002";
        public static final String INVALID_GRANT = "KOE003";
        public static final String UNAUTHORIZED_CLIENT = "KOE004";
        public static final String UNSUPPORTED_GRANT_TYPE = "KOE005";
        public static final String INVALID_SCOPE = "KOE006";
        public static final String INVALID_TOKEN = "KOE401";
        public static final String INSUFFICIENT_SCOPE = "KOE403";
    }

    // 편의 메서드들
    public static OAuthException invalidRequest(String description) {
        return new OAuthException(KakaoError.INVALID_REQUEST, description);
    }

    public static OAuthException invalidClient(String description) {
        return new OAuthException(KakaoError.INVALID_CLIENT, description);
    }

    public static OAuthException invalidGrant(String description) {
        return new OAuthException(KakaoError.INVALID_GRANT, description);
    }

    public static OAuthException invalidToken(String description) {
        return new OAuthException(KakaoError.INVALID_TOKEN, description);
    }
}
