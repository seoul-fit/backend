package com.seoulfit.backend.publicdata.culture.adapter.in.web.dto;

import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 문화행사 정보 API 응답 DTO
 */
@Getter
@Builder
@Schema(description = "문화행사 정보 응답")
public class CulturalEventResponse {

    @Schema(description = "문화행사 ID", example = "1")
    private Long id;

    @Schema(description = "분류", example = "전시/미술")
    private String codeName;

    @Schema(description = "자치구", example = "강남구")
    private String district;

    @Schema(description = "공연/행사명", example = "서울 미술 전시회")
    private String title;

    @Schema(description = "날짜/시간", example = "2025-08-15 ~ 2025-08-20")
    private String eventDate;

    @Schema(description = "시작일", example = "2025-08-15")
    private LocalDate startDate;

    @Schema(description = "종료일", example = "2025-08-20")
    private LocalDate endDate;

    @Schema(description = "장소", example = "서울시립미술관")
    private String place;

    @Schema(description = "기관명", example = "서울시립미술관")
    private String orgName;

    @Schema(description = "이용대상", example = "전체")
    private String useTarget;

    @Schema(description = "이용요금", example = "무료")
    private String useFee;

    @Schema(description = "출연자정보", example = "김작가")
    private String player;

    @Schema(description = "프로그램소개", example = "현대 미술 작품 전시")
    private String program;

    @Schema(description = "기타내용", example = "주차 가능")
    private String etcDesc;

    @Schema(description = "홈페이지 주소", example = "https://example.com")
    private String orgLink;

    @Schema(description = "대표이미지", example = "https://example.com/image.jpg")
    private String mainImg;

    @Schema(description = "신청일", example = "2025-08-01")
    private String registrationDate;

    @Schema(description = "시민/기관", example = "시민")
    private String ticket;

    @Schema(description = "테마분류", example = "미술")
    private String themeCode;

    @Schema(description = "위도", example = "37.5665")
    private BigDecimal latitude;

    @Schema(description = "경도", example = "126.9780")
    private BigDecimal longitude;

    @Schema(description = "유무료", example = "무료")
    private String isFree;

    @Schema(description = "문화포털상세URL", example = "https://culture.seoul.go.kr")
    private String homepageAddr;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    /**
     * CulturalEvent 도메인 객체를 CulturalEventResponse DTO로 변환
     */
    public static CulturalEventResponse from(CulturalEvent event) {
        return CulturalEventResponse.builder()
                .id(event.getId())
                .codeName(event.getCodeName())
                .district(event.getDistrict())
                .title(event.getTitle())
                .eventDate(event.getEventDate())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .place(event.getPlace())
                .orgName(event.getOrgName())
                .useTarget(event.getUseTarget())
                .useFee(event.getUseFee())
                .player(event.getPlayer())
                .program(event.getProgram())
                .etcDesc(event.getEtcDesc())
                .orgLink(event.getOrgLink())
                .mainImg(event.getMainImg())
                .registrationDate(event.getRegistrationDate())
                .ticket(event.getTicket())
                .themeCode(event.getThemeCode())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .isFree(event.getIsFree())
                .homepageAddr(event.getHomepageAddr())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    /**
     * CulturalEvent 리스트를 CulturalEventResponse 리스트로 변환
     */
    public static List<CulturalEventResponse> from(List<CulturalEvent> events) {
        return events.stream()
                .map(CulturalEventResponse::from)
                .collect(Collectors.toList());
    }
}
