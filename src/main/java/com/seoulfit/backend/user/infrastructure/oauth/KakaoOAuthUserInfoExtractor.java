package com.seoulfit.backend.user.infrastructure.oauth;

import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.infrastructure.dto.OAuthUserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KakaoOAuthUserInfoExtractor extends OAuthUserInfoExtractor {

    public KakaoOAuthUserInfoExtractor() {
        super(AuthProvider.KAKAO);
    }

    @Override
    public OAuthUserInfo extractUserInfo(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String oauthId = String.valueOf(attributes.get("id"));
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");
        String profileImageUrl = (String) profile.get("profile_image_url");

        return OAuthUserInfo.builder()
                .oauthId(oauthId)
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .provider(AuthProvider.KAKAO)
                .build();
    }
}
