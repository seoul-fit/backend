package com.seoulfit.backend.location.adapter.dto;

import com.seoulfit.backend.location.domain.MapScale;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 고도화된 위치 기반 데이터 요청 DTO
 * 
 * 지도 축적정보를 포함한 고급 위치 기반 데이터 조회 요청
 * 다양한 방식으로 지도 축적을 지정할 수 있음
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Schema(description = "고도화된 위치 기반 데이터 요청")
@Getter
@NoArgsConstructor
public class AdvancedLocationRequest {

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

    // 지도 축적 정보 지정 방법 1: 직접 지정
    @Schema(description = "지도 축적 정보 (직접 지정)", 
            example = "DISTRICT_MEDIUM",
            allowableValues = {"BUILDING_LEVEL", "STREET_LEVEL", "NEIGHBORHOOD_CLOSE", 
                              "NEIGHBORHOOD_MEDIUM", "NEIGHBORHOOD_FAR", "DISTRICT_CLOSE", 
                              "DISTRICT_MEDIUM", "DISTRICT_FAR", "CITY_CLOSE", "CITY_MEDIUM", 
                              "CITY_FAR", "METROPOLITAN"})
    private MapScale mapScale;

    // 지도 축적 정보 지정 방법 2: 줌 레벨 (Google Maps 스타일)
    @Schema(description = "줌 레벨 (Google Maps 스타일, 1-20)", example = "15")
    @DecimalMin(value = "1", message = "줌 레벨은 1 이상이어야 합니다.")
    @DecimalMax(value = "20", message = "줌 레벨은 20 이하여야 합니다.")
    private Integer zoomLevel;

    // 지도 축적 정보 지정 방법 3: 픽셀 해상도
    @Schema(description = "픽셀당 미터 (해상도)", example = "10.5")
    @Positive(message = "픽셀당 미터는 양수여야 합니다.")
    private Double metersPerPixel;

    // 지도 축적 정보 지정 방법 4: 바운딩 박스 크기
    @Schema(description = "바운딩 박스 너비 (km)", example = "2.0")
    @Positive(message = "바운딩 박스 너비는 양수여야 합니다.")
    private Double boundingBoxWidthKm;

    @Schema(description = "바운딩 박스 높이 (km)", example = "1.5")
    @Positive(message = "바운딩 박스 높이는 양수여야 합니다.")
    private Double boundingBoxHeightKm;

    // 추가 옵션들
    @Schema(description = "최대 결과 개수 (성능 최적화)", example = "100")
    @Positive(message = "최대 결과 개수는 양수여야 합니다.")
    private Integer maxResults;

    @Schema(description = "거리 계산 정확도 모드", 
            example = "BALANCED",
            allowableValues = {"FAST", "BALANCED", "ACCURATE"})
    private DistanceCalculationMode distanceMode = DistanceCalculationMode.BALANCED;

    @Schema(description = "결과 정렬 방식", 
            example = "DISTANCE",
            allowableValues = {"DISTANCE", "RELEVANCE", "POPULARITY"})
    private SortMode sortMode = SortMode.DISTANCE;

    @Schema(description = "카테고리별 최대 개수 제한 여부", example = "true")
    private Boolean limitPerCategory = true;

    @Schema(description = "비동기 처리 여부", example = "true")
    private Boolean asyncProcessing = true;

    public AdvancedLocationRequest(Double latitude, Double longitude, MapScale mapScale, 
                                 Integer maxResults) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.mapScale = mapScale;
        this.maxResults = maxResults;
    }

    /**
     * 거리 계산 정확도 모드
     */
    public enum DistanceCalculationMode {
        FAST("빠른 계산 (근사치)"),
        BALANCED("균형 (기본값)"),
        ACCURATE("정확한 계산 (Haversine)");

        private final String description;

        DistanceCalculationMode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 결과 정렬 방식
     */
    public enum SortMode {
        DISTANCE("거리순"),
        RELEVANCE("관련도순"),
        POPULARITY("인기도순");

        private final String description;

        SortMode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 요청 유효성 검증
     */
    public boolean isValid() {
        if (latitude == null || longitude == null) {
            return false;
        }

        // 지도 축적 정보가 하나라도 지정되어야 함
        return mapScale != null || zoomLevel != null || metersPerPixel != null || 
               (boundingBoxWidthKm != null && boundingBoxHeightKm != null);
    }

    /**
     * 지도 축적 정보 지정 방법 개수 반환
     */
    public int getScaleSpecificationCount() {
        int count = 0;
        if (mapScale != null) count++;
        if (zoomLevel != null) count++;
        if (metersPerPixel != null) count++;
        if (boundingBoxWidthKm != null && boundingBoxHeightKm != null) count++;
        return count;
    }
}
