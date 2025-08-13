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

        switch (provider) {
            case KAKAO:
                KakaoOAuthClient kakaoClient = (KakaoOAuthClient) oAuthClientFactory.getClient(provider);
                return kakaoClient.logout(accessToken);
            case GOOGLE:
                // Google 로그아웃 처리 (필요시 구현)
                return Map.of("result", "Google 로그아웃은 클라이언트에서 처리됩니다.");
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + provider);
        }
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

        switch (provider) {
            case KAKAO:
                KakaoOAuthClient kakaoClient = (KakaoOAuthClient) oAuthClientFactory.getClient(provider);
                return kakaoClient.unlink(accessToken);
            case GOOGLE:
                // Google 연결 해제 처리 (필요시 구현)
                return Map.of("result", "Google 연결 해제는 별도 API 호출이 필요합니다.");
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + provider);
        }
    }

    /**
     * Google OAuth 인증 URL 생성
     */
    private String generateGoogleAuthUrl(String redirectUri, String scope, String state) {
        // Google OAuth 설정에서 클라이언트 ID를 가져와야 함
        StringBuilder authUrl = new StringBuilder();
        authUrl.append("https://accounts.google.com/oauth/authorize");
        authUrl.append("?client_id=").append("your-google-client-id"); // 실제 구현시 설정값 사용
        authUrl.append("&redirect_uri=").append(redirectUri);
        authUrl.append("&response_type=code");
        authUrl.append("&scope=").append(scope != null ? scope : "profile email");

        if (state != null && !state.trim().isEmpty()) {
            authUrl.append("&state=").append(state);
        }

        return authUrl.toString();
    }
}
