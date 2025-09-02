package com.seoulfit.backend.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 공통 API 응답 모델
 * 
 * @param <T> 응답 데이터 타입
 * @author Seoul Fit
 * @since 1.0.0
 */
@Schema(description = "API 공통 응답 모델")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @Schema(description = "응답 상태 코드", example = "200")
    private int status;

    @Schema(description = "응답 메시지", example = "Success")
    private String message;

    @Schema(description = "응답 데이터")
    private T data;

    @Schema(description = "응답 시간", example = "2025-08-28T17:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "요청 경로", example = "/api/v1/cultural-events")
    private String path;

    @Schema(description = "페이지네이션 정보")
    private PageInfo pageInfo;

    /**
     * 성공 응답 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                200,
                "Success",
                data,
                LocalDateTime.now(),
                null,
                null
        );
    }

    /**
     * 성공 응답 생성 (데이터 없음)
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(
                200,
                "Success",
                null,
                LocalDateTime.now(),
                null,
                null
        );
    }

    /**
     * 성공 응답 생성 (커스텀 메시지)
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(
                200,
                message,
                data,
                LocalDateTime.now(),
                null,
                null
        );
    }

    /**
     * 페이지네이션 지원 성공 응답 생성
     */
    public static <T> ApiResponse<List<T>> success(Page<T> page) {
        return new ApiResponse<>(
                200,
                "Success",
                page.getContent(),
                LocalDateTime.now(),
                null,
                PageInfo.from(page)
        );
    }

    /**
     * 페이지네이션 지원 성공 응답 생성 (커스텀 메시지)
     */
    public static <T> ApiResponse<List<T>> success(String message, Page<T> page) {
        return new ApiResponse<>(
                200,
                message,
                page.getContent(),
                LocalDateTime.now(),
                null,
                PageInfo.from(page)
        );
    }

    /**
     * 생성된 리소스 응답 (201)
     */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(
                201,
                "Created",
                data,
                LocalDateTime.now(),
                null,
                null
        );
    }

    /**
     * 에러 응답 생성
     */
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(
                status,
                message,
                null,
                LocalDateTime.now(),
                null,
                null
        );
    }

    /**
     * 에러 응답 생성 (경로 포함)
     */
    public static <T> ApiResponse<T> error(int status, String message, String path) {
        return new ApiResponse<>(
                status,
                message,
                null,
                LocalDateTime.now(),
                path,
                null
        );
    }

    /**
     * 페이지네이션 정보 클래스
     */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "페이지네이션 정보")
    public static class PageInfo {
        
        @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
        private int page;
        
        @Schema(description = "페이지 크기", example = "20")
        private int size;
        
        @Schema(description = "전체 요소 수", example = "150")
        private long totalElements;
        
        @Schema(description = "전체 페이지 수", example = "8")
        private int totalPages;
        
        @Schema(description = "첫 번째 페이지 여부", example = "true")
        private boolean first;
        
        @Schema(description = "마지막 페이지 여부", example = "false")
        private boolean last;
        
        @Schema(description = "다음 페이지 존재 여부", example = "true")
        private boolean hasNext;
        
        @Schema(description = "이전 페이지 존재 여부", example = "false")
        private boolean hasPrevious;
        
        public static PageInfo from(Page<?> page) {
            return PageInfo.builder()
                    .page(page.getNumber())
                    .size(page.getSize())
                    .totalElements(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .first(page.isFirst())
                    .last(page.isLast())
                    .hasNext(page.hasNext())
                    .hasPrevious(page.hasPrevious())
                    .build();
        }
    }
}