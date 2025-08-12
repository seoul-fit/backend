package com.seoulfit.backend.publicdata.park.domain;

import com.seoulfit.backend.location.util.GeoUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공원 엔티티
 * 
 * 서울시 주요 공원 현황 정보를 저장하는 엔티티
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Entity
@Table(name = "parks", indexes = {
    @Index(name = "idx_parks_location", columnList = "latitude, longitude"),
    @Index(name = "idx_parks_name", columnList = "name"),
    @Index(name = "idx_parks_zone", columnList = "zone")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Park implements GeoUtils.GeoPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "park_idx")
    private Integer parkIdx; // 연번(공원번호)

    @Column(name = "name", nullable = false, length = 200)
    private String name; // 공원명

    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // 공원개요

    @Column(name = "area", length = 500)
    private String area; // 면적

    @Column(name = "open_date")
    private String openDate; // 개원일

    @Column(name = "main_equipment", columnDefinition = "TEXT")
    private String mainEquipment; // 주요시설

    @Column(name = "main_plants", columnDefinition = "TEXT")
    private String mainPlants; // 주요식물

    @Column(name = "guidance", length = 500)
    private String guidance; // 안내도

    @Column(name = "visit_road", columnDefinition = "TEXT")
    private String visitRoad; // 오시는길

    @Column(name = "use_reference", columnDefinition = "TEXT")
    private String useReference; // 이용시참고사항

    @Column(name = "image_url", length = 500)
    private String imageUrl; // 이미지

    @Column(name = "zone", length = 100)
    private String zone; // 지역

    @Column(name = "address", length = 300)
    private String address; // 공원주소

    @Column(name = "management_dept", length = 100)
    private String managementDept; // 관리부서

    @Column(name = "admin_tel", length = 50)
    private String adminTel; // 전화번호

    @Column(name = "grs80_longitude")
    private Double grs80Longitude; // X좌표(GRS80TM)

    @Column(name = "grs80_latitude")
    private Double grs80Latitude; // Y좌표(GRS80TM)

    @Column(name = "longitude")
    private Double longitude; // X좌표(WGS84)

    @Column(name = "latitude")
    private Double latitude; // Y좌표(WGS84)

    @Column(name = "template_url", length = 500)
    private String templateUrl; // 바로가기

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Park(Integer parkIdx, String name, String content, String area, String openDate,
               String mainEquipment, String mainPlants, String guidance, String visitRoad,
               String useReference, String imageUrl, String zone, String address,
               String managementDept, String adminTel, Double grs80Longitude, Double grs80Latitude,
               Double longitude, Double latitude, String templateUrl) {
        this.parkIdx = parkIdx;
        this.name = name;
        this.content = content;
        this.area = area;
        this.openDate = openDate;
        this.mainEquipment = mainEquipment;
        this.mainPlants = mainPlants;
        this.guidance = guidance;
        this.visitRoad = visitRoad;
        this.useReference = useReference;
        this.imageUrl = imageUrl;
        this.zone = zone;
        this.address = address;
        this.managementDept = managementDept;
        this.adminTel = adminTel;
        this.grs80Longitude = grs80Longitude;
        this.grs80Latitude = grs80Latitude;
        this.longitude = longitude;
        this.latitude = latitude;
        this.templateUrl = templateUrl;
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
        return adminTel != null && !adminTel.trim().isEmpty();
    }

    /**
     * 이미지가 있는지 확인
     */
    public boolean hasImage() {
        return imageUrl != null && !imageUrl.trim().isEmpty();
    }

    /**
     * 엔티티 업데이트
     */
    public void update(String name, String content, String area, String mainEquipment,
                      String address, String adminTel, Double longitude, Double latitude) {
        this.name = name;
        this.content = content;
        this.area = area;
        this.mainEquipment = mainEquipment;
        this.address = address;
        this.adminTel = adminTel;
        this.longitude = longitude;
        this.latitude = latitude;
        this.updatedAt = LocalDateTime.now();
    }
}
