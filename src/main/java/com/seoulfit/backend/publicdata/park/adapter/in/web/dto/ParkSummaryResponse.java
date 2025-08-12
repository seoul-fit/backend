package com.seoulfit.backend.publicdata.park.adapter.in.web.dto;

import com.seoulfit.backend.publicdata.park.domain.Park;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 공원 요약 정보 API 응답 DTO
 * 
 * 목록 조회 시 필요한 핵심 정보만 제공하여 응답 크기 최적화
 */
@Getter
@Builder
@Schema(description = "공원 요약 정보 응답")
public class ParkSummaryResponse {

    @Schema(description = "공원 ID", example = "1")
    private Long id;

    @Schema(description = "공원명", example = "남산공원")
    private String name;

    @Schema(description = "공원 개요 (100자 제한)", example = "서울의 대표적인 도심 공원으로...")
    private String shortContent;

    @Schema(description = "면적", example = "125,000㎡")
    private String area;

    @Schema(description = "이미지 URL", example = "https://example.com/park.jpg")
    private String imageUrl;

    @Schema(description = "지역", example = "중구")
    private String zone;

    @Schema(description = "주소", example = "서울특별시 중구 남산공원길 105")
    private String address;

    @Schema(description = "전화번호", example = "02-1234-5678")
    private String adminTel;

    @Schema(description = "위도", example = "37.5511")
    private Double latitude;

    @Schema(description = "경도", example = "126.9882")
    private Double longitude;

    @Schema(description = "연락처 정보 보유 여부", example = "true")
    private Boolean hasContact;

    @Schema(description = "이미지 보유 여부", example = "true")
    private Boolean hasImage;

    /**
     * Park 도메인 객체를 ParkSummaryResponse DTO로 변환
     */
    public static ParkSummaryResponse from(Park park) {
        return ParkSummaryResponse.builder()
                .id(park.getId())
                .name(park.getName())
                .shortContent(truncateContent(park.getContent(), 100))
                .area(park.getArea())
                .imageUrl(park.getImageUrl())
                .zone(park.getZone())
                .address(park.getAddress())
                .adminTel(park.getAdminTel())
                .latitude(park.getLatitude())
                .longitude(park.getLongitude())
                .hasContact(park.hasContact())
                .hasImage(park.hasImage())
                .build();
    }

    /**
     * Park 리스트를 ParkSummaryResponse 리스트로 변환
     */
    public static List<ParkSummaryResponse> from(List<Park> parks) {
        return parks.stream()
                .map(ParkSummaryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 텍스트를 지정된 길이로 자르기
     */
    private static String truncateContent(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}
