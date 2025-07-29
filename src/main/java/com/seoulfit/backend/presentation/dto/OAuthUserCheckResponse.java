package com.seoulfit.backend.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "OAuth 사용자 확인 응답")
@Getter
@Builder
public class OAuthUserCheckResponse {

    @Schema(description = "사용자 존재 여부", example = "true")
    private boolean exists;

    @Schema(description = "사용자 정보 (존재하는 경우)")
    private UserResponse user;
}
