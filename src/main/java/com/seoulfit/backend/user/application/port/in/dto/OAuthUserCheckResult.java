package com.seoulfit.backend.user.application.port.in.dto;

import com.seoulfit.backend.user.domain.InterestCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * OAuth 사용자 확인 결과를 담는 DTO
 */
@Getter
@Builder
public class OAuthUserCheckResult {
    private final boolean exists;
    private final Long userId;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;
    private final List<InterestCategory> interests;
    private final String message;
    
    public static OAuthUserCheckResult exists(Long userId) {
        return OAuthUserCheckResult.builder()
                .exists(true)
                .userId(userId)
                .interests(List.of())
                .message("사용자가 존재합니다.")
                .build();
    }
    
    public static OAuthUserCheckResult exists(Long userId, String email, String nickname, String profileImageUrl, List<InterestCategory> interests) {
        return OAuthUserCheckResult.builder()
                .exists(true)
                .userId(userId)
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .interests(interests)
                .message("사용자가 존재합니다.")
                .build();
    }
    
    public static OAuthUserCheckResult notExists() {
        return OAuthUserCheckResult.builder()
                .exists(false)
                .interests(List.of())
                .message("사용자가 존재하지 않습니다.")
                .build();
    }
    
    // 기존 코드와의 호환성을 위한 별칭 메서드들
    public boolean isExists() {
        return exists;
    }
}
