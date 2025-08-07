package com.seoulfit.backend.trigger.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 위치 기반 트리거 요청 DTO
 * 
 * 사용자의 위치 정보를 기반으로 트리거를 평가하기 위한 요청 객체
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Schema(description = "위치 기반 트리거 평가 요청")
@Getter
@NoArgsConstructor
public class LocationTriggerRequest {

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

    @Schema(description = "검색 반경 (미터)", example = "2000", required = false)
    @Positive(message = "반경은 양수여야 합니다.")
    private Integer radius = 2000; // 기본값: 2km

    @Schema(description = "평가할 트리거 타입 목록 (비어있으면 모든 타입)", 
            example = "[\"TEMPERATURE\", \"AIR_QUALITY\", \"BIKE_SHARE\"]")
    private List<String> triggerTypes;

    @Schema(description = "강제 평가 여부 (중복 알림 방지 무시)", example = "false")
    private Boolean forceEvaluation = false;

    public LocationTriggerRequest(Double latitude, Double longitude, Integer radius, 
                                List<String> triggerTypes, Boolean forceEvaluation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius != null ? radius : 2000;
        this.triggerTypes = triggerTypes;
        this.forceEvaluation = forceEvaluation != null ? forceEvaluation : false;
    }
}
