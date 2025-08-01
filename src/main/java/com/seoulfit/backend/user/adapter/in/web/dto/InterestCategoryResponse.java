package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.user.domain.InterestCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * 관심사 카테고리 응답 DTO Hexagonal Architecture의 인바운드 어댑터 DTO
 */
@Schema(description = "관심사 카테고리 정보")
@Getter
@Builder
public class InterestCategoryResponse {

    @Schema(description = "관심사 코드", example = "WEATHER")
    private final String code;

    @Schema(description = "관심사 표시명", example = "날씨")
    private final String displayName;

    @Schema(description = "관심사 설명", example = "기상정보, 폭염, 한파, 미세먼지 등 날씨 관련 알림")
    private final String description;

    /**
     * InterestCategory로부터 응답 DTO 생성
     *
     * @param category 관심사 카테고리
     * @return 관심사 카테고리 응답 DTO
     */
    public static InterestCategoryResponse from(InterestCategory category) {
        return InterestCategoryResponse.builder()
                .code(category.name())
                .displayName(category.getDisplayName())
                .description(category.getDescription())
                .build();
    }

    /**
     * 모든 관심사 카테고리 목록 조회
     *
     * @return 관심사 카테고리 응답 DTO 목록
     */
    public static List<InterestCategoryResponse> getAllCategories() {
        return Arrays.stream(InterestCategory.values())
                .map(InterestCategoryResponse::from)
                .toList();
    }
}
