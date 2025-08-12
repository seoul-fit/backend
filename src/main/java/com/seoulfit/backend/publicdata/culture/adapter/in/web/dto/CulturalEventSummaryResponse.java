package com.seoulfit.backend.publicdata.culture.adapter.in.web.dto;

import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 문화행사 요약 정보 API 응답 DTO
 * 목록 조회 시 필요한 핵심 정보만 제공
 */
@Getter
@Builder
@Schema(description = "문화행사 요약 정보 응답")
public class CulturalEventSummaryResponse {

    @Schema(description = "문화행사 ID", example = "1")
    private Long id;

    @Schema(description = "분류", example = "전시/미술")
    private String codeName;

    @Schema(description = "자치구", example = "강남구")
    private String district;

    @Schema(description = "공연/행사명", example = "서울 미술 전시회")
    private String title;

    @Schema(description = "시작일", example = "2025-08-15")
    private LocalDate startDate;

    @Schema(description = "종료일", example = "2025-08-20")
    private LocalDate endDate;

    @Schema(description = "장소", example = "서울시립미술관")
    private String place;

    @Schema(description = "이용요금", example = "무료")
    private String useFee;

    @Schema(description = "대표이미지", example = "https://example.com/image.jpg")
    private String mainImg;

    @Schema(description = "위도", example = "37.5665")
    private BigDecimal latitude;

    @Schema(description = "경도", example = "126.9780")
    private BigDecimal longitude;

    @Schema(description = "유무료", example = "무료")
    private String isFree;

    /**
     * CulturalEvent 도메인 객체를 CulturalEventSummaryResponse DTO로 변환
     */
    public static CulturalEventSummaryResponse from(CulturalEvent event) {
        return CulturalEventSummaryResponse.builder()
                .id(event.getId())
                .codeName(event.getCodeName())
                .district(event.getDistrict())
                .title(event.getTitle())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .place(event.getPlace())
                .useFee(event.getUseFee())
                .mainImg(event.getMainImg())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .isFree(event.getIsFree())
                .build();
    }

    /**
     * CulturalEvent 리스트를 CulturalEventSummaryResponse 리스트로 변환
     */
    public static List<CulturalEventSummaryResponse> from(List<CulturalEvent> events) {
        return events.stream()
                .map(CulturalEventSummaryResponse::from)
                .collect(Collectors.toList());
    }
}
