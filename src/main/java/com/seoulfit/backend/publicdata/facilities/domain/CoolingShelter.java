package com.seoulfit.backend.publicdata.facilities.domain;

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
@Table(name = "cooling_shelters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CoolingShelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "years", length = 4)
    private String year;

    @Column(name = "area_code", length = 20)
    private String areaCode;

    @Column(name = "facility_type1", length = 100)
    private String facilityType1;

    @Column(name = "facility_type2", length = 100)
    private String facilityType2;

    @Column(name = "facility_name", nullable = false, length = 500)
    private String facilityName;

    @Column(name = "detailed_address", length = 500)
    private String detailedAddress;

    @Column(name = "lot_number_address", length = 500)
    private String lotNumberAddress;

    @Column(name = "area_square_meters")
    private String areaSquareMeters;

    @Column(name = "use_person_number")
    private Integer usePersonNumber;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "map_coord_x")
    private Double mapCoordX;

    @Column(name = "map_coord_y")
    private Double mapCoordY;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public CoolingShelter(String year, String areaCode, String facilityType1, String facilityType2,
                          String facilityName, String detailedAddress, String lotNumberAddress,
                          String areaSquareMeters, Integer usePersonNumber,
                          String remark, BigDecimal longitude, BigDecimal latitude,
                          Double mapCoordX, Double mapCoordY) {
        this.year = year;
        this.areaCode = areaCode;
        this.facilityType1 = facilityType1;
        this.facilityType2 = facilityType2;
        this.facilityName = facilityName;
        this.detailedAddress = detailedAddress;
        this.lotNumberAddress = lotNumberAddress;
        this.areaSquareMeters = areaSquareMeters;
        this.usePersonNumber = usePersonNumber;
        this.remark = remark;
        this.longitude = longitude;
        this.latitude = latitude;
        this.mapCoordX = mapCoordX;
        this.mapCoordY = mapCoordY;
    }

    /**
     * 편의시설이 특정 위치 범위 내에 있는지 확인
     */
    public boolean isWithinRange(BigDecimal centerLat, BigDecimal centerLon, double radiusKm) {
        if (latitude == null || longitude == null) {
            return false;
        }

        double distance = calculateDistance(
                centerLat.doubleValue(), centerLon.doubleValue(),
                latitude.doubleValue(), longitude.doubleValue()
        );

        return distance <= radiusKm;
    }

    /**
     * 두 지점 간의 거리 계산 (Haversine formula)
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름 (km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public boolean hasValidLocation() {
        return latitude != null && longitude != null &&
                latitude.doubleValue() >= 37.4 && latitude.doubleValue() <= 37.7 &&
                longitude.doubleValue() >= 126.7 && longitude.doubleValue() <= 127.2;
    }

    public boolean isValid() {
        return facilityName != null && !facilityName.trim().isEmpty() &&
                (detailedAddress != null && !detailedAddress.trim().isEmpty() ||
                        lotNumberAddress != null && !lotNumberAddress.trim().isEmpty());
    }
}