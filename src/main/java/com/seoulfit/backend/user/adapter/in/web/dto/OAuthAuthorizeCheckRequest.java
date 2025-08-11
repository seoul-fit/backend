package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.user.domain.AuthProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OAuth 인가코드 검증 요청 DTO
 * 프론트엔드에서 받은 인가코드로 사용자 정보를 조회하기 위한 요청
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Schema(description = "OAuth 인가코드 검증 요청")
@Getter
@NoArgsConstructor
public class OAuthAuthorizeCheckRequest {

    @Schema(description = "OAuth 제공자", example = "KAKAO", required = true)
    @NotNull(message = "OAuth 프로바이더는 필수입니다.")
    private AuthProvider provider;

    @Schema(description = "권한부여 승인코드", example = "abc123def456", required = true)
    @NotBlank(message = "권한부여 승인코드는 필수입니다.")
    private String authorizationCode;

    @Schema(description = "리다이렉트 URI", example = "http://localhost:3000/auth/callback", required = true)
    @NotBlank(message = "리다이렉트 URI는 필수입니다.")
    private String redirectUri;

    public OAuthAuthorizeCheckRequest(AuthProvider provider, String authorizationCode, String redirectUri) {
        this.provider = provider;
        this.authorizationCode = authorizationCode;
        this.redirectUri = redirectUri;
    }
}
