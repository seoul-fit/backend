package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.user.domain.InterestCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 관심사 업데이트 요청 DTO Hexagonal Architecture의 인바운드 어댑터 DTO
 */
@Schema(description = "사용자 관심사 업데이트 요청")
@Getter
@NoArgsConstructor
public class UpdateUserInterestRequest {

    @Schema(description = "관심사 목록", example = "[\"WEATHER\", \"CULTURE\", \"TRAFFIC\"]")
    @NotNull(message = "관심사 목록은 필수입니다.")
    private List<InterestCategory> interests;

    @Builder
    public UpdateUserInterestRequest(List<InterestCategory> interests) {
        this.interests = interests;
    }
}
