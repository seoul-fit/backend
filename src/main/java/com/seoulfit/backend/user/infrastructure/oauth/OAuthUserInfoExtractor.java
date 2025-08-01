package com.seoulfit.backend.user.infrastructure.oauth;

import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.infrastructure.dto.OAuthUserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

public abstract class OAuthUserInfoExtractor {

    protected AuthProvider provider;

    public OAuthUserInfoExtractor(AuthProvider provider) {
        this.provider = provider;
    }

    public abstract OAuthUserInfo extractUserInfo(OAuth2User oAuth2User);

    public AuthProvider getProvider() {
        return provider;
    }
}
