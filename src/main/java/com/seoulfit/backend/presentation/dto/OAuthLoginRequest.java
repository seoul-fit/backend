package com.seoulfit.backend.presentation.dto;

import com.seoulfit.backend.domain.OAuthProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "OAuth 로그인 요청")
@Getter
@NoArgsConstructor
public class OAuthLoginRequest {

    @Schema(description = "OAuth 프로바이더", example = "KAKAO")
    @NotNull(message = "OAuth 프로바이더는 필수입니다.")
    private OAuthProvider provider;

    @Schema(description = "OAuth 유저 식별 값", example = "123123")
    @NotBlank(message = "OAuth 유저 식별 값은 필수입니다.")
    private String oauthUserId;

    @Builder
    public OAuthLoginRequest(OAuthProvider provider, String oauthUserId) {
        this.provider = provider;
        this.oauthUserId = oauthUserId;
    }
}
