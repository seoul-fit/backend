package com.seoulfit.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "facility_amenities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FacilityAmenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Column(name = "amenity_name", nullable = false, length = 100)
    private String amenityName;

    @Builder
    public FacilityAmenity(Facility facility, String amenityName) {
        this.facility = facility;
        this.amenityName = amenityName;
    }
}
