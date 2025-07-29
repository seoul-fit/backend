package com.seoulfit.backend.application.oauth;

import com.seoulfit.backend.application.dto.OAuthUserInfo;
import com.seoulfit.backend.domain.OAuthProvider;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NaverOAuthUserInfoExtractor extends OAuthUserInfoExtractor {

    public NaverOAuthUserInfoExtractor() {
        super(OAuthProvider.NAVER);
    }

    @Override
    public OAuthUserInfo extractUserInfo(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        String oauthId = (String) response.get("id");
        String email = (String) response.get("email");
        String nickname = (String) response.get("nickname");
        String profileImageUrl = (String) response.get("profile_image");

        return OAuthUserInfo.builder()
                .oauthId(oauthId)
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .provider(OAuthProvider.NAVER)
                .build();
    }
}
