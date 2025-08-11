package com.seoulfit.backend.user.application.port.in.dto;

import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 결과
 * <p>
 * 헥사고날 아키텍처의 Result 객체
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
@Builder
public class UserResult {

    private final Long id;
    private final Long authUserId;
    private final String nickname;
    private final String profileImageUrl;
    private final Double locationLatitude;
    private final Double locationLongitude;
    private final String locationAddress;
    private final UserStatus status;
    private final List<InterestCategory> interests;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /**
     * User 도메인으로부터 결과 생성
     *
     * @param user 사용자 도메인
     * @return 사용자 결과
     */
    public static UserResult from(User user) {
        return UserResult.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .locationLatitude(user.getLocationLatitude())
                .locationLongitude(user.getLocationLongitude())
                .locationAddress(user.getLocationAddress())
                .status(user.getStatus())
                .interests(user.getInterestCategories())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
