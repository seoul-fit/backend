package com.seoulfit.backend.user.infrastructure.oauth;

import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.infrastructure.dto.OAuthTokenResponse;
import com.seoulfit.backend.user.infrastructure.dto.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Google OAuth 클라이언트 구현
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOAuthClient implements OAuthClient {

    @Qualifier("oauthRestTemplate")
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    private static final String TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final String USER_INFO_URI = "https://www.googleapis.com/oauth2/v2/userinfo";

    @Override
    public boolean supports(AuthProvider provider) {
        return AuthProvider.GOOGLE.equals(provider);
    }

    @Override
    public OAuthTokenResponse exchangeCodeForToken(String authorizationCode, String redirectUri) {
        log.info("Google 토큰 교환 시작: code={}", authorizationCode.substring(0, 10) + "...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            OAuthTokenResponse response = restTemplate.postForObject(TOKEN_URI, request, OAuthTokenResponse.class);
            log.info("Google 토큰 교환 성공");
            return response;
        } catch (Exception e) {
            log.error("Google 토큰 교환 실패", e);
            throw new RuntimeException("Google 토큰 교환에 실패했습니다.", e);
        }
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        log.info("Google 사용자 정보 조회 시작");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.exchange(
                    USER_INFO_URI, HttpMethod.GET, request, Map.class
            ).getBody();

            if (response == null) {
                throw new RuntimeException("Google 사용자 정보 응답이 null입니다.");
            }

            return extractUserInfo(response);
        } catch (Exception e) {
            log.error("Google 사용자 정보 조회 실패", e);
            throw new RuntimeException("Google 사용자 정보 조회에 실패했습니다.", e);
        }
    }

    private OAuthUserInfo extractUserInfo(Map<String, Object> response) {
        String id = String.valueOf(response.get("id"));
        String email = (String) response.get("email");
        String name = (String) response.get("name");
        String picture = (String) response.get("picture");

        return OAuthUserInfo.builder()
                .id(id)
                .email(email)
                .nickname(name)
                .profileImageUrl(picture)
                .provider(AuthProvider.GOOGLE)
                .build();
    }
}
