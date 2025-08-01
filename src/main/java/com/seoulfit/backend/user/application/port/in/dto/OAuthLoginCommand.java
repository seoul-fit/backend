package com.seoulfit.backend.user.application.port.in.dto;

import com.seoulfit.backend.user.domain.AuthProvider;
import lombok.Builder;
import lombok.Getter;

/**
 * OAuth 로그인 명령
 * <p>
 * 헥사고날 아키텍처의 Command 객체
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Getter
@Builder
public class OAuthLoginCommand {

    private final AuthProvider provider;
    private final String oauthUserId;

    /**
     * OAuth 로그인 명령 생성
     *
     * @param provider    OAuth 프로바이더
     * @param oauthUserId OAuth 사용자 ID
     * @return OAuth 로그인 명령
     */
    public static OAuthLoginCommand of(AuthProvider provider, String oauthUserId) {
        return OAuthLoginCommand.builder()
                .provider(provider)
                .oauthUserId(oauthUserId)
                .build();
    }
}
