package com.seoulfit.backend.env.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "air_quality",
        indexes = {
                @Index(name = "idx_air_quality_station", columnList = "msr_ste_nm"),
                @Index(name = "idx_air_quality_date", columnList = "msr_dt"),
                @Index(name = "idx_air_quality_region", columnList = "msr_rgn_nm")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AirQualityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "msr_dt", nullable = false)
    private LocalDateTime msrDt; // 측정일시

    @Column(name = "msr_rgn_nm", length = 100)
    private String msrRgnNm; // 권역명

    @Column(name = "msr_ste_nm", nullable = false, length = 100)
    private String msrSteNm; // 측정소명

    @Column(name = "pm10")
    private Integer pm10; // 미세먼지(㎍/㎥)

    @Column(name = "pm25")
    private Integer pm25; // 초미세먼지농도(㎍/㎥)

    @Column(name = "o3")
    private Double o3; // 오존(ppm)

    @Column(name = "no2")
    private Double no2; // 이산화질소농도(ppm)

    @Column(name = "co")
    private Double co; // 일산화탄소농도(ppm)

    @Column(name = "so2")
    private Double so2; // 아황산가스농도(ppm)

    @Column(name = "idex_nm", length = 50)
    private String idexNm; // 통합대기환경등급

    @Column(name = "idex_mvl")
    private Integer idexMvl; // 통합대기환경지수

    @Column(name = "arplt_main", length = 50)
    private String arpltMain; // 지수결정물질

    @Column(name = "external_id", length = 100, unique = true)
    private String externalId; // 외부 시스템 고유 ID (중복 방지용)

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public AirQualityEntity(LocalDateTime msrDt, String msrRgnNm, String msrSteNm,
                      Integer pm10, Integer pm25, Double o3, Double no2, Double co, Double so2,
                      String idexNm, Integer idexMvl, String arpltMain, String externalId) {
        this.msrDt = msrDt;
        this.msrRgnNm = msrRgnNm;
        this.msrSteNm = msrSteNm;
        this.pm10 = pm10;
        this.pm25 = pm25;
        this.o3 = o3;
        this.no2 = no2;
        this.co = co;
        this.so2 = so2;
        this.idexNm = idexNm;
        this.idexMvl = idexMvl;
        this.arpltMain = arpltMain;
        this.externalId = externalId;
    }

    /**
     * PM10 기준 대기질 등급 계산
     */
    public AirQualityEntity.AirQualityGrade getPm10Grade() {
        if (pm10 == null) return AirQualityEntity.AirQualityGrade.UNKNOWN;

        if (pm10 <= 30) return AirQualityEntity.AirQualityGrade.GOOD;
        else if (pm10 <= 80) return AirQualityEntity.AirQualityGrade.MODERATE;
        else if (pm10 <= 150) return AirQualityEntity.AirQualityGrade.UNHEALTHY;
        else return AirQualityEntity.AirQualityGrade.VERY_UNHEALTHY;
    }

    /**
     * PM2.5 기준 대기질 등급 계산
     */
    public AirQualityEntity.AirQualityGrade getPm25Grade() {
        if (pm25 == null) return AirQualityEntity.AirQualityGrade.UNKNOWN;

        if (pm25 <= 15) return AirQualityEntity.AirQualityGrade.GOOD;
        else if (pm25 <= 35) return AirQualityEntity.AirQualityGrade.MODERATE;
        else if (pm25 <= 75) return AirQualityEntity.AirQualityGrade.UNHEALTHY;
        else return AirQualityEntity.AirQualityGrade.VERY_UNHEALTHY;
    }

    /**
     * 전체 대기질 등급 (가장 나쁜 등급 기준)
     */
    public AirQualityEntity.AirQualityGrade getOverallGrade() {
        AirQualityEntity.AirQualityGrade pm10Grade = getPm10Grade();
        AirQualityEntity.AirQualityGrade pm25Grade = getPm25Grade();

        // 더 나쁜 등급을 반환
        return pm10Grade.ordinal() > pm25Grade.ordinal() ? pm10Grade : pm25Grade;
    }

    /**
     * 외부 활동 권장 여부
     */
    public boolean isGoodForOutdoorActivity() {
        AirQualityEntity.AirQualityGrade overallGrade = getOverallGrade();
        return overallGrade == AirQualityEntity.AirQualityGrade.GOOD || overallGrade == AirQualityEntity.AirQualityGrade.MODERATE;
    }

    /**
     * 대기질 상태 메시지
     */
    public String getQualityMessage() {
        return switch (getOverallGrade()) {
            case GOOD -> "좋음 - 외부 활동하기 좋은 날씨입니다.";
            case MODERATE -> "보통 - 일반적인 외부 활동에 문제없습니다.";
            case UNHEALTHY -> "나쁨 - 민감한 분들은 외부 활동을 자제하세요.";
            case VERY_UNHEALTHY -> "매우 나쁨 - 외부 활동을 피하시기 바랍니다.";
            case UNKNOWN -> "측정 데이터가 없습니다.";
        };
    }

    /**
     * 대기질 등급 열거형
     */
    @RequiredArgsConstructor
    @Getter
    public enum AirQualityGrade {
        GOOD("좋음"),
        MODERATE("보통"),
        UNHEALTHY("나쁨"),
        VERY_UNHEALTHY("매우 나쁨"),
        UNKNOWN("알 수 없음");

        private final String description;

    }

    /**
     * 필수 데이터 유효성 검증
     */
    public boolean isValid() {
        return msrSteNm != null && !msrSteNm.trim().isEmpty() &&
                msrDt != null;
    }

    /**
     * 대기질 데이터 유효성 검증
     */
    public boolean hasValidAirQuality() {
        return pm10 != null || pm25 != null || o3 != null ||
                no2 != null || co != null || so2 != null;
    }
}
