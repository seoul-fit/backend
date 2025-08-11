package com.seoulfit.backend.user.application.port.in.dto;

import com.seoulfit.backend.user.domain.AuthProvider;
import lombok.Builder;
import lombok.Getter;

/**
 * OAuth 인가코드 검증 결과
 * 인가코드로 조회한 사용자 정보를 담는 결과 객체
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
@Builder
public class OAuthAuthorizeCheckResult {

    private final AuthProvider provider;
    private final String oauthUserId;
    private final String nickname;
    private final String email;
    private final String profileImageUrl;

    /**
     * OAuth 인가코드 검증 결과 생성
     *
     * @param provider OAuth 제공자
     * @param oauthUserId OAuth 사용자 ID
     * @param nickname 닉네임
     * @param email 이메일
     * @param profileImageUrl 프로필 이미지 URL
     * @return OAuth 인가코드 검증 결과
     */
    public static OAuthAuthorizeCheckResult of(AuthProvider provider, String oauthUserId, String nickname, String email, String profileImageUrl) {
        return OAuthAuthorizeCheckResult.builder()
                .provider(provider)
                .oauthUserId(oauthUserId)
                .nickname(nickname)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
