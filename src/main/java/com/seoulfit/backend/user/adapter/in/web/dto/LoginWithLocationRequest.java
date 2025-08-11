package com.seoulfit.backend.user.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 위치 정보를 포함한 로그인 요청 DTO
 * 
 * 사용자 로그인 시 위치 정보를 함께 전달하여 실시간 트리거 평가를 수행
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Schema(description = "위치 정보를 포함한 로그인 요청")
@Getter
@NoArgsConstructor
public class LoginWithLocationRequest {

    @Schema(description = "사용자 ID (이메일)", example = "user@example.com", required = true)
    @NotNull(message = "사용자 ID는 필수입니다.")
    private String userId;

    @Schema(description = "위도", example = "37.5665", required = true)
    @NotNull(message = "위도는 필수입니다.")
    @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다.")
    @DecimalMax(value = "90.0", message = "위도는 90.0 이하여야 합니다.")
    private Double latitude;

    @Schema(description = "경도", example = "126.9780", required = true)
    @NotNull(message = "경도는 필수입니다.")
    @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다.")
    @DecimalMax(value = "180.0", message = "경도는 180.0 이하여야 합니다.")
    private Double longitude;

    @Schema(description = "트리거 평가 반경 (미터)", example = "2000")
    private Integer radius = 2000;

    public LoginWithLocationRequest(String userId, Double latitude, Double longitude, Integer radius) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius != null ? radius : 2000;
    }
}
