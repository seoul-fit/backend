package com.seoulfit.backend.user.application.port.in.dto;

import com.seoulfit.backend.user.domain.AuthProvider;
import lombok.Builder;
import lombok.Getter;

/**
 * OAuth 인가코드 검증 명령
 * 인가코드로 사용자 정보를 조회하기 위한 명령 객체
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Getter
@Builder
public class OAuthAuthorizeCheckCommand {

    private final AuthProvider provider;
    private final String authorizationCode;
    private final String redirectUri;

    /**
     * OAuth 인가코드 검증 명령 생성
     *
     * @param provider OAuth 제공자
     * @param authorizationCode 권한부여 승인코드
     * @param redirectUri 리다이렉트 URI
     * @return OAuth 인가코드 검증 명령
     */
    public static OAuthAuthorizeCheckCommand of(AuthProvider provider, String authorizationCode, String redirectUri) {
        return OAuthAuthorizeCheckCommand.builder()
                .provider(provider)
                .authorizationCode(authorizationCode)
                .redirectUri(redirectUri)
                .build();
    }
}
