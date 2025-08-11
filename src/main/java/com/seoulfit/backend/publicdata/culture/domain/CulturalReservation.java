package com.seoulfit.backend.publicdata.culture.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "cultural_reservation")
@Getter
@NoArgsConstructor
@ToString
public class CulturalReservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 50, nullable = false)
    private Long id; // 서비스ID (기본 키로 가정)

    @Column(name = "gubun", length = 50)
    private String gubun; // 서비스구분

    @Column(name = "svc_id", length = 100)
    private String svcId;

    @Column(name = "max_class_nm", length = 100)
    private String maxClassNm; // 대분류명

    @Column(name = "min_class_nm", length = 100)
    private String minClassNm; // 소분류명

    @Column(name = "svc_stat_nm", length = 50)
    private String svcStatNm; // 서비스상태

    @Column(name = "svc_nm", length = 200)
    private String svcNm; // 서비스명

    @Column(name = "pay_at_nm", length = 50)
    private String payAtNm; // 결제방법

    @Column(name = "place_nm", length = 200)
    private String placeNm; // 장소명

    @Column(name = "use_tgt_info", length = 200)
    private String useTgtInfo; // 서비스대상

    @Column(name = "svc_url")
    private String svcUrl; // 바로가기URL

    @Column(name = "x_coord", length = 50)
    private String x; // 장소X좌표 (경도)

    @Column(name = "y_coord", length = 50)
    private String y; // 장소Y좌표 (위도)

    @Column(name = "svc_opn_bgn_dt")
    private LocalDateTime svcOpnBgnDt; // 서비스개시시작일시

    @Column(name = "svc_opn_end_dt")
    private LocalDateTime svcOpnEndDt; // 서비스개시종료일시

    @Column(name = "rcpt_bgn_dt")
    private LocalDateTime rcptBgnDt; // 접수시작일시

    @Column(name = "rcpt_end_dt")
    private LocalDateTime rcptEndDt; // 접수종료일시

    @Column(name = "area_nm", length = 100)
    private String areaNm; // 지역명

    @Column(name = "img_url")
    private String imgUrl; // 이미지경로

    @Column(name = "dtl_cont", columnDefinition = "LONGTEXT")
    private String dtlCont; // 상세내용

    @Column(name = "tel_no", length = 50)
    private String telNo; // 전화번호

    @Column(name = "v_min", length = 50)
    private String vMin; // 서비스이용 시작시간

    @Column(name = "v_max", length = 50)
    private String vMax; // 서비스이용 종료시간

    @Column(name = "rev_std_day_nm", length = 100)
    private String revStdDayNm; // 취소기간 기준정보

    @Column(name = "rev_std_day", length = 50)
    private String revStdDay; // 취소기간 기준일까지

    @Builder
    public CulturalReservation(String gubun, String svcId, String maxClassNm, String minClassNm, String svcStatNm, String svcNm, String payAtNm, String placeNm, String useTgtInfo, String svcUrl, String x, String y, LocalDateTime svcOpnBgnDt, LocalDateTime svcOpnEndDt, LocalDateTime rcptBgnDt, LocalDateTime rcptEndDt, String areaNm, String imgUrl, String dtlCont, String telNo, String vMin, String vMax, String revStdDayNm, String revStdDay) {
        this.gubun = gubun;
        this.svcId = svcId;
        this.maxClassNm = maxClassNm;
        this.minClassNm = minClassNm;
        this.svcStatNm = svcStatNm;
        this.svcNm = svcNm;
        this.payAtNm = payAtNm;
        this.placeNm = placeNm;
        this.useTgtInfo = useTgtInfo;
        this.svcUrl = svcUrl;
        this.x = x;
        this.y = y;
        this.svcOpnBgnDt = svcOpnBgnDt;
        this.svcOpnEndDt = svcOpnEndDt;
        this.rcptBgnDt = rcptBgnDt;
        this.rcptEndDt = rcptEndDt;
        this.areaNm = areaNm;
        this.imgUrl = imgUrl;
        this.dtlCont = dtlCont;
        this.telNo = telNo;
        this.vMin = vMin;
        this.vMax = vMax;
        this.revStdDayNm = revStdDayNm;
        this.revStdDay = revStdDay;
    }

    // LocalDateTime으로 변환을 위한 커스텀 setter (필요 시 사용)
    public void setSvcOpnBgnDt(String svcOpnBgnDt) {
        this.svcOpnBgnDt = svcOpnBgnDt != null ? LocalDateTime.parse(svcOpnBgnDt) : null;
    }

    public void setSvcOpnEndDt(String svcOpnEndDt) {
        this.svcOpnEndDt = svcOpnEndDt != null ? LocalDateTime.parse(svcOpnEndDt) : null;
    }

    public void setRcptBgnDt(String rcptBgnDt) {
        this.rcptBgnDt = rcptBgnDt != null ? LocalDateTime.parse(rcptBgnDt) : null;
    }

    public void setRcptEndDt(String rcptEndDt) {
        this.rcptEndDt = rcptEndDt != null ? LocalDateTime.parse(rcptEndDt) : null;
    }
}