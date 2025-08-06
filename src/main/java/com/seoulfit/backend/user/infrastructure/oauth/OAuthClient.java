package com.seoulfit.backend.user.infrastructure.oauth;

import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.infrastructure.dto.OAuthTokenResponse;
import com.seoulfit.backend.user.infrastructure.dto.OAuthUserInfo;

/**
 * OAuth 클라이언트 인터페이스
 * OAuth 제공자와의 통신을 담당
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
public interface OAuthClient {

    /**
     * 지원하는 OAuth 제공자 확인
     *
     * @param provider OAuth 제공자
     * @return 지원 여부
     */
    boolean supports(AuthProvider provider);

    /**
     * Authorization Code를 Access Token으로 교환
     *
     * @param authorizationCode 권한부여 승인코드
     * @param redirectUri 리다이렉트 URI
     * @return OAuth 토큰 응답
     */
    OAuthTokenResponse exchangeCodeForToken(String authorizationCode, String redirectUri);

    /**
     * Access Token으로 사용자 정보 조회
     *
     * @param accessToken 액세스 토큰
     * @return OAuth 사용자 정보
     */
    OAuthUserInfo getUserInfo(String accessToken);
}
