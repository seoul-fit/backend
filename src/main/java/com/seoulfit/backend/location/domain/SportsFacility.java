package com.seoulfit.backend.location.domain;

import com.seoulfit.backend.location.util.GeoUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 체육시설 엔티티
 * 
 * 서울시 공공체육시설 정보를 저장하는 엔티티
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Entity
@Table(name = "sports_facilities", indexes = {
    @Index(name = "idx_sports_facilities_location", columnList = "latitude, longitude"),
    @Index(name = "idx_sports_facilities_name", columnList = "name"),
    @Index(name = "idx_sports_facilities_district", columnList = "district")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SportsFacility implements GeoUtils.GeoPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "facility_idx")
    private String facilityIdx; // 체육시설일련번호

    @Column(name = "district", length = 50)
    private String district; // 자치구

    @Column(name = "name", nullable = false, length = 200)
    private String name; // 시설명

    @Column(name = "facility_type", length = 100)
    private String facilityType; // 시설유형

    @Column(name = "postal_code", length = 10)
    private String postalCode; // 시설우편번호

    @Column(name = "address", length = 300)
    private String address; // 시설주소

    @Column(name = "address_detail", length = 300)
    private String addressDetail; // 시설상세주소

    @Column(name = "facility_size", length = 200)
    private String facilitySize; // 시설규모

    @Column(name = "operating_org", length = 200)
    private String operatingOrg; // 운영기관

    @Column(name = "phone", length = 50)
    private String phone; // 연락처

    @Column(name = "weekday_hours", length = 200)
    private String weekdayHours; // 운영시간_평일

    @Column(name = "weekend_hours", length = 200)
    private String weekendHours; // 운영시간_주말

    @Column(name = "holiday_hours", length = 200)
    private String holidayHours; // 운영시간_공휴일

    @Column(name = "rental_available", length = 50)
    private String rentalAvailable; // 시설대관여부

    @Column(name = "usage_fee", length = 300)
    private String usageFee; // 시설사용료

    @Column(name = "parking_info", length = 300)
    private String parkingInfo; // 주차정보

    @Column(name = "homepage", length = 500)
    private String homepage; // 홈페이지

    @Column(name = "facility_kind", length = 100)
    private String facilityKind; // 시설종류

    @Column(name = "operation_status", length = 50)
    private String operationStatus; // 시설운영상태

    @Column(name = "amenities", columnDefinition = "TEXT")
    private String amenities; // 시설편의시설

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks; // 비고

    @Column(name = "latitude")
    private Double latitude; // 위도

    @Column(name = "longitude")
    private Double longitude; // 경도

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public SportsFacility(String facilityIdx, String district, String name, String facilityType,
                         String postalCode, String address, String addressDetail, String facilitySize,
                         String operatingOrg, String phone, String weekdayHours, String weekendHours,
                         String holidayHours, String rentalAvailable, String usageFee, String parkingInfo,
                         String homepage, String facilityKind, String operationStatus, String amenities,
                         String remarks, Double latitude, Double longitude) {
        this.facilityIdx = facilityIdx;
        this.district = district;
        this.name = name;
        this.facilityType = facilityType;
        this.postalCode = postalCode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.facilitySize = facilitySize;
        this.operatingOrg = operatingOrg;
        this.phone = phone;
        this.weekdayHours = weekdayHours;
        this.weekendHours = weekendHours;
        this.holidayHours = holidayHours;
        this.rentalAvailable = rentalAvailable;
        this.usageFee = usageFee;
        this.parkingInfo = parkingInfo;
        this.homepage = homepage;
        this.facilityKind = facilityKind;
        this.operationStatus = operationStatus;
        this.amenities = amenities;
        this.remarks = remarks;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * GeoPoint 인터페이스 구현 - 위도 반환
     */
    @Override
    public double getLatitude() {
        return latitude != null ? latitude : 0.0;
    }

    /**
     * GeoPoint 인터페이스 구현 - 경도 반환
     */
    @Override
    public double getLongitude() {
        return longitude != null ? longitude : 0.0;
    }

    /**
     * 위치 정보가 있는지 확인
     */
    public boolean hasLocation() {
        return latitude != null && longitude != null;
    }

    /**
     * 연락처 정보가 있는지 확인
     */
    public boolean hasContact() {
        return phone != null && !phone.trim().isEmpty();
    }

    /**
     * 홈페이지가 있는지 확인
     */
    public boolean hasWebsite() {
        return homepage != null && !homepage.trim().isEmpty();
    }

    /**
     * 운영 중인지 확인
     */
    public boolean isOperating() {
        return "운영".equals(operationStatus) || "정상운영".equals(operationStatus);
    }

    /**
     * 엔티티 업데이트
     */
    public void update(String name, String address, String phone, String weekdayHours,
                      String weekendHours, String usageFee, Double latitude, Double longitude) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.weekdayHours = weekdayHours;
        this.weekendHours = weekendHours;
        this.usageFee = usageFee;
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedAt = LocalDateTime.now();
    }
}
