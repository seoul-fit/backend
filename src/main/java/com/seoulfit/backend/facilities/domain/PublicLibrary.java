package com.seoulfit.backend.facilities.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "public_library")
@Entity
public class PublicLibrary {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lbrry_id")
    private Long id;

    @Column(name = "lbrry_name")
    private String lbrryName;

    @Column(name = "gu_code")
    private String guCode;

    @Column(name = "code_value")
    private String codeValue;

    @Column(name = "adres")
    private String adres;

    @Column(name = "tel_no")
    private String telNo;

    @Column(name = "hmpg_url")
    private String hmpgUrl;

    @Column(name = "op_time")
    private String opTime;

    @Column(name = "fdrm_close_date")
    private String fdrmCloseDate;

    @Column(name = "lbrry_se_name")
    private String lbrrySeName;

    @Column(name = "xcnts")
    private double xcnts;

    @Column(name = "ydnts")
    private double ydnts;

    @Builder
    public PublicLibrary(String lbrryName, String guCode, String codeValue, String adres, String telNo, String hmpgUrl, String opTime, String fdrmCloseDate, String lbrrySeName, double xcnts, double ydnts) {
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
    }
}