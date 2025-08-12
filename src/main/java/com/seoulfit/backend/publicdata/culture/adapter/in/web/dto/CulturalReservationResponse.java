package com.seoulfit.backend.publicdata.culture.adapter.in.web.dto;

import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 문화행사 예약 정보 API 응답 DTO
 */
@Getter
@Builder
@Schema(description = "문화행사 예약 정보 응답")
public class CulturalReservationResponse {

    @Schema(description = "예약 ID", example = "1")
    private Long id;

    @Schema(description = "서비스구분", example = "접수")
    private String gubun;

    @Schema(description = "서비스ID", example = "S123456")
    private String svcId;

    @Schema(description = "대분류명", example = "문화행사")
    private String maxClassNm;

    @Schema(description = "소분류명", example = "전시")
    private String minClassNm;

    @Schema(description = "서비스상태", example = "접수중")
    private String svcStatNm;

    @Schema(description = "서비스명", example = "서울 미술 전시회")
    private String svcNm;

    @Schema(description = "결제방법", example = "무료")
    private String payAtNm;

    @Schema(description = "장소명", example = "서울시립미술관")
    private String placeNm;

    @Schema(description = "이용대상정보", example = "전체")
    private String useTgtInfo;

    @Schema(description = "서비스URL", example = "https://example.com")
    private String svcUrl;

    @Schema(description = "X좌표", example = "126.9780")
    private String x;

    @Schema(description = "Y좌표", example = "37.5665")
    private String y;

    @Schema(description = "서비스개시시작일시", example = "2025-08-15T10:00:00")
    private LocalDateTime svcOpnBgnDt;

    @Schema(description = "서비스개시종료일시", example = "2025-08-20T18:00:00")
    private LocalDateTime svcOpnEndDt;

    @Schema(description = "접수시작일시", example = "2025-08-01T09:00:00")
    private LocalDateTime rcptBgnDt;

    @Schema(description = "접수종료일시", example = "2025-08-10T18:00:00")
    private LocalDateTime rcptEndDt;

    @Schema(description = "지역명", example = "강남구")
    private String areaNm;

    @Schema(description = "이미지경로", example = "https://example.com/image.jpg")
    private String imgUrl;

    @Schema(description = "상세정보", example = "현대 미술 작품 전시")
    private String dtlCont;

    @Schema(description = "전화번호", example = "02-1234-5678")
    private String telNo;

    @Schema(description = "서비스이용 시작시간", example = "09:00")
    private String vMin;

    @Schema(description = "서비스이용 종료시간", example = "18:00")
    private String vMax;

    @Schema(description = "취소기간 기준정보", example = "이용일 3일 전까지")
    private String revStdDayNm;

    @Schema(description = "취소기간 기준일", example = "3")
    private String revStdDay;

    /**
     * CulturalReservation 도메인 객체를 CulturalReservationResponse DTO로 변환
     */
    public static CulturalReservationResponse from(CulturalReservation reservation) {
        return CulturalReservationResponse.builder()
                .id(reservation.getId())
                .gubun(reservation.getGubun())
                .svcId(reservation.getSvcId())
                .maxClassNm(reservation.getMaxClassNm())
                .minClassNm(reservation.getMinClassNm())
                .svcStatNm(reservation.getSvcStatNm())
                .svcNm(reservation.getSvcNm())
                .payAtNm(reservation.getPayAtNm())
                .placeNm(reservation.getPlaceNm())
                .useTgtInfo(reservation.getUseTgtInfo())
                .svcUrl(reservation.getSvcUrl())
                .x(reservation.getX())
                .y(reservation.getY())
                .svcOpnBgnDt(reservation.getSvcOpnBgnDt())
                .svcOpnEndDt(reservation.getSvcOpnEndDt())
                .rcptBgnDt(reservation.getRcptBgnDt())
                .rcptEndDt(reservation.getRcptEndDt())
                .areaNm(reservation.getAreaNm())
                .imgUrl(reservation.getImgUrl())
                .dtlCont(reservation.getDtlCont())
                .telNo(reservation.getTelNo())
                .vMin(reservation.getVMin())
                .vMax(reservation.getVMax())
                .revStdDayNm(reservation.getRevStdDayNm())
                .revStdDay(reservation.getRevStdDay())
                .build();
    }

    /**
     * CulturalReservation 리스트를 CulturalReservationResponse 리스트로 변환
     */
    public static List<CulturalReservationResponse> from(List<CulturalReservation> reservations) {
        return reservations.stream()
                .map(CulturalReservationResponse::from)
                .collect(Collectors.toList());
    }
}
