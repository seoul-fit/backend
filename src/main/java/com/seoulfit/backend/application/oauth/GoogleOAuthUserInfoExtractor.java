package com.seoulfit.backend.application.oauth;

import com.seoulfit.backend.application.dto.OAuthUserInfo;
import com.seoulfit.backend.domain.OAuthProvider;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GoogleOAuthUserInfoExtractor extends OAuthUserInfoExtractor {

    public GoogleOAuthUserInfoExtractor() {
        super(OAuthProvider.GOOGLE);
    }

    @Override
    public OAuthUserInfo extractUserInfo(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String oauthId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String nickname = (String) attributes.get("name");
        String profileImageUrl = (String) attributes.get("picture");

        return OAuthUserInfo.builder()
                .oauthId(oauthId)
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .provider(OAuthProvider.GOOGLE)
                .build();
    }
}
