package com.seoulfit.backend.publicdata.culture.adapter.in.web.dto;

import com.seoulfit.backend.publicdata.culture.domain.CulturalSpace;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 문화공간 정보 API 응답 DTO
 */
@Getter
@Builder
@Schema(description = "문화공간 정보 응답")
public class CulturalSpaceResponse {

    @Schema(description = "문화공간 ID", example = "1")
    private Long id;

    @Schema(description = "번호", example = "1")
    private Integer num;

    @Schema(description = "주제분류", example = "미술관")
    private String subjCode;

    @Schema(description = "문화시설명", example = "서울시립미술관")
    private String facilityName;

    @Schema(description = "주소", example = "서울특별시 중구 덕수궁길 61")
    private String address;

    @Schema(description = "자치구", example = "중구")
    private String district;

    @Schema(description = "경도", example = "126.9780")
    private BigDecimal longitude;

    @Schema(description = "위도", example = "37.5665")
    private BigDecimal latitude;

    @Schema(description = "전화번호", example = "02-2124-8800")
    private String phone;

    @Schema(description = "팩스번호", example = "02-2124-8801")
    private String fax;

    @Schema(description = "홈페이지", example = "https://sema.seoul.go.kr")
    private String homepage;

    @Schema(description = "관람시간", example = "화~일 10:00~20:00")
    private String openHour;

    @Schema(description = "관람료", example = "무료")
    private String entranceFee;

    @Schema(description = "휴관일", example = "매주 월요일")
    private String closeDay;

    @Schema(description = "개관일자", example = "1988-12-15")
    private String openDay;

    @Schema(description = "객석수", example = "200")
    private String seatCount;

    @Schema(description = "대표이미지", example = "https://example.com/image.jpg")
    private String mainImage;

    @Schema(description = "기타사항", example = "주차 가능")
    private String etcDesc;

    @Schema(description = "시설소개", example = "서울시립미술관은...")
    private String facilityDesc;

    @Schema(description = "무료구분", example = "무료")
    private String isFree;

    @Schema(description = "지하철", example = "1호선 시청역")
    private String subway;

    @Schema(description = "버스정거장", example = "시청앞")
    private String busStop;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    /**
     * CulturalSpace 도메인 객체를 CulturalSpaceResponse DTO로 변환
     */
    public static CulturalSpaceResponse from(CulturalSpace space) {
        return CulturalSpaceResponse.builder()
                .id(space.getId())
                .num(space.getNum())
                .subjCode(space.getSubjCode())
                .facilityName(space.getFacilityName())
                .address(space.getAddress())
                .district(space.getDistrict())
                .longitude(space.getLongitude())
                .latitude(space.getLatitude())
                .phone(space.getPhone())
                .fax(space.getFax())
                .homepage(space.getHomepage())
                .openHour(space.getOpenHour())
                .entranceFee(space.getEntranceFee())
                .closeDay(space.getCloseDay())
                .openDay(space.getOpenDay())
                .seatCount(space.getSeatCount())
                .mainImage(space.getMainImage())
                .etcDesc(space.getEtcDesc())
                .facilityDesc(space.getFacilityDesc())
                .isFree(space.getIsFree())
                .subway(space.getSubway())
                .busStop(space.getBusStop())
                .createdAt(space.getCreatedAt())
                .updatedAt(space.getUpdatedAt())
                .build();
    }

    /**
     * CulturalSpace 리스트를 CulturalSpaceResponse 리스트로 변환
     */
    public static List<CulturalSpaceResponse> from(List<CulturalSpace> spaces) {
        return spaces.stream()
                .map(CulturalSpaceResponse::from)
                .collect(Collectors.toList());
    }
}
