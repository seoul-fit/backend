package com.seoulfit.backend.user.infrastructure.oauth;

import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.infrastructure.dto.OAuthUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Kakao OAuth 사용자 정보 추출")
class KakaoOAuthUserInfoExtractorTest {

    private final KakaoOAuthUserInfoExtractor extractor = new KakaoOAuthUserInfoExtractor();

    @Test
    @DisplayName("account_email 없이 프로필을 추출한다")
    void extractUserInfo_WithoutEmail() {
        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(() -> "ROLE_USER"),
                Map.of(
                        "id", 12345L,
                        "kakao_account", Map.of(
                                "profile", Map.of(
                                        "nickname", "테스트사용자",
                                        "profile_image_url", "https://example.com/profile.jpg"
                                )
                        )
                ),
                "id"
        );

        OAuthUserInfo userInfo = extractor.extractUserInfo(oAuth2User);

        assertThat(userInfo.getProvider()).isEqualTo(AuthProvider.KAKAO);
        assertThat(userInfo.getOAuthId()).isEqualTo("12345");
        assertThat(userInfo.getEmail()).isNull();
        assertThat(userInfo.getNickname()).isEqualTo("테스트사용자");
        assertThat(userInfo.getProfileImageUrl()).isEqualTo("https://example.com/profile.jpg");
    }
}
