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
 * @author Seoul Fit
 * @since 1.0.0
 */
@Entity
@Table(name = "sports_reservation_facilities", indexes = {
    @Index(name = "idx_sports_res_facilities_location", columnList = "latitude, longitude"),
    @Index(name = "idx_sports_res_facilities_name", columnList = "facility_name"),
    @Index(name = "idx_sports_res_facilities_district", columnList = "district")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SportsFacility implements GeoUtils.GeoPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_id")
    private String serviceId; // 서비스ID

    @Column(name = "district", length = 50)
    private String district; // 자치구

    @Column(name = "facility_name", nullable = false, length = 200)
    private String facilityName; // 시설명

    @Column(name = "facility_type", length = 100)
    private String facilityType; // 시설유형

    @Column(name = "x_coordinate", length = 50)
    private String xCoordinate; // X좌표

    @Column(name = "address", length = 300)
    private String address; // 시설주소

    @Column(name = "y_coordinate", length = 50)
    private String yCoordinate; // Y좌표

    @Column(name = "facility_scale", length = 200)
    private String facilityScale; // 시설규모

    @Column(name = "detail_content", length = 2000)
    private String detailContent; // 상세내용

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

    @Column(name = "image_url", length = 500)
    private String imageUrl; // 이미지URL

    @Column(name = "parking_info", length = 300)
    private String parkingInfo; // 주차정보


    @Column(name = "latitude")
    private Double latitude; // 위도

    @Column(name = "longitude")
    private Double longitude; // 경도

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public SportsFacility(String serviceId, String district, String facilityName, String facilityType,
                         String address, String facilityScale, String phoneNumber, String operatingHours,
                         String holiday, String feeInfo, String parkingInfo, String homepageUrl,
                         String detailContent, String imageUrl, String xCoordinate, String yCoordinate,
                         Double latitude, Double longitude) {
        this.serviceId = serviceId;
        this.district = district;
        this.facilityName = facilityName;
        this.facilityType = facilityType;
        this.address = address;
        this.facilityScale = facilityScale;
        this.phoneNumber = phoneNumber;
        this.operatingHours = operatingHours;
        this.holiday = holiday;
        this.feeInfo = feeInfo;
        this.parkingInfo = parkingInfo;
        this.homepageUrl = homepageUrl;
        this.detailContent = detailContent;
        this.imageUrl = imageUrl;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
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
        return phoneNumber != null && !phoneNumber.trim().isEmpty();
    }

    /**
     * 홈페이지가 있는지 확인
     */
    public boolean hasWebsite() {
        return homepageUrl != null && !homepageUrl.trim().isEmpty();
    }

    /**
     * 운영 중인지 확인
     */
    public boolean isOperating() {
        return true; // sports_reservation_facilities 테이블은 운영 상태 컬럼이 없음
    }

    /**
     * 엔티티 업데이트
     */
    public void update(String facilityName, String address, String phoneNumber, String operatingHours,
                      String holiday, String feeInfo, Double latitude, Double longitude) {
        this.facilityName = facilityName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.operatingHours = operatingHours;
        this.holiday = holiday;
        this.feeInfo = feeInfo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedAt = LocalDateTime.now();
    }
}
