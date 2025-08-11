package com.seoulfit.backend.publicdata.facilities.domain;

import com.seoulfit.backend.location.util.GeoUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 도서관 엔티티
 * 
 * 서울시 공공도서관 정보를 저장하는 엔티티
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Entity
@Table(name = "libraries", indexes = {
    @Index(name = "idx_libraries_location", columnList = "xcnts, ydnts"),
    @Index(name = "idx_libraries_gu", columnList = "code_value"),
    @Index(name = "idx_libraries_name", columnList = "lbrry_name")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Library implements GeoUtils.GeoPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lbrry_seq_no", length = 50)
    private String lbrrySeqNo; // 도서관 일련번호

    @Column(name = "lbrry_name", nullable = false, length = 200)
    private String lbrryName; // 도서관명

    @Column(name = "gu_code", length = 10)
    private String guCode; // 구 코드

    @Column(name = "code_value", length = 50)
    private String codeValue; // 구명

    @Column(name = "adres", length = 300)
    private String adres; // 주소

    @Column(name = "tel_no", length = 50)
    private String telNo; // 전화번호

    @Column(name = "hmpg_url", length = 500)
    private String hmpgUrl; // 홈페이지 URL

    @Column(name = "op_time", length = 200)
    private String opTime; // 운영시간

    @Column(name = "fdrm_close_date", length = 200)
    private String fdrmCloseDate; // 정기 휴관일

    @Column(name = "lbrry_se_name", length = 100)
    private String lbrrySeName; // 도서관 구분명

    @Column(name = "xcnts")
    private Double xcnts; // 위도

    @Column(name = "ydnts")
    private Double ydnts; // 경도

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Library(String lbrrySeqNo, String lbrryName, String guCode, String codeValue,
                  String adres, String telNo, String hmpgUrl, String opTime, String fdrmCloseDate,
                  String lbrrySeName, Double xcnts, Double ydnts) {
        this.lbrrySeqNo = lbrrySeqNo;
        this.lbrryName = lbrryName;
        this.guCode = guCode;
        this.codeValue = codeValue;
        this.adres = adres;
        this.telNo = telNo;
        this.hmpgUrl = hmpgUrl;
        this.opTime = opTime;
        this.fdrmCloseDate = fdrmCloseDate;
        this.lbrrySeName = lbrrySeName;
        this.xcnts = xcnts;
        this.ydnts = ydnts;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * GeoPoint 인터페이스 구현 - 위도 반환
     */
    @Override
    public double getLatitude() {
        return xcnts != null ? xcnts : 0.0;
    }

    /**
     * GeoPoint 인터페이스 구현 - 경도 반환
     */
    @Override
    public double getLongitude() {
        return ydnts != null ? ydnts : 0.0;
    }

    /**
     * 위치 정보가 있는지 확인
     */
    public boolean hasLocation() {
        return xcnts != null && ydnts != null;
    }

    /**
     * 연락처 정보가 있는지 확인
     */
    public boolean hasContact() {
        return telNo != null && !telNo.trim().isEmpty();
    }

    /**
     * 홈페이지가 있는지 확인
     */
    public boolean hasWebsite() {
        return hmpgUrl != null && !hmpgUrl.trim().isEmpty();
    }

    /**
     * 엔티티 업데이트
     */
    public void update(String lbrryName, String adres, String telNo, String hmpgUrl, 
                      String opTime, String fdrmCloseDate, Double xcnts, Double ydnts) {
        this.lbrryName = lbrryName;
        this.adres = adres;
        this.telNo = telNo;
        this.hmpgUrl = hmpgUrl;
        this.opTime = opTime;
        this.fdrmCloseDate = fdrmCloseDate;
        this.xcnts = xcnts;
        this.ydnts = ydnts;
        this.updatedAt = LocalDateTime.now();
    }
}
