package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.user.application.port.in.dto.UserInterestResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 관심사 응답 DTO 
 * Hexagonal Architecture의 인바운드 어댑터 DTO
 */
@Schema(description = "사용자 관심사 응답")
@Getter
@Builder
public class UserInterestResponse {

    @Schema(description = "사용자 ID", example = "1")
    private final Long userId;

    @Schema(description = "닉네임", example = "홍길동")
    private final String nickname;

    @Schema(description = "관심사 목록", example = "[\"WEATHER\", \"CULTURE\", \"TRAFFIC\"]")
    private final List<String> interests;

    @Schema(description = "응답 메시지", example = "관심사 조회가 완료되었습니다.")
    private final String message;

    /**
     * UserInterestResult로부터 응답 DTO 생성
     *
     * @param result 사용자 관심사 결과
     * @return 사용자 관심사 응답 DTO
     */
    public static UserInterestResponse from(UserInterestResult result) {
        return UserInterestResponse.builder()
                .userId(result.getUserId())
                .nickname(result.getNickname())
                .interests(result.getInterests()) // result.getInterests()는 List<String>을 반환
                .message(result.message())
                .build();
    }
}
