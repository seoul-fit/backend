package com.seoulfit.backend.user.application.port.in.dto;

import com.seoulfit.backend.user.domain.AuthProvider;
import lombok.Builder;
import lombok.Getter;

/**
 * OAuth 권한부여 승인코드 기반 인증 명령
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
@Builder
public class OAuthAuthorizationCommand {

    private final AuthProvider provider;
    private final String authorizationCode;
    private final String redirectUri;

    public static OAuthAuthorizationCommand of(AuthProvider provider, String authorizationCode, String redirectUri) {
        return OAuthAuthorizationCommand.builder()
                .provider(provider)
                .authorizationCode(authorizationCode)
                .redirectUri(redirectUri)
                .build();
    }
}
