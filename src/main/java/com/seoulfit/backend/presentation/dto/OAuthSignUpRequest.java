package com.seoulfit.backend.presentation.dto;

import com.seoulfit.backend.domain.OAuthProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "OAuth 회원가입 요청")
@Getter
@NoArgsConstructor
public class OAuthSignUpRequest {

    @Schema(description = "OAuth 프로바이더", example = "KAKAO")
    @NotNull(message = "OAuth 프로바이더는 필수입니다.")
    private OAuthProvider provider;

    @Schema(description = "OAuth 유저 식별 값", example = "123123")
    @NotBlank(message = "OAuth 유저 식별 값은 필수입니다.")
    private String oauthUserId;

    @Schema(description = "닉네임", example = "홍길동")
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다.")
    private String nickname;

    @Schema(description = "이메일", example = "user@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Builder
    public OAuthSignUpRequest(OAuthProvider provider, String oauthUserId, String nickname, 
                             String email, String profileImageUrl) {
        this.provider = provider;
        this.oauthUserId = oauthUserId;
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }
}
