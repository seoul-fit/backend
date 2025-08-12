package com.seoulfit.backend.publicdata.park.adapter.in.web.dto;

import com.seoulfit.backend.publicdata.park.domain.Park;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 공원 정보 API 응답 DTO
 * 
 * 프론트엔드에서 필요한 공원 정보만 선별하여 제공
 * Swagger 문서화를 위한 스키마 정보 포함
 */
@Getter
@Builder
@Schema(description = "공원 정보 응답")
public class ParkResponse {

    @Schema(description = "공원 ID", example = "1")
    private Long id;

    @Schema(description = "공원 번호", example = "1")
    private Integer parkIdx;

    @Schema(description = "공원명", example = "남산공원")
    private String name;

    @Schema(description = "공원 개요", example = "서울의 대표적인 도심 공원")
    private String content;

    @Schema(description = "면적", example = "125,000㎡")
    private String area;

    @Schema(description = "개원일", example = "1968-01-01")
    private String openDate;

    @Schema(description = "주요시설", example = "산책로, 전망대, 놀이터")
    private String mainEquipment;

    @Schema(description = "주요식물", example = "소나무, 벚나무, 단풍나무")
    private String mainPlants;

    @Schema(description = "이미지 URL", example = "https://example.com/park.jpg")
    private String imageUrl;

    @Schema(description = "지역", example = "중구")
    private String zone;

    @Schema(description = "주소", example = "서울특별시 중구 남산공원길 105")
    private String address;

    @Schema(description = "관리부서", example = "중구청 공원녹지과")
    private String managementDept;

    @Schema(description = "전화번호", example = "02-1234-5678")
    private String adminTel;

    @Schema(description = "위도", example = "37.5511")
    private Double latitude;

    @Schema(description = "경도", example = "126.9882")
    private Double longitude;

    @Schema(description = "홈페이지 URL", example = "https://example.com")
    private String templateUrl;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    /**
     * Park 도메인 객체를 ParkResponse DTO로 변환
     */
    public static ParkResponse from(Park park) {
        return ParkResponse.builder()
                .id(park.getId())
                .parkIdx(park.getParkIdx())
                .name(park.getName())
                .content(park.getContent())
                .area(park.getArea())
                .openDate(park.getOpenDate())
                .mainEquipment(park.getMainEquipment())
                .mainPlants(park.getMainPlants())
                .imageUrl(park.getImageUrl())
                .zone(park.getZone())
                .address(park.getAddress())
                .managementDept(park.getManagementDept())
                .adminTel(park.getAdminTel())
                .latitude(park.getLatitude())
                .longitude(park.getLongitude())
                .templateUrl(park.getTemplateUrl())
                .createdAt(park.getCreatedAt())
                .updatedAt(park.getUpdatedAt())
                .build();
    }

    /**
     * Park 리스트를 ParkResponse 리스트로 변환
     */
    public static List<ParkResponse> from(List<Park> parks) {
        return parks.stream()
                .map(ParkResponse::from)
                .collect(Collectors.toList());
    }
}
