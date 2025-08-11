package com.seoulfit.backend.publicdata.env.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 서울시 대기질 정보 도메인 엔티티
 * 서울시 공공 데이터 API에서 제공하는 대기질 정보를 저장
 */
@Entity
@Table(name = "air_quality")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AirQuality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("측정 일시")
    @Column(name = "msr_dt", nullable = false)
    private LocalDateTime msrDt;

    @Comment("측정소 지역명")
    @Column(name = "msr_rgn_nm", length = 100)
    private String msrRgnNm;

    @Comment("측정소 명")
    @Column(name = "msr_ste_nm", length = 100, nullable = false)
    private String msrSteNm;

    @Comment("PM10 농도 ((㎍/㎥)")
    @Column(name = "pm10_value")
    private Integer pm10Value;

    @Comment("PM2.5 농도 (㎍/㎥)")
    @Column(name = "pm25_value")
    private Integer pm25Value;

    @Comment("오존 농도 (ppm)")
    @Column(name = "o3_value")
    private Double o3Value;

    @Comment("이산화질소 농도 (ppm)")
    @Column(name = "no2_value")
    private Double no2Value;

    @Comment("일산화탄소 농도 (ppm)")
    @Column(name = "co_value")
    private Double coValue;

    @Comment("아황솬가스 농도 (ppm)")
    @Column(name = "so2_value")
    private Double so2Value;

    @Comment("통합대기환경지수")
    @Column(name = "khai_value")
    private Integer khaiValue;

    @Comment("통합대기환경지수 등급")
    @Column(name = "khai_grade", length = 20)
    private String khaiGrade;

    @Comment("PM10 24시간 예측이동평균농도")
    @Column(name = "pm10_24h_avg")
    private Integer pm1024hAvg;

    @Comment("PM2.5 24시간 예측이동평균농도")
    @Column(name = "pm25_24h_avg")
    private Integer pm2524hAvg;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public AirQuality(LocalDateTime msrDt, String msrRgnNm, String msrSteNm,
                     Integer pm10Value, Integer pm25Value, Double o3Value,
                     Double no2Value, Double coValue, Double so2Value,
                     Integer khaiValue, String khaiGrade,
                     Integer pm1024hAvg, Integer pm2524hAvg) {
        this.msrDt = msrDt;
        this.msrRgnNm = msrRgnNm;
        this.msrSteNm = msrSteNm;
        this.pm10Value = pm10Value;
        this.pm25Value = pm25Value;
        this.o3Value = o3Value;
        this.no2Value = no2Value;
        this.coValue = coValue;
        this.so2Value = so2Value;
        this.khaiValue = khaiValue;
        this.khaiGrade = khaiGrade;
        this.pm1024hAvg = pm1024hAvg;
        this.pm2524hAvg = pm2524hAvg;
    }

    /**
     * 대기질 상태 판단
     */
    public AirQualityStatus getAirQualityStatus() {
        if (khaiValue == null) {
            return AirQualityStatus.UNKNOWN;
        }
        
        if (khaiValue <= 50) {
            return AirQualityStatus.GOOD;
        } else if (khaiValue <= 100) {
            return AirQualityStatus.MODERATE;
        } else if (khaiValue <= 150) {
            return AirQualityStatus.UNHEALTHY_FOR_SENSITIVE;
        } else if (khaiValue <= 200) {
            return AirQualityStatus.UNHEALTHY;
        } else if (khaiValue <= 300) {
            return AirQualityStatus.VERY_UNHEALTHY;
        } else {
            return AirQualityStatus.HAZARDOUS;
        }
    }

    /**
     * PM2.5 기준 대기질 상태 판단
     */
    public AirQualityStatus getPm25Status() {
        if (pm25Value == null) {
            return AirQualityStatus.UNKNOWN;
        }
        
        if (pm25Value <= 15) {
            return AirQualityStatus.GOOD;
        } else if (pm25Value <= 35) {
            return AirQualityStatus.MODERATE;
        } else if (pm25Value <= 75) {
            return AirQualityStatus.UNHEALTHY_FOR_SENSITIVE;
        } else if (pm25Value <= 150) {
            return AirQualityStatus.UNHEALTHY;
        } else {
            return AirQualityStatus.VERY_UNHEALTHY;
        }
    }

    /**
     * 데이터 업데이트
     */
    public void updateData(Integer pm10Value, Integer pm25Value, Double o3Value,
                          Double no2Value, Double coValue, Double so2Value,
                          Integer khaiValue, String khaiGrade,
                          Integer pm1024hAvg, Integer pm2524hAvg) {
        this.pm10Value = pm10Value;
        this.pm25Value = pm25Value;
        this.o3Value = o3Value;
        this.no2Value = no2Value;
        this.coValue = coValue;
        this.so2Value = so2Value;
        this.khaiValue = khaiValue;
        this.khaiGrade = khaiGrade;
        this.pm1024hAvg = pm1024hAvg;
        this.pm2524hAvg = pm2524hAvg;
    }

}
