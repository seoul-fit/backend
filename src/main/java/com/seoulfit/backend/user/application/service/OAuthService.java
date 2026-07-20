package com.seoulfit.backend.user.application.service;

import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.infrastructure.oauth.KakaoOAuthClient;
import com.seoulfit.backend.user.infrastructure.oauth.OAuthClientFactory;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * OAuth 관련 추가 서비스 로그아웃, 연결 해제 등 확장 기능 제공
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final OAuthClientFactory oAuthClientFactory;

    /**
     * OAuth 로그아웃 처리
     *
     * @param provider    OAuth 제공자
     * @param accessToken 액세스 토큰
     * @return 로그아웃 결과
     */
    public Map<String, Object> logout(AuthProvider provider, String accessToken) {
        log.info("OAuth 로그아웃 처리: provider={}", provider);
        KakaoOAuthClient kakaoClient = (KakaoOAuthClient) oAuthClientFactory.getClient(provider);
        return kakaoClient.logout(accessToken);
    }

    /**
     * OAuth 연결 해제 처리
     *
     * @param provider    OAuth 제공자
     * @param accessToken 액세스 토큰
     * @return 연결 해제 결과
     */
    public Map<String, Object> unlink(AuthProvider provider, String accessToken) {
        log.info("OAuth 연결 해제 처리: provider={}", provider);
        KakaoOAuthClient kakaoClient = (KakaoOAuthClient) oAuthClientFactory.getClient(provider);
        return kakaoClient.unlink(accessToken);
    }
}
