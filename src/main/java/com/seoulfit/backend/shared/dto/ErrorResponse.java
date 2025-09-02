package com.seoulfit.backend.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 에러 응답 모델
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Schema(description = "에러 응답 모델")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "VALIDATION_FAILED")
    private String code;

    @Schema(description = "에러 메시지", example = "입력값이 유효하지 않습니다.")
    private String message;

    @Schema(description = "HTTP 상태 코드", example = "400")
    private int status;

    @Schema(description = "에러 발생 시간", example = "2025-08-28T17:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "요청 경로", example = "/api/v1/users")
    private String path;

    @Schema(description = "상세 에러 목록")
    private List<FieldError> errors;

    /**
     * 필드 에러 정보
     */
    @Schema(description = "필드 에러 정보")
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FieldError {
        @Schema(description = "필드명", example = "email")
        private String field;

        @Schema(description = "입력값", example = "invalid-email")
        private String value;

        @Schema(description = "에러 메시지", example = "올바른 이메일 형식이 아닙니다.")
        private String message;
    }

    /**
     * 일반 에러 응답 생성
     */
    public static ErrorResponse of(String code, String message, int status, String path) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .status(status)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    /**
     * 필드 에러 응답 생성
     */
    public static ErrorResponse of(String code, String message, int status, String path, List<FieldError> errors) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .status(status)
                .timestamp(LocalDateTime.now())
                .path(path)
                .errors(errors)
                .build();
    }

    /**
     * 인증 에러
     */
    public static ErrorResponse unauthorized(String path) {
        return of("UNAUTHORIZED", "인증이 필요합니다.", 401, path);
    }

    /**
     * 권한 에러
     */
    public static ErrorResponse forbidden(String path) {
        return of("FORBIDDEN", "권한이 없습니다.", 403, path);
    }

    /**
     * 리소스 없음 에러
     */
    public static ErrorResponse notFound(String path) {
        return of("NOT_FOUND", "요청한 리소스를 찾을 수 없습니다.", 404, path);
    }

    /**
     * 서버 에러
     */
    public static ErrorResponse internalServerError(String path) {
        return of("INTERNAL_SERVER_ERROR", "서버에서 오류가 발생했습니다.", 500, path);
    }
}