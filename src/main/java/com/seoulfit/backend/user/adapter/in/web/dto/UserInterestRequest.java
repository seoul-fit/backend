package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.user.domain.InterestCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 사용자 관심사 설정 요청 DTO
 * 
 * 사용자가 체크박스로 선택한 관심사들을 설정하기 위한 요청 객체
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Schema(description = "사용자 관심사 설정 요청")
@Getter
@NoArgsConstructor
public class UserInterestRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @Schema(description = "선택한 관심사 카테고리 목록", 
            example = "[\"RESTAURANTS\", \"LIBRARIES\", \"PARKS\", \"SPORTS\", \"CULTURE\"]",
            required = true)
    @NotNull(message = "관심사 목록은 필수입니다.")
    @NotEmpty(message = "최소 하나 이상의 관심사를 선택해야 합니다.")
    private List<InterestCategory> interests;

    public UserInterestRequest(List<InterestCategory> interests) {
        this.interests = interests;
    }

    /**
     * 관심사 유효성 검증
     */
    public boolean isValid() {
        if (interests == null || interests.isEmpty()) {
            return false;
        }
        
        // 중복 제거 및 유효성 검증
        return interests.stream()
                .distinct()
                .allMatch(interest -> interest != null);
    }

    /**
     * 관심사 개수 반환
     */
    public int getInterestCount() {
        return interests != null ? interests.size() : 0;
    }
}
