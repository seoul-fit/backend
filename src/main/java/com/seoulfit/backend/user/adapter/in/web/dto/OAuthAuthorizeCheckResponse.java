package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.user.application.port.in.dto.OAuthAuthorizeCheckResult;
import com.seoulfit.backend.user.domain.AuthProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * OAuth 인가코드 검증 응답 DTO
 * 인가코드로 조회한 사용자 정보를 프론트엔드로 반환
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Schema(description = "OAuth 인가코드 검증 응답")
@Getter
@Builder
public class OAuthAuthorizeCheckResponse {

    @Schema(description = "OAuth 제공자", example = "KAKAO")
    private final AuthProvider provider;

    @Schema(description = "OAuth 사용자 ID", example = "123456789")
    private final String oauthUserId;

    @Schema(description = "닉네임", example = "홍길동")
    private final String nickname;

    @Schema(description = "이메일", example = "user@example.com")
    private final String email;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private final String profileImageUrl;

    /**
     * OAuthAuthorizeCheckResult로부터 응답 DTO 생성
     *
     * @param result OAuth 인가코드 검증 결과
     * @return OAuth 인가코드 검증 응답 DTO
     */
    public static OAuthAuthorizeCheckResponse from(OAuthAuthorizeCheckResult result) {
        return OAuthAuthorizeCheckResponse.builder()
                .provider(result.getProvider())
                .oauthUserId(result.getOauthUserId())
                .nickname(result.getNickname())
                .email(result.getEmail())
                .profileImageUrl(result.getProfileImageUrl())
                .build();
    }
}
