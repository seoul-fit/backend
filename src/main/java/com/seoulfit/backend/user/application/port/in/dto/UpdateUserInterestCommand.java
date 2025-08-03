package com.seoulfit.backend.user.application.port.in.dto;

import com.seoulfit.backend.user.domain.InterestCategory;
import java.util.List;

/**
 * 사용자 관심사 업데이트 명령
 */
public record UpdateUserInterestCommand(
    Long userId,
    List<InterestCategory> interestCategories
) {
    
    public UpdateUserInterestCommand {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        if (interestCategories == null) {
            throw new IllegalArgumentException("관심사 카테고리는 필수입니다.");
        }
    }
    
    /**
     * 명령 생성
     *
     * @param userId 사용자 ID
     * @param interestCategories 관심사 카테고리 목록
     * @return 업데이트 명령
     */
    public static UpdateUserInterestCommand of(Long userId, List<InterestCategory> interestCategories) {
        return new UpdateUserInterestCommand(userId, interestCategories);
    }
    
    // 기존 코드와의 호환성을 위한 별칭 메서드
    public List<InterestCategory> getInterests() {
        return interestCategories;
    }
}
