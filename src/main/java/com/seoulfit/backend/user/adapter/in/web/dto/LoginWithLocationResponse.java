package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.trigger.adapter.in.web.dto.TriggerEvaluationResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 위치 정보를 포함한 로그인 응답 DTO
 * 
 * 로그인 성공과 함께 트리거 평가 결과를 반환
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Schema(description = "위치 정보를 포함한 로그인 응답")
@Getter
@Builder
public class LoginWithLocationResponse {

    @Schema(description = "로그인 성공 여부", example = "true")
    private final boolean loginSuccess;

    @Schema(description = "사용자 ID", example = "1")
    private final Long userId;

    @Schema(description = "사용자 닉네임", example = "홍길동")
    private final String nickname;

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private final String email;

    @Schema(description = "액세스 토큰")
    private final String accessToken;

    @Schema(description = "리프레시 토큰")
    private final String refreshToken;

    @Schema(description = "트리거 평가 결과")
    private final TriggerEvaluationResponse triggerEvaluation;

    @Schema(description = "로그인 메시지", example = "로그인 성공")
    private final String message;

    /**
     * 로그인 성공 응답 생성
     */
    public static LoginWithLocationResponse success(Long userId, String nickname, String email,
                                                   String accessToken, String refreshToken,
                                                   TriggerEvaluationResponse triggerEvaluation) {
        return LoginWithLocationResponse.builder()
                .loginSuccess(true)
                .userId(userId)
                .nickname(nickname)
                .email(email)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .triggerEvaluation(triggerEvaluation)
                .message("로그인 성공")
                .build();
    }

    /**
     * 로그인 실패 응답 생성
     */
    public static LoginWithLocationResponse failure(String message) {
        return LoginWithLocationResponse.builder()
                .loginSuccess(false)
                .message(message)
                .build();
    }
}
