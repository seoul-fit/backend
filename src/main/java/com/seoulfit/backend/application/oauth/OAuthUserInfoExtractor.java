package com.seoulfit.backend.application.oauth;

import com.seoulfit.backend.application.dto.OAuthUserInfo;
import com.seoulfit.backend.domain.OAuthProvider;
import org.springframework.security.oauth2.core.user.OAuth2User;

public abstract class OAuthUserInfoExtractor {
    
    protected OAuthProvider provider;
    
    public OAuthUserInfoExtractor(OAuthProvider provider) {
        this.provider = provider;
    }
    
    public abstract OAuthUserInfo extractUserInfo(OAuth2User oAuth2User);
    
    public OAuthProvider getProvider() {
        return provider;
    }
}
