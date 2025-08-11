package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.user.domain.AuthProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OAuth 로그인 요청 DTO
 * Authorization Code Flow를 위한 승인 코드 기반 로그인
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
@NoArgsConstructor
public class OAuthLoginRequest {

    @NotNull(message = "OAuth 프로바이더는 필수입니다.")
    private AuthProvider provider;

    @NotBlank(message = "권한부여 승인코드는 필수입니다.")
    private String authorizationCode;

    @NotBlank(message = "리다이렉트 URI는 필수입니다.")
    private String redirectUri;

    // 기존 방식과의 호환성을 위해 유지 (deprecated)
    @Deprecated
    private String oauthUserId;
}
