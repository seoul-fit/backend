package com.seoulfit.backend.domain;

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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facilities",
        indexes = {
                @Index(name = "idx_location", columnList = "latitude, longitude"),
                @Index(name = "idx_category", columnList = "category"),
                @Index(name = "idx_district", columnList = "district")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, length = 50)
    private String district;

    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(length = 20)
    private String phone;

    @Column(name = "operating_hours")
    private String operatingHours;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(name = "reservation_url", length = 500)
    private String reservationUrl;

    @Column(name = "external_id", length = 100)
    private String externalId;

    @Column(name = "data_source", nullable = false, length = 50)
    private String dataSource;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacilityAmenity> amenities = new ArrayList<>();

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL)
    private List<CongestionData> congestionData = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Facility(String name, InterestCategory category, String description, String address,
                   String district, BigDecimal latitude, BigDecimal longitude, String phone,
                   String operatingHours, String websiteUrl, String reservationUrl,
                   String externalId, String dataSource) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.address = address;
        this.district = district;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.operatingHours = operatingHours;
        this.websiteUrl = websiteUrl;
        this.reservationUrl = reservationUrl;
        this.externalId = externalId;
        this.dataSource = dataSource;
    }

    public void addAmenity(String amenityName) {
        FacilityAmenity amenity = FacilityAmenity.builder()
                .facility(this)
                .amenityName(amenityName)
                .build();
        this.amenities.add(amenity);
    }

    public boolean hasReservation() {
        return this.reservationUrl != null && !this.reservationUrl.isEmpty();
    }

    public CongestionLevel getCurrentCongestionLevel() {
        return this.congestionData.stream()
                .filter(data -> data.getRecordedAt().isAfter(LocalDateTime.now().minusHours(1)))
                .findFirst()
                .map(CongestionData::getCongestionLevel)
                .orElse(CongestionLevel.LOW);
    }
}
