package com.seoulfit.backend.user.infrastructure.oauth;

import com.seoulfit.backend.user.domain.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * OAuth 클라이언트 팩토리
 * OAuth 제공자에 따라 적절한 클라이언트를 반환
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class OAuthClientFactory {

    private final List<OAuthClient> oAuthClients;

    /**
     * OAuth 제공자에 맞는 클라이언트 반환
     *
     * @param provider OAuth 제공자
     * @return OAuth 클라이언트
     * @throws IllegalArgumentException 지원하지 않는 제공자인 경우
     */
    public OAuthClient getClient(AuthProvider provider) {
        return oAuthClients.stream()
                .filter(client -> client.supports(provider))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + provider));
    }
}
