package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.user.domain.AuthProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OAuth 로그인 요청 DTO
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Getter
@NoArgsConstructor
public class OAuthLoginRequest {

    @NotNull(message = "OAuth 프로바이더는 필수입니다.")
    private AuthProvider provider;

    @NotBlank(message = "OAuth 사용자 ID는 필수입니다.")
    private String oauthUserId;
}
