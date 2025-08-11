package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.user.domain.InterestCategory;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 수정 요청 DTO
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
@NoArgsConstructor
public class UpdateUserRequest {

    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다.")
    private String nickname;

    private String profileImageUrl;

    private Double locationLatitude;

    private Double locationLongitude;

    private String locationAddress;

    private List<InterestCategory> interests;
}
