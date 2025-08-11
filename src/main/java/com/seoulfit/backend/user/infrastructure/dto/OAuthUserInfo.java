package com.seoulfit.backend.user.infrastructure.dto;

import com.seoulfit.backend.user.domain.AuthProvider;
import lombok.Builder;
import lombok.Getter;

/**
 * OAuth 사용자 정보 DTO
 * OAuth 제공자로부터 받은 사용자 정보를 담는 객체
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
@Builder
public class OAuthUserInfo {

    private String id;
    private String oauthId; // 기존 호환성을 위해 유지
    private String email;
    private String nickname;
    private String profileImageUrl;
    private AuthProvider provider;

    /**
     * OAuth ID 반환 (id 우선, 없으면 oauthId)
     */
    public String getOAuthId() {
        return id != null ? id : oauthId;
    }
}
