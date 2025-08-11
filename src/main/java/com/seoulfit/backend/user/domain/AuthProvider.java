package com.seoulfit.backend.user.domain;

/**
 * OAuth 인증 제공자 열거형
 * <p>
 * 지원하는 OAuth 제공자들을 정의
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
public enum AuthProvider {
    /**
     * 구글 OAuth
     */
    GOOGLE("google", "Google"),

    /**
     * 카카오 OAuth
     */
    KAKAO("kakao", "Kakao"),

    /**
     * 네이버 OAuth
     */
    NAVER("naver", "Naver"),

    /**
     * 로컬 계정 (OAuth 아님)
     */
    LOCAL("local", "Local");

    private final String code;
    private final String displayName;

    AuthProvider(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    /**
     * 코드 반환
     *
     * @return 프로바이더 코드
     */
    public String getCode() {
        return code;
    }

    /**
     * 표시명 반환
     *
     * @return 프로바이더 표시명
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 코드로 프로바이더 찾기
     *
     * @param code 프로바이더 코드
     * @return AuthProvider
     * @throws IllegalArgumentException 지원하지 않는 프로바이더인 경우
     */
    public static AuthProvider fromCode(String code) {
        for (AuthProvider provider : values()) {
            if (provider.code.equals(code)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 OAuth 프로바이더입니다: " + code);
    }

    /**
     * OAuth 프로바이더 여부 확인
     *
     * @return OAuth 프로바이더 여부
     */
    public boolean isOAuth() {
        return this != LOCAL;
    }

    /**
     * Registration ID로 프로바이더 찾기 (OAuth2 호환)
     *
     * @param registrationId Registration ID
     * @return AuthProvider
     * @throws IllegalArgumentException 지원하지 않는 프로바이더인 경우
     */
    public static AuthProvider fromRegistrationId(String registrationId) {
        return fromCode(registrationId);
    }
}
