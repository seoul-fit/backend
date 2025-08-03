package com.seoulfit.backend.presentation;

import com.seoulfit.backend.application.service.FacilityService;
import com.seoulfit.backend.domain.CongestionLevel;
import com.seoulfit.backend.domain.Facility;
import com.seoulfit.backend.user.domain.InterestCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping("/nearby")
    public ResponseEntity<Map<String, List<FacilityMarkerResponse>>> getNearbyFacilities(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "1.0") double radius,
            @RequestParam(required = false) String categories) {

        List<InterestCategory> categoryList = parseCategories(categories);
        List<Facility> facilities = facilityService.findNearbyFacilities(lat, lng, radius, categoryList);

        List<FacilityMarkerResponse> response = facilities.stream()
                .map(facility -> new FacilityMarkerResponse(
                        facility.getId(),
                        facility.getName(),
                        facility.getCategory(),
                        facility.getLatitude(),
                        facility.getLongitude(),
                        facilityService.calculateDistance(lat, lng, facility.getLatitude(), facility.getLongitude()),
                        facility.getCurrentCongestionLevel(),
                        facility.getAddress(),
                        facility.getPhone()
                ))
                .toList();

        return ResponseEntity.ok(Map.of("facilities", response));
    }

    @GetMapping("/{facilityId}")
    public ResponseEntity<FacilityDetailResponse> getFacilityDetail(@PathVariable Long facilityId) {
        Facility facility = facilityService.findByIdWithDetails(facilityId);

        FacilityDetailResponse response = new FacilityDetailResponse(
                facility.getId(),
                facility.getName(),
                facility.getCategory(),
                facility.getDescription(),
                facility.getAddress(),
                facility.getPhone(),
                facility.getOperatingHours(),
                facility.getCurrentCongestionLevel(),
                new ReservationInfo(facility.hasReservation(), facility.getReservationUrl()),
                facility.getAmenities().stream()
                        .map(amenity -> amenity.getAmenityName())
                        .toList()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/districts/{district}")
    public ResponseEntity<List<FacilityMarkerResponse>> getFacilitiesByDistrict(@PathVariable String district) {
        List<Facility> facilities = facilityService.findByDistrict(district);

        List<FacilityMarkerResponse> response = facilities.stream()
                .map(facility -> new FacilityMarkerResponse(
                        facility.getId(),
                        facility.getName(),
                        facility.getCategory(),
                        facility.getLatitude(),
                        facility.getLongitude(),
                        0.0, // 거리 정보 없음
                        facility.getCurrentCongestionLevel(),
                        facility.getAddress(),
                        facility.getPhone()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    private List<InterestCategory> parseCategories(String categories) {
        if (categories == null || categories.isEmpty()) {
            return Arrays.asList(InterestCategory.values());
        }

        return Arrays.stream(categories.split(","))
                .map(String::trim)
                .map(InterestCategory::valueOf)
                .toList();
    }

    public record FacilityMarkerResponse(
            Long id,
            String name,
            InterestCategory category,
            BigDecimal latitude,
            BigDecimal longitude,
            double distance,
            CongestionLevel congestionLevel,
            String address,
            String phone
    ) {}

    public record FacilityDetailResponse(
            Long id,
            String name,
            InterestCategory category,
            String description,
            String address,
            String phone,
            String operatingHours,
            CongestionLevel congestionLevel,
            ReservationInfo reservationInfo,
            List<String> amenities
    ) {}

    public record ReservationInfo(
            boolean available,
            String reservationUrl
    ) {}
}
