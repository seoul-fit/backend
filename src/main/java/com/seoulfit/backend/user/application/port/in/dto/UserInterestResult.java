package com.seoulfit.backend.user.application.port.in.dto;

import com.seoulfit.backend.user.domain.InterestCategory;
import java.util.List;

/**
 * 사용자 관심사 결과를 담는 DTO
 */
public record UserInterestResult(
        Long userId,
        String nickname,
        List<String> interestCategories,
        String message
) {

    public static UserInterestResult success(Long userId, List<String> interestCategories) {
        return new UserInterestResult(userId, null, interestCategories, "관심사 조회/업데이트가 완료되었습니다.");
    }

    public static UserInterestResult empty(Long userId) {
        return new UserInterestResult(userId, null, List.of(), "등록된 관심사가 없습니다.");
    }

    /**
     * 사용자 정보와 관심사를 포함한 결과 생성
     */
    public static UserInterestResult of(Long userId, String nickname, List<InterestCategory> interests) {
        List<String> interestNames = interests.stream()
                .map(InterestCategory::name)
                .toList();
        return new UserInterestResult(userId, nickname, interestNames, "관심사 조회가 완료되었습니다.");
    }

    // Record는 자동으로 getter 메서드를 생성하지만, 기존 코드와의 호환성을 위해 별칭 메서드 추가
    public List<String> getInterests() {
        return interestCategories;
    }
    
    // 기존 코드와의 호환성을 위한 getter 메서드들
    public Long getUserId() {
        return userId;
    }
    
    public String getNickname() {
        return nickname;
    }
}
