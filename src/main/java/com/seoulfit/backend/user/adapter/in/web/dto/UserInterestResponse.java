package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.user.domain.InterestCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 관심사 응답 DTO
 * 
 * 사용자의 관심사 정보를 클라이언트에 반환하기 위한 응답 객체
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Schema(description = "사용자 관심사 응답")
@Getter
@Builder
public class UserInterestResponse {

    @Schema(description = "사용자 ID", example = "1")
    private final Long userId;

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private final String userEmail;

    @Schema(description = "선택된 관심사 목록")
    private final List<InterestInfo> interests;

    @Schema(description = "총 관심사 개수", example = "5")
    private final Integer totalCount;

    @Schema(description = "마지막 업데이트 시간")
    private final LocalDateTime lastUpdated;

    @Schema(description = "설정 완료 여부", example = "true")
    private final Boolean isCompleted;

    /**
     * 관심사 정보
     */
    @Schema(description = "관심사 정보")
    @Getter
    @Builder
    public static class InterestInfo {
        
        @Schema(description = "관심사 카테고리", example = "RESTAURANTS")
        private final InterestCategory category;

        @Schema(description = "표시명", example = "맛집")
        private final String displayName;

        @Schema(description = "설명", example = "서울시 관광 음식점, 맛집 정보 및 추천")
        private final String description;

        @Schema(description = "이모지", example = "🍽️")
        private final String emoji;

        @Schema(description = "위치 기반 서비스 여부", example = "true")
        private final Boolean isLocationBased;

        @Schema(description = "실시간 알림 필요 여부", example = "false")
        private final Boolean isRealtimeNotificationRequired;

        public static InterestInfo from(InterestCategory category) {
            return InterestInfo.builder()
                    .category(category)
                    .displayName(category.getDisplayName())
                    .description(category.getDescription())
                    .emoji(category.getEmoji())
                    .isLocationBased(category.isLocationBased())
                    .isRealtimeNotificationRequired(category.isRealtimeNotificationRequired())
                    .build();
        }
    }

    /**
     * 관심사 목록으로부터 응답 생성
     */
    public static UserInterestResponse from(Long userId, String userEmail, 
                                          List<InterestCategory> interests) {
        return UserInterestResponse.builder()
                .userId(userId)
                .userEmail(userEmail)
                .interests(interests.stream()
                        .map(InterestInfo::from)
                        .toList())
                .totalCount(interests.size())
                .lastUpdated(LocalDateTime.now())
                .isCompleted(interests.size() > 0)
                .build();
    }
}
