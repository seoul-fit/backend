package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.user.application.port.in.dto.TokenResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 토큰 응답 DTO Hexagonal Architecture의 인바운드 어댑터 DTO
 */
@Schema(description = "토큰 응답")
@Getter
@Builder
public class TokenResponse {

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private final String accessToken;

    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private final String refreshToken;

    @Schema(description = "사용자 정보")
    private final UserResponse user;

    /**
     * TokenResult로부터 응답 DTO 생성
     *
     * @param result 토큰 결과
     * @return 토큰 응답 DTO
     */
    public static TokenResponse from(TokenResult result) {
        UserResponse userResponse = UserResponse.builder()
                .id(result.getUserId())
                .nickname(result.getNickname())
                .build();

        return TokenResponse.builder()
                .accessToken(result.getAccessToken())
                .refreshToken(result.getRefreshToken())
                .user(userResponse)
                .build();
    }
}
