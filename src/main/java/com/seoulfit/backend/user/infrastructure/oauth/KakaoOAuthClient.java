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
 * Kakao OAuth 클라이언트 구현
 * 카카오 공식 문서 기준으로 최신 API 적용
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuthClient implements OAuthClient {

    @Qualifier("oauthRestTemplate")
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret:}")
    private String clientSecret;

    // 카카오 OAuth API 엔드포인트 (공식 문서 기준)
    private static final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";
    private static final String LOGOUT_URI = "https://kapi.kakao.com/v1/user/logout";
    private static final String UNLINK_URI = "https://kapi.kakao.com/v1/user/unlink";

    @Override
    public boolean supports(AuthProvider provider) {
        return AuthProvider.KAKAO.equals(provider);
    }

    @Override
    public OAuthTokenResponse exchangeCodeForToken(String authorizationCode, String redirectUri) {
        log.info("Kakao 토큰 교환 시작: code={}", authorizationCode.substring(0, Math.min(10, authorizationCode.length())) + "...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", authorizationCode);
        
        // 클라이언트 시크릿이 설정된 경우에만 추가 (선택사항)
        if (clientSecret != null && !clientSecret.trim().isEmpty()) {
            params.add("client_secret", clientSecret);
            log.debug("클라이언트 시크릿 사용");
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            OAuthTokenResponse response = restTemplate.postForObject(TOKEN_URI, request, OAuthTokenResponse.class);
            log.info("Kakao 토큰 교환 성공");
            return response;
        } catch (Exception e) {
            log.error("Kakao 토큰 교환 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Kakao 토큰 교환에 실패했습니다: " + e.getMessage(), e);
        }
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        log.info("Kakao 사용자 정보 조회 시작");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);

        // 공식 문서에 따르면 사용자 정보 조회는 POST 방식 사용
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.exchange(
                    USER_INFO_URI, HttpMethod.POST, request, Map.class
            ).getBody();

            if (response == null) {
                throw new RuntimeException("Kakao 사용자 정보 응답이 null입니다.");
            }

            log.info("Kakao 사용자 정보 조회 성공");
            return extractUserInfo(response);
        } catch (Exception e) {
            log.error("Kakao 사용자 정보 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Kakao 사용자 정보 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 카카오 로그아웃 처리
     * 
     * @param accessToken 액세스 토큰
     * @return 로그아웃 결과
     */
    public Map<String, Object> logout(String accessToken) {
        log.info("Kakao 로그아웃 시작");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.exchange(
                    LOGOUT_URI, HttpMethod.POST, request, Map.class
            ).getBody();

            log.info("Kakao 로그아웃 성공");
            return response;
        } catch (Exception e) {
            log.error("Kakao 로그아웃 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Kakao 로그아웃에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 카카오 연결 해제 처리
     * 
     * @param accessToken 액세스 토큰
     * @return 연결 해제 결과
     */
    public Map<String, Object> unlink(String accessToken) {
        log.info("Kakao 연결 해제 시작");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.exchange(
                    UNLINK_URI, HttpMethod.POST, request, Map.class
            ).getBody();

            log.info("Kakao 연결 해제 성공");
            return response;
        } catch (Exception e) {
            log.error("Kakao 연결 해제 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Kakao 연결 해제에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 카카오 응답에서 사용자 정보 추출
     * 공식 문서 기준 응답 구조 반영
     */
    private OAuthUserInfo extractUserInfo(Map<String, Object> response) {
        String id = String.valueOf(response.get("id"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) response.get("properties");
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");

        String nickname = null;
        String profileImageUrl = null;
        String email = null;

        // properties에서 닉네임과 프로필 이미지 추출
        if (properties != null) {
            nickname = (String) properties.get("nickname");
            profileImageUrl = (String) properties.get("profile_image");
            
            // thumbnail_image도 확인 (더 작은 이미지)
            if (profileImageUrl == null) {
                profileImageUrl = (String) properties.get("thumbnail_image");
            }
        }

        // kakao_account에서 이메일 추출
        if (kakaoAccount != null) {
            email = (String) kakaoAccount.get("email");
            
            // profile 정보도 확인 (properties가 없는 경우 대비)
            if (nickname == null || profileImageUrl == null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                if (profile != null) {
                    if (nickname == null) {
                        nickname = (String) profile.get("nickname");
                    }
                    if (profileImageUrl == null) {
                        profileImageUrl = (String) profile.get("profile_image_url");
                        if (profileImageUrl == null) {
                            profileImageUrl = (String) profile.get("thumbnail_image_url");
                        }
                    }
                }
            }
        }

        log.debug("추출된 사용자 정보 - ID: {}, 닉네임: {}, 이메일: {}", id, nickname, email);

        return OAuthUserInfo.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .provider(AuthProvider.KAKAO)
                .build();
    }

    /**
     * 카카오 인증 URL 생성
     * 
     * @param redirectUri 리다이렉트 URI
     * @param scope 추가 동의항목 (선택사항)
     * @param state 상태값 (선택사항)
     * @return 인증 URL
     */
    public String generateAuthUrl(String redirectUri, String scope, String state) {
        StringBuilder authUrl = new StringBuilder();
        authUrl.append("https://kauth.kakao.com/oauth/authorize");
        authUrl.append("?client_id=").append(clientId);
        authUrl.append("&redirect_uri=").append(redirectUri);
        authUrl.append("&response_type=code");
        
        if (scope != null && !scope.trim().isEmpty()) {
            authUrl.append("&scope=").append(scope);
        }
        
        if (state != null && !state.trim().isEmpty()) {
            authUrl.append("&state=").append(state);
        }
        
        return authUrl.toString();
    }
}
