package com.seoulfit.backend.restaurant.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 서울시 관광 음식 정보 도메인 엔티티
 * 서울시 공공 데이터 API에서 제공하는 관광 음식점 정보를 저장
 */
@Entity
@Table(name = "tourist_restaurants", indexes = {
    @Index(name = "idx_restaurant_date", columnList = "dataDate"),
    @Index(name = "idx_restaurant_name", columnList = "restaurantName"),
    @Index(name = "idx_restaurant_lang", columnList = "langCodeId"),
    @Index(name = "idx_restaurant_post_sn", columnList = "postSn")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TouristRestaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 고유번호 (API의 POST_SN)
     */
    @Column(name = "post_sn", nullable = false, length = 50)
    private String postSn;

    /**
     * 언어 (API의 LANG_CODE_ID)
     */
    @Column(name = "lang_code_id", length = 10)
    private String langCodeId;

    /**
     * 상호명 (API의 POST_SJ)
     */
    @Column(name = "restaurant_name", nullable = false, length = 200)
    private String restaurantName;

    /**
     * 콘텐츠URL (API의 POST_URL)
     */
    @Column(name = "post_url", length = 500)
    private String postUrl;

    /**
     * 주소 (API의 ADDRESS)
     */
    @Column(name = "address", length = 300)
    private String address;

    /**
     * 신주소 (API의 NEW_ADDRESS)
     */
    @Column(name = "new_address", length = 300)
    private String newAddress;

    /**
     * 전화번호 (API의 CMMN_TELNO)
     */
    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    /**
     * 웹사이트 (API의 CMMN_HMPG_URL)
     */
    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    /**
     * 운영시간 (API의 CMMN_USE_TIME)
     */
    @Column(name = "operating_hours", columnDefinition = "TEXT")
    private String operatingHours;

    /**
     * 교통정보 (API의 SUBWAY_INFO)
     */
    @Column(name = "subway_info", columnDefinition = "TEXT")
    private String subwayInfo;

    /**
     * 홈페이지 언어 (API의 CMMN_HMPG_LANG)
     */
    @Column(name = "homepage_language", length = 50)
    private String homepageLanguage;

    /**
     * 대표메뉴 (API의 FD_REPRSNT_MENU)
     */
    @Column(name = "representative_menu", columnDefinition = "TEXT")
    private String representativeMenu;

    /**
     * 데이터 수집 날짜 (YYYYMMDD 형식)
     */
    @Column(name = "data_date", nullable = false, length = 8)
    private String dataDate;

    /**
     * 생성 일시
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public TouristRestaurant(String postSn, String langCodeId, String restaurantName,
                            String postUrl, String address, String newAddress,
                            String phoneNumber, String websiteUrl, String operatingHours,
                            String subwayInfo, String homepageLanguage, String representativeMenu,
                            String dataDate) {
        this.postSn = postSn;
        this.langCodeId = langCodeId;
        this.restaurantName = restaurantName;
        this.postUrl = postUrl;
        this.address = address;
        this.newAddress = newAddress;
        this.phoneNumber = phoneNumber;
        this.websiteUrl = websiteUrl;
        this.operatingHours = operatingHours;
        this.subwayInfo = subwayInfo;
        this.homepageLanguage = homepageLanguage;
        this.representativeMenu = representativeMenu;
        this.dataDate = dataDate;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 엔티티 업데이트
     */
    public void update(String langCodeId, String restaurantName, String postUrl,
                      String address, String newAddress, String phoneNumber,
                      String websiteUrl, String operatingHours, String subwayInfo,
                      String homepageLanguage, String representativeMenu) {
        this.langCodeId = langCodeId;
        this.restaurantName = restaurantName;
        this.postUrl = postUrl;
        this.address = address;
        this.newAddress = newAddress;
        this.phoneNumber = phoneNumber;
        this.websiteUrl = websiteUrl;
        this.operatingHours = operatingHours;
        this.subwayInfo = subwayInfo;
        this.homepageLanguage = homepageLanguage;
        this.representativeMenu = representativeMenu;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 웹사이트가 있는지 확인
     */
    public boolean hasWebsite() {
        return websiteUrl != null && !websiteUrl.trim().isEmpty();
    }

    /**
     * 전화번호가 있는지 확인
     */
    public boolean hasPhoneNumber() {
        return phoneNumber != null && !phoneNumber.trim().isEmpty();
    }

    /**
     * 특정 언어인지 확인
     */
    public boolean isLanguage(String targetLang) {
        return langCodeId != null && langCodeId.equals(targetLang);
    }

    /**
     * 한국어 정보인지 확인
     */
    public boolean isKorean() {
        return isLanguage("ko") || isLanguage("KO");
    }

    /**
     * 영어 정보인지 확인
     */
    public boolean isEnglish() {
        return isLanguage("en") || isLanguage("EN");
    }

    /**
     * 대표메뉴가 있는지 확인
     */
    public boolean hasRepresentativeMenu() {
        return representativeMenu != null && !representativeMenu.trim().isEmpty();
    }

    /**
     * 교통정보가 있는지 확인
     */
    public boolean hasSubwayInfo() {
        return subwayInfo != null && !subwayInfo.trim().isEmpty();
    }
}
