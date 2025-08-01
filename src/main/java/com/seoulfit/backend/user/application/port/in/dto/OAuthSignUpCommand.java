package com.seoulfit.backend.user.application.port.in.dto;

import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.InterestCategory;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * OAuth 회원가입 명령
 * <p>
 * 헥사고날 아키텍처의 Command 객체
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Getter
@Builder
public class OAuthSignUpCommand {

    private final AuthProvider provider;
    private final String oauthUserId;
    private final String nickname;
    private final String email;
    private final String profileImageUrl;
    private final List<InterestCategory> interests;

    /**
     * OAuth 회원가입 명령 생성
     *
     * @param provider        OAuth 프로바이더
     * @param oauthUserId     OAuth 사용자 ID
     * @param nickname        닉네임
     * @param email           이메일
     * @param profileImageUrl 프로필 이미지 URL
     * @param interests       관심사 목록
     * @return OAuth 회원가입 명령
     */
    public static OAuthSignUpCommand of(AuthProvider provider, String oauthUserId,
            String nickname, String email, String profileImageUrl,
            List<InterestCategory> interests) {
        return OAuthSignUpCommand.builder()
                .provider(provider)
                .oauthUserId(oauthUserId)
                .nickname(nickname)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .interests(interests != null ? interests : List.of())
                .build();
    }
}
