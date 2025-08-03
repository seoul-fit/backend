package com.seoulfit.backend.user.application.port.in.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 토큰 결과
 * <p>
 * 헥사고날 아키텍처의 Result 객체
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Getter
@Builder
public class TokenResult {

    private final String accessToken;
    private final String refreshToken;
    private final Long expiresIn;
    private final Long userId;
    private final String nickname;

    /**
     * 토큰 결과 생성
     *
     * @param accessToken  액세스 토큰
     * @param refreshToken 리프레시 토큰
     * @param expiresIn    만료 시간 (초)
     * @return 토큰 결과
     */
    public static TokenResult of(String accessToken, String refreshToken, Long expiresIn) {
        return TokenResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .build();
    }

    /**
     * 사용자 정보를 포함한 토큰 결과 생성
     *
     * @param accessToken  액세스 토큰
     * @param refreshToken 리프레시 토큰
     * @param userId       사용자 ID
     * @param nickname     사용자 닉네임
     * @return 토큰 결과
     */
    public static TokenResult of(String accessToken, String refreshToken, Long userId, String nickname) {
        return TokenResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600L) // 기본 1시간
                .userId(userId)
                .nickname(nickname)
                .build();
    }
}
