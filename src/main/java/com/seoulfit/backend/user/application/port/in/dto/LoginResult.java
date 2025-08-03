package com.seoulfit.backend.user.application.port.in.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 결과
 * <p>
 * 헥사고날 아키텍처의 Result 객체
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Getter
@Builder
public class LoginResult {

    private final Long authUserId;
    private final Long userId;
    private final String email;
    private final String nickname;
    private final String accessToken;
    private final String refreshToken;

    /**
     * 로그인 결과 생성
     *
     * @param authUserId   인증 사용자 ID
     * @param userId       사용자 ID
     * @param email        이메일
     * @param nickname     닉네임
     * @param accessToken  액세스 토큰
     * @param refreshToken 리프레시 토큰
     * @return 로그인 결과
     */
    public static LoginResult of(Long authUserId, Long userId, String email, String nickname,
                                String accessToken, String refreshToken) {
        return LoginResult.builder()
                .authUserId(authUserId)
                .userId(userId)
                .email(email)
                .nickname(nickname)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
