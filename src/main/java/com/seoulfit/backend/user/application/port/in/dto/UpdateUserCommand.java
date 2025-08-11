package com.seoulfit.backend.user.application.port.in.dto;

import com.seoulfit.backend.user.domain.InterestCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 사용자 수정 명령
 * <p>
 * 헥사고날 아키텍처의 Command 객체
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
@Builder
public class UpdateUserCommand {

    private final Long userId;
    private final String nickname;
    private final String profileImageUrl;
    private final Double locationLatitude;
    private final Double locationLongitude;
    private final String locationAddress;
    private final List<InterestCategory> interests;

    /**
     * 사용자 수정 명령 생성
     *
     * @param userId            사용자 ID
     * @param nickname          닉네임
     * @param profileImageUrl   프로필 이미지 URL
     * @param locationLatitude  위도
     * @param locationLongitude 경도
     * @param locationAddress   주소
     * @param interests         관심사 목록
     * @return 사용자 수정 명령
     */
    public static UpdateUserCommand of(Long userId, String nickname, String profileImageUrl,
                                      Double locationLatitude, Double locationLongitude, String locationAddress,
                                      List<InterestCategory> interests) {
        return UpdateUserCommand.builder()
                .userId(userId)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .locationLatitude(locationLatitude)
                .locationLongitude(locationLongitude)
                .locationAddress(locationAddress)
                .interests(interests)
                .build();
    }
}
