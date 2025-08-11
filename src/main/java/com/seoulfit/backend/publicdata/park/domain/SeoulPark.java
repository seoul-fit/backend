package com.seoulfit.backend.publicdata.park.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 서울시 공원 정보 도메인 엔티티
 * 서울시 공공 데이터 API에서 제공하는 공원 현황 정보를 저장
 */
@Entity
@Table(name = "seoul_parks", indexes = {
    @Index(name = "idx_seoul_park_date", columnList = "dataDate"),
    @Index(name = "idx_seoul_park_zone", columnList = "zone"),
    @Index(name = "idx_seoul_park_name", columnList = "parkName")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeoulPark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 공원 번호 (API의 P_IDX)
     */
    @Column(name = "park_idx")
    private Long parkIdx;

    /**
     * 공원명 (API의 P_PARK)
     */
    @Column(name = "park_name", nullable = false, length = 200)
    private String parkName;

    /**
     * 공원 개요 (API의 P_LIST_CONTENT)
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 면적 (API의 AREA)
     */
    @Column(name = "area", length = 100)
    private String area;

    /**
     * 개원일 (API의 OPEN_DT)
     */
    @Column(name = "open_date", length = 50)
    private String openDate;

    /**
     * 주요시설 (API의 MAIN_EQUIP)
     */
    @Column(name = "main_equipment", columnDefinition = "TEXT")
    private String mainEquipment;

    /**
     * 주요식물 (API의 MAIN_PLANTS)
     */
    @Column(name = "main_plants", columnDefinition = "TEXT")
    private String mainPlants;

    /**
     * 안내도 (API의 GUIDANCE)
     */
    @Column(name = "guidance", columnDefinition = "TEXT")
    private String guidance;

    /**
     * 오시는길 (API의 VISIT_ROAD)
     */
    @Column(name = "visit_road", columnDefinition = "TEXT")
    private String visitRoad;

    /**
     * 이용시 참고사항 (API의 USE_REFER)
     */
    @Column(name = "usage_reference", columnDefinition = "TEXT")
    private String usageReference;

    /**
     * 이미지 URL (API의 P_IMG)
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /**
     * 지역 (API의 P_ZONE)
     */
    @Column(name = "zone", length = 100)
    private String zone;

    /**
     * 공원 주소 (API의 P_ADDR)
     */
    @Column(name = "address", length = 300)
    private String address;

    /**
     * 관리부서 (API의 P_NAME)
     */
    @Column(name = "management_department", length = 200)
    private String managementDepartment;

    /**
     * 전화번호 (API의 P_ADMINTEL)
     */
    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    /**
     * X좌표 (GRS80TM) (API의 G_LONGITUDE)
     */
    @Column(name = "grs80_longitude")
    private Double grs80Longitude;

    /**
     * Y좌표 (GRS80TM) (API의 G_LATITUDE)
     */
    @Column(name = "grs80_latitude")
    private Double grs80Latitude;

    /**
     * X좌표 (WGS84) (API의 LONGITUDE)
     */
    @Column(name = "wgs84_longitude")
    private Double wgs84Longitude;

    /**
     * Y좌표 (WGS84) (API의 LATITUDE)
     */
    @Column(name = "wgs84_latitude")
    private Double wgs84Latitude;

    /**
     * 바로가기 URL (API의 TEMPLATE_URL)
     */
    @Column(name = "template_url", length = 500)
    private String templateUrl;

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
    public SeoulPark(Long parkIdx, String parkName, String description, String area,
                     String openDate, String mainEquipment, String mainPlants,
                     String guidance, String visitRoad, String usageReference,
                     String imageUrl, String zone, String address,
                     String managementDepartment, String phoneNumber,
                     Double grs80Longitude, Double grs80Latitude,
                     Double wgs84Longitude, Double wgs84Latitude,
                     String templateUrl, String dataDate) {
        this.parkIdx = parkIdx;
        this.parkName = parkName;
        this.description = description;
        this.area = area;
        this.openDate = openDate;
        this.mainEquipment = mainEquipment;
        this.mainPlants = mainPlants;
        this.guidance = guidance;
        this.visitRoad = visitRoad;
        this.usageReference = usageReference;
        this.imageUrl = imageUrl;
        this.zone = zone;
        this.address = address;
        this.managementDepartment = managementDepartment;
        this.phoneNumber = phoneNumber;
        this.grs80Longitude = grs80Longitude;
        this.grs80Latitude = grs80Latitude;
        this.wgs84Longitude = wgs84Longitude;
        this.wgs84Latitude = wgs84Latitude;
        this.templateUrl = templateUrl;
        this.dataDate = dataDate;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 엔티티 업데이트
     */
    public void update(String parkName, String description, String area,
                      String openDate, String mainEquipment, String mainPlants,
                      String guidance, String visitRoad, String usageReference,
                      String imageUrl, String zone, String address,
                      String managementDepartment, String phoneNumber,
                      Double grs80Longitude, Double grs80Latitude,
                      Double wgs84Longitude, Double wgs84Latitude,
                      String templateUrl) {
        this.parkName = parkName;
        this.description = description;
        this.area = area;
        this.openDate = openDate;
        this.mainEquipment = mainEquipment;
        this.mainPlants = mainPlants;
        this.guidance = guidance;
        this.visitRoad = visitRoad;
        this.usageReference = usageReference;
        this.imageUrl = imageUrl;
        this.zone = zone;
        this.address = address;
        this.managementDepartment = managementDepartment;
        this.phoneNumber = phoneNumber;
        this.grs80Longitude = grs80Longitude;
        this.grs80Latitude = grs80Latitude;
        this.wgs84Longitude = wgs84Longitude;
        this.wgs84Latitude = wgs84Latitude;
        this.templateUrl = templateUrl;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 좌표 정보가 유효한지 확인
     */
    public boolean hasValidCoordinates() {
        return wgs84Longitude != null && wgs84Latitude != null &&
               wgs84Longitude != 0.0 && wgs84Latitude != 0.0;
    }

    /**
     * 특정 지역에 속하는지 확인
     */
    public boolean isInZone(String targetZone) {
        return zone != null && zone.contains(targetZone);
    }
}
