package com.seoulfit.backend.publicdata.sports.domain;

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
 * 서울시 체육시설 정보를 저장하는 엔티티
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Entity
@Table(name = "sports_reservation_facilities", indexes = {
    @Index(name = "idx_sports_res_location", columnList = "latitude, longitude"),
    @Index(name = "idx_sports_res_name", columnList = "facility_name"),
    @Index(name = "idx_sports_res_type", columnList = "facility_type")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sports implements GeoUtils.GeoPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "facility_name", nullable = false, length = 200)
    private String facilityName; // 시설명

    @Column(name = "facility_type", length = 100)
    private String facilityType; // 시설유형

    @Column(name = "address", length = 300)
    private String address; // 주소

    @Column(name = "phone_number", length = 50)
    private String phoneNumber; // 전화번호

    @Column(name = "operating_hours", length = 200)
    private String operatingHours; // 운영시간

    @Column(name = "holiday", length = 100)
    private String holiday; // 휴무일

    @Column(name = "fee_info", length = 500)
    private String feeInfo; // 이용료 정보

    @Column(name = "homepage_url", length = 500)
    private String homepageUrl; // 홈페이지 URL

    @Column(name = "latitude")
    private Double latitude; // 위도

    @Column(name = "longitude")
    private Double longitude; // 경도

    @Column(name = "district", length = 50)
    private String district; // 자치구

    @Column(name = "facility_scale", length = 200)
    private String facilityScale; // 시설규모

    @Column(name = "parking_info", length = 200)
    private String parkingInfo; // 주차정보

    @Column(name = "detail_content", length = 2000)
    private String detailContent; // 상세내용

    @Column(name = "image_url", length = 500)
    private String imageUrl; // 이미지URL

    @Column(name = "x_coordinate", length = 50)
    private String xCoordinate; // X좌표

    @Column(name = "y_coordinate", length = 50)
    private String yCoordinate; // Y좌표

    @Column(name = "service_id", length = 50)
    private String serviceId; // 서비스ID

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Sports(String facilityName, String facilityType, String address, String phoneNumber,
                  String operatingHours, String holiday, String feeInfo, String homepageUrl,
                  Double latitude, Double longitude, String district, String facilityScale,
                  String parkingInfo, String detailContent, String imageUrl, String xCoordinate,
                  String yCoordinate, String serviceId) {
        this.facilityName = facilityName;
        this.facilityType = facilityType;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.operatingHours = operatingHours;
        this.holiday = holiday;
        this.feeInfo = feeInfo;
        this.homepageUrl = homepageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.district = district;
        this.facilityScale = facilityScale;
        this.parkingInfo = parkingInfo;
        this.detailContent = detailContent;
        this.imageUrl = imageUrl;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.serviceId = serviceId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 위경도 정보 업데이트
     */
    public void updateLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 시설 정보 업데이트
     */
    public void updateInfo(String facilityName, String facilityType, String address, 
                          String phoneNumber, String operatingHours, String holiday, 
                          String feeInfo, String homepageUrl, String district, 
                          String facilityScale, String parkingInfo, String detailContent,
                          String imageUrl, String xCoordinate, String yCoordinate, String serviceId) {
        this.facilityName = facilityName;
        this.facilityType = facilityType;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.operatingHours = operatingHours;
        this.holiday = holiday;
        this.feeInfo = feeInfo;
        this.homepageUrl = homepageUrl;
        this.district = district;
        this.facilityScale = facilityScale;
        this.parkingInfo = parkingInfo;
        this.detailContent = detailContent;
        this.imageUrl = imageUrl;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.serviceId = serviceId;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public double getLatitude() {
        return latitude != null ? latitude : 0.0;
    }

    @Override
    public double getLongitude() {
        return longitude != null ? longitude : 0.0;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
