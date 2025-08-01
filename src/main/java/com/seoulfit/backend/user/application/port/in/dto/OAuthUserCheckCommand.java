package com.seoulfit.backend.user.application.port.in.dto;

import com.seoulfit.backend.user.domain.AuthProvider;
import lombok.Builder;
import lombok.Getter;

/**
 * OAuth 사용자 확인 명령
 */
@Getter
@Builder
public class OAuthUserCheckCommand {
    private final AuthProvider provider;
    private final String oauthUserId;
    
    public static OAuthUserCheckCommand of(AuthProvider provider, String oauthUserId) {
        return OAuthUserCheckCommand.builder()
                .provider(provider)
                .oauthUserId(oauthUserId)
                .build();
    }
}
