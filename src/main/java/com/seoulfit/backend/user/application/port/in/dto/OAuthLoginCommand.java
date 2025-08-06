package com.seoulfit.backend.user.application.port.in.dto;

import com.seoulfit.backend.user.domain.AuthProvider;
import lombok.Builder;
import lombok.Getter;

/**
 * OAuth 로그인 명령
 * <p>
 * 헥사고날 아키텍처의 Command 객체
 * Authorization Code Flow를 지원하도록 업데이트
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Getter
@Builder
public class OAuthLoginCommand {

    private final AuthProvider provider;
    private final String authorizationCode;
    private final String redirectUri;
    
    // 기존 방식과의 호환성을 위해 유지 (deprecated)
    @Deprecated
    private final String oauthUserId;

    /**
     * OAuth 로그인 명령 생성 (Authorization Code Flow)
     *
     * @param provider OAuth 프로바이더
     * @param authorizationCode 권한부여 승인코드
     * @param redirectUri 리다이렉트 URI
     * @return OAuth 로그인 명령
     */
    public static OAuthLoginCommand of(AuthProvider provider, String authorizationCode, String redirectUri) {
        return OAuthLoginCommand.builder()
                .provider(provider)
                .authorizationCode(authorizationCode)
                .redirectUri(redirectUri)
                .build();
    }

    /**
     * OAuth 로그인 명령 생성 (기존 방식 - deprecated)
     *
     * @param provider    OAuth 프로바이더
     * @param oauthUserId OAuth 사용자 ID
     * @return OAuth 로그인 명령
     */
    @Deprecated
    public static OAuthLoginCommand ofLegacy(AuthProvider provider, String oauthUserId) {
        return OAuthLoginCommand.builder()
                .provider(provider)
                .oauthUserId(oauthUserId)
                .build();
    }
}
