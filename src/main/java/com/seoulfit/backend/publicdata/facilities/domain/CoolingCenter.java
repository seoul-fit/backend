package com.seoulfit.backend.publicdata.facilities.domain;

import com.seoulfit.backend.location.util.GeoUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 무더위쉼터 엔티티
 * 
 * 서울시 무더위쉼터 정보를 저장하는 엔티티
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Entity
@Table(name = "cooling_centers", indexes = {
    @Index(name = "idx_cooling_centers_location", columnList = "latitude, longitude"),
    @Index(name = "idx_cooling_centers_name", columnList = "name"),
    @Index(name = "idx_cooling_centers_type", columnList = "facility_type1, facility_type2")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoolingCenter implements GeoUtils.GeoPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "facility_year")
    private Integer facilityYear; // 시설년도

    @Column(name = "area_code", length = 50)
    private String areaCode; // 위치코드

    @Column(name = "facility_type1", length = 100)
    private String facilityType1; // 시설구분1

    @Column(name = "facility_type2", length = 100)
    private String facilityType2; // 시설구분2

    @Column(name = "name", nullable = false, length = 200)
    private String name; // 쉼터명칭

    @Column(name = "road_address", length = 300)
    private String roadAddress; // 도로명주소

    @Column(name = "lot_address", length = 300)
    private String lotAddress; // 지번주소

    @Column(name = "area_size", length = 100)
    private String areaSize; // 시설면적

    @Column(name = "capacity")
    private Integer capacity; // 이용가능인원

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks; // 비고

    @Column(name = "longitude")
    private Double longitude; // 경도

    @Column(name = "latitude")
    private Double latitude; // 위도

    @Column(name = "map_coord_x")
    private Double mapCoordX; // X좌표(EPSG:5186)

    @Column(name = "map_coord_y")
    private Double mapCoordY; // Y좌표(EPSG:5186)

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public CoolingCenter(Integer facilityYear, String areaCode, String facilityType1,
                        String facilityType2, String name, String roadAddress, String lotAddress,
                        String areaSize, Integer capacity, String remarks, Double longitude,
                        Double latitude, Double mapCoordX, Double mapCoordY) {
        this.facilityYear = facilityYear;
        this.areaCode = areaCode;
        this.facilityType1 = facilityType1;
        this.facilityType2 = facilityType2;
        this.name = name;
        this.roadAddress = roadAddress;
        this.lotAddress = lotAddress;
        this.areaSize = areaSize;
        this.capacity = capacity;
        this.remarks = remarks;
        this.longitude = longitude;
        this.latitude = latitude;
        this.mapCoordX = mapCoordX;
        this.mapCoordY = mapCoordY;
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
     * 수용 인원이 있는지 확인
     */
    public boolean hasCapacity() {
        return capacity != null && capacity > 0;
    }

    /**
     * 실내 시설인지 확인
     */
    public boolean isIndoorFacility() {
        return facilityType1 != null && 
               (facilityType1.contains("실내") || facilityType1.contains("건물"));
    }

    /**
     * 엔티티 업데이트
     */
    public void update(String name, String roadAddress, String facilityType1,
                      String facilityType2, Integer capacity, Double longitude, Double latitude) {
        this.name = name;
        this.roadAddress = roadAddress;
        this.facilityType1 = facilityType1;
        this.facilityType2 = facilityType2;
        this.capacity = capacity;
        this.longitude = longitude;
        this.latitude = latitude;
        this.updatedAt = LocalDateTime.now();
    }
}
