package com.seoulfit.backend.publicdata.culture.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cultural_spaces",
        indexes = {
                @Index(name = "idx_space_location", columnList = "latitude, longitude"),
                @Index(name = "idx_space_category", columnList = "subj_code"),
                @Index(name = "idx_space_district", columnList = "district")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CulturalSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num")
    private Integer num; // 번호

    @Column(name = "subj_code", length = 100)
    private String subjCode; // 주제분류

    @Column(name = "facility_name", nullable = false, length = 500)
    private String facilityName; // 문화시설명

    @Column(name = "address", nullable = false, length = 500)
    private String address; // 주소

    @Column(name = "district", length = 50)
    private String district; // 자치구

    @Column(name = "longitude", precision = 16, scale = 8)
    private BigDecimal longitude; // 경도 (X_COORD)

    @Column(name = "latitude", precision = 16, scale = 8)
    private BigDecimal latitude; // 위도 (Y_COORD)

    @Column(name = "phone", length = 50)
    private String phone; // 전화번호

    @Column(name = "fax", length = 50)
    private String fax; // 팩스번호

    @Column(name = "homepage", length = 1000)
    private String homepage; // 홈페이지

    @Column(name = "open_hour", columnDefinition = "TEXT")
    private String openHour; // 관람시간

    @Column(name = "entrance_fee", columnDefinition = "TEXT")
    private String entranceFee; // 관람료

    @Column(name = "close_day", length = 200)
    private String closeDay; // 휴관일

    @Column(name = "open_day", length = 50)
    private String openDay; // 개관일자

    @Column(name = "seat_count")
    private String seatCount; // 객석수

    @Column(name = "main_image", length = 1000)
    private String mainImage; // 대표이미지

    @Column(name = "etc_desc", columnDefinition = "TEXT")
    private String etcDesc; // 기타사항

    @Column(name = "facility_desc", columnDefinition = "TEXT")
    private String facilityDesc; // 시설소개

    @Column(name = "is_free", length = 10)
    private String isFree; // 무료구분

    @Column(name = "subway", length = 200)
    private String subway; // 지하철

    @Column(name = "bus_stop", length = 200)
    private String busStop; // 버스정거장

    @Column(name = "yellow_line", length = 200)
    private String yellowLine; // 노란색 지하철 노선

    @Column(name = "green_line", length = 200)
    private String greenLine; // 초록색 지하철 노선

    @Column(name = "blue_line", length = 200)
    private String blueLine; // 파란색 지하철 노선

    @Column(name = "red_line", length = 200)
    private String redLine; // 빨간색 지하철 노선

    @Column(name = "airport_bus", length = 200)
    private String airportBus; // 공항버스

    @Column(name = "external_id", length = 100, unique = true)
    private String externalId; // 외부 시스템 고유 ID (중복 방지용)

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public CulturalSpace(Integer num, String subjCode, String facilityName, String address, String district,
                         BigDecimal longitude, BigDecimal latitude, String phone, String fax, String homepage,
                         String openHour, String entranceFee, String closeDay, String openDay, String seatCount,
                         String mainImage, String etcDesc, String facilityDesc, String isFree, String subway,
                         String busStop, String yellowLine, String greenLine, String blueLine, String redLine,
                         String airportBus, String externalId) {
        this.num = num;
        this.subjCode = subjCode;
        this.facilityName = facilityName;
        this.address = address;
        this.district = district;
        this.longitude = longitude;
        this.latitude = latitude;
        this.phone = phone;
        this.fax = fax;
        this.homepage = homepage;
        this.openHour = openHour;
        this.entranceFee = entranceFee;
        this.closeDay = closeDay;
        this.openDay = openDay;
        this.seatCount = seatCount;
        this.mainImage = mainImage;
        this.etcDesc = etcDesc;
        this.facilityDesc = facilityDesc;
        this.isFree = isFree;
        this.subway = subway;
        this.busStop = busStop;
        this.yellowLine = yellowLine;
        this.greenLine = greenLine;
        this.blueLine = blueLine;
        this.redLine = redLine;
        this.airportBus = airportBus;
        this.externalId = externalId;
    }

    public boolean isFreeSpace() {
        return "무료".equals(this.isFree);
    }
}