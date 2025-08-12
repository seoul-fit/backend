package com.seoulfit.backend.publicdata.facilities.adapter.in.web.dto;

import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 무더위쉼터 정보 API 응답 DTO
 */
@Getter
@Builder
@Schema(description = "무더위쉼터 정보 응답")
public class CoolingCenterResponse {

    @Schema(description = "무더위쉼터 ID", example = "1")
    private Long id;

    @Schema(description = "시설년도", example = "2025")
    private Integer facilityYear;

    @Schema(description = "위치코드", example = "11010")
    private String areaCode;

    @Schema(description = "시설구분1", example = "공공시설")
    private String facilityType1;

    @Schema(description = "시설구분2", example = "주민센터")
    private String facilityType2;

    @Schema(description = "쉼터명칭", example = "종로구청 무더위쉼터")
    private String name;

    @Schema(description = "도로명주소", example = "서울특별시 종로구 종로1길 36")
    private String roadAddress;

    @Schema(description = "지번주소", example = "서울특별시 종로구 수송동 146")
    private String lotAddress;

    @Schema(description = "시설면적", example = "100.5㎡")
    private String areaSize;

    @Schema(description = "이용가능인원", example = "50")
    private Integer capacity;

    @Schema(description = "비고", example = "에어컨 완비")
    private String remarks;

    @Schema(description = "경도", example = "126.9780")
    private Double longitude;

    @Schema(description = "위도", example = "37.5665")
    private Double latitude;

    @Schema(description = "X좌표(EPSG:5186)", example = "198765.123")
    private Double mapCoordX;

    @Schema(description = "Y좌표(EPSG:5186)", example = "451234.567")
    private Double mapCoordY;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    /**
     * CoolingCenter 도메인 객체를 CoolingCenterResponse DTO로 변환
     */
    public static CoolingCenterResponse from(CoolingCenter center) {
        return CoolingCenterResponse.builder()
                .id(center.getId())
                .facilityYear(center.getFacilityYear())
                .areaCode(center.getAreaCode())
                .facilityType1(center.getFacilityType1())
                .facilityType2(center.getFacilityType2())
                .name(center.getName())
                .roadAddress(center.getRoadAddress())
                .lotAddress(center.getLotAddress())
                .areaSize(center.getAreaSize())
                .capacity(center.getCapacity())
                .remarks(center.getRemarks())
                .longitude(center.getLongitude())
                .latitude(center.getLatitude())
                .mapCoordX(center.getMapCoordX())
                .mapCoordY(center.getMapCoordY())
                .createdAt(center.getCreatedAt())
                .updatedAt(center.getUpdatedAt())
                .build();
    }

    /**
     * CoolingCenter 리스트를 CoolingCenterResponse 리스트로 변환
     */
    public static List<CoolingCenterResponse> from(List<CoolingCenter> centers) {
        return centers.stream()
                .map(CoolingCenterResponse::from)
                .collect(Collectors.toList());
    }
}
