package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.user.application.port.in.dto.OAuthUserCheckResult;
import com.seoulfit.backend.user.domain.InterestCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * OAuth 사용자 확인 응답 DTO Hexagonal Architecture의 인바운드 어댑터 DTO
 */
@Schema(description = "OAuth 사용자 확인 응답")
@Getter
@Builder
public class OAuthUserCheckResponse {

    @Schema(description = "사용자 존재 여부", example = "true")
    private final boolean exists;

    @Schema(description = "사용자 ID", example = "1")
    private final Long userId;

    @Schema(description = "이메일", example = "user@example.com")
    private final String email;

    @Schema(description = "닉네임", example = "홍길동")
    private final String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private final String profileImageUrl;

    @Schema(description = "관심사 목록", example = "[\"WEATHER\", \"CULTURE\", \"TRAFFIC\"]")
    private final List<InterestCategory> interests;

    /**
     * OAuthUserCheckResult로부터 응답 DTO 생성
     *
     * @param result OAuth 사용자 확인 결과
     * @return OAuth 사용자 확인 응답 DTO
     */
    public static OAuthUserCheckResponse from(OAuthUserCheckResult result) {
        return OAuthUserCheckResponse.builder()
                .exists(result.isExists())
                .userId(result.getUserId())
                .email(result.getEmail())
                .nickname(result.getNickname())
                .profileImageUrl(result.getProfileImageUrl())
                .interests(result.getInterests())
                .build();
    }
}
