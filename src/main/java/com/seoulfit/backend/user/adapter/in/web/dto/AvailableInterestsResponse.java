package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.user.domain.InterestCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 선택 가능한 관심사 목록 응답 DTO
 * 
 * 사용자가 체크박스로 선택할 수 있는 모든 관심사 카테고리를 반환
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Schema(description = "선택 가능한 관심사 목록 응답")
@Getter
@Builder
public class AvailableInterestsResponse {

    @Schema(description = "선택 가능한 관심사 목록")
    private final List<InterestCategoryInfo> categories;

    @Schema(description = "총 카테고리 개수", example = "11")
    private final Integer totalCount;

    /**
     * 관심사 카테고리 정보
     */
    @Schema(description = "관심사 카테고리 정보")
    @Getter
    @Builder
    public static class InterestCategoryInfo {
        
        @Schema(description = "카테고리 코드", example = "RESTAURANTS")
        private final String code;

        @Schema(description = "표시명", example = "맛집")
        private final String displayName;

        @Schema(description = "설명", example = "서울시 관광 음식점, 맛집 정보 및 추천")
        private final String description;

        @Schema(description = "이모지", example = "🍽️")
        private final String emoji;

        @Schema(description = "이모지 포함 표시명", example = "🍽️ 맛집")
        private final String displayNameWithEmoji;

        @Schema(description = "위치 기반 서비스 여부", example = "true")
        private final Boolean isLocationBased;

        @Schema(description = "실시간 알림 필요 여부", example = "false")
        private final Boolean isRealtimeNotificationRequired;

        @Schema(description = "추천 여부", example = "true")
        private final Boolean isRecommended;

        public static InterestCategoryInfo from(InterestCategory category) {
            return InterestCategoryInfo.builder()
                    .code(category.name())
                    .displayName(category.getDisplayName())
                    .description(category.getDescription())
                    .emoji(category.getEmoji())
                    .displayNameWithEmoji(category.getDisplayNameWithEmoji())
                    .isLocationBased(category.isLocationBased())
                    .isRealtimeNotificationRequired(category.isRealtimeNotificationRequired())
                    .isRecommended(isRecommendedCategory(category))
                    .build();
        }

        /**
         * 추천 카테고리인지 확인
         */
        private static boolean isRecommendedCategory(InterestCategory category) {
            // 위치 기반 서비스나 실시간 알림이 필요한 카테고리를 추천
            return category.isLocationBased() || category.isRealtimeNotificationRequired();
        }
    }

    /**
     * 모든 관심사 카테고리 응답 생성
     */
    public static AvailableInterestsResponse createAll() {
        List<InterestCategoryInfo> categories = Arrays.stream(InterestCategory.values())
                .map(InterestCategoryInfo::from)
                .toList();

        return AvailableInterestsResponse.builder()
                .categories(categories)
                .totalCount(categories.size())
                .build();
    }

    /**
     * 위치 기반 서비스 카테고리만 응답 생성
     */
    public static AvailableInterestsResponse createLocationBased() {
        List<InterestCategoryInfo> categories = Arrays.stream(InterestCategory.values())
                .filter(InterestCategory::isLocationBased)
                .map(InterestCategoryInfo::from)
                .toList();

        return AvailableInterestsResponse.builder()
                .categories(categories)
                .totalCount(categories.size())
                .build();
    }

    /**
     * 실시간 알림 카테고리만 응답 생성
     */
    public static AvailableInterestsResponse createRealtimeNotification() {
        List<InterestCategoryInfo> categories = Arrays.stream(InterestCategory.values())
                .filter(InterestCategory::isRealtimeNotificationRequired)
                .map(InterestCategoryInfo::from)
                .toList();

        return AvailableInterestsResponse.builder()
                .categories(categories)
                .totalCount(categories.size())
                .build();
    }
}
