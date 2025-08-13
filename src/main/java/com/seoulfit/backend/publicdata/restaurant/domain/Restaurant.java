package com.seoulfit.backend.publicdata.restaurant.domain;

import com.seoulfit.backend.location.util.GeoUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 맛집 엔티티
 * 
 * 서울시 관광 음식점 정보를 저장하는 엔티티
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Entity
@Table(name = "restaurants", indexes = {
    @Index(name = "idx_restaurants_location", columnList = "latitude, longitude"),
    @Index(name = "idx_restaurants_name", columnList = "name")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant implements GeoUtils.GeoPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_sn", length = 50)
    private String postSn; // 고유번호

    @Column(name = "lang_code_id", length = 10)
    private String langCodeId; // 언어

    @Column(name = "name", nullable = false, length = 200)
    private String name; // 상호명

    @Column(name = "post_url", length = 500)
    private String postUrl; // 콘텐츠URL

    @Column(name = "address", length = 300)
    private String address; // 주소

    @Column(name = "new_address", length = 300)
    private String newAddress; // 신주소

    @Column(name = "phone", length = 200)
    private String phone; // 전화번호

    @Column(name = "website", length = 500)
    private String website; // 웹사이트

    @Column(name = "operating_hours", length = 500)
    private String operatingHours; // 운영시간

    @Column(name = "subway_info", length = 300)
    private String subwayInfo; // 교통정보

    @Column(name = "homepage_lang", length = 50)
    private String homepageLang; // 홈페이지 언어

    @Column(name = "representative_menu", length = 500)
    private String representativeMenu; // 대표메뉴

    @Column(name = "latitude")
    private Double latitude; // 위도

    @Column(name = "longitude")
    private Double longitude; // 경도

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Restaurant(String postSn, String langCodeId, String name, String postUrl,
                     String address, String newAddress, String phone, String website,
                     String operatingHours, String subwayInfo, String homepageLang,
                     String representativeMenu, Double latitude, Double longitude) {
        this.postSn = postSn;
        this.langCodeId = langCodeId;
        this.name = name;
        this.postUrl = postUrl;
        this.address = address;
        this.newAddress = newAddress;
        this.phone = phone;
        this.website = website;
        this.operatingHours = operatingHours;
        this.subwayInfo = subwayInfo;
        this.homepageLang = homepageLang;
        this.representativeMenu = representativeMenu;
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
     * 웹사이트가 있는지 확인
     */
    public boolean hasWebsite() {
        return website != null && !website.trim().isEmpty();
    }

    /**
     * 엔티티 업데이트
     */
    public void update(String name, String address, String phone, String website,
                      String operatingHours, String representativeMenu,
                      Double latitude, Double longitude) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.website = website;
        this.operatingHours = operatingHours;
        this.representativeMenu = representativeMenu;
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedAt = LocalDateTime.now();
    }
}
