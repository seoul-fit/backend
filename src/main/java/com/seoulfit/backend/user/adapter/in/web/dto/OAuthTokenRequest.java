package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.user.domain.AuthProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OAuth 토큰 요청 DTO
 * Authorization Code를 Access Token으로 교환하기 위한 요청
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Getter
@NoArgsConstructor
public class OAuthTokenRequest {

    @NotNull(message = "OAuth 프로바이더는 필수입니다.")
    private AuthProvider provider;

    @NotBlank(message = "권한부여 승인코드는 필수입니다.")
    private String authorizationCode;

    @NotBlank(message = "리다이렉트 URI는 필수입니다.")
    private String redirectUri;

    @NotBlank(message = "클라이언트 ID는 필수입니다.")
    private String clientId;

    @NotBlank(message = "클라이언트 시크릿은 필수입니다.")
    private String clientSecret;
}
