package com.seoulfit.backend.application.service;

import com.seoulfit.backend.domain.Facility;
import com.seoulfit.backend.infra.FacilityRepository;
import com.seoulfit.backend.user.domain.InterestCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public List<Facility> findNearbyFacilities(BigDecimal latitude, BigDecimal longitude, 
                                              double radiusKm, List<InterestCategory> categories) {
        return facilityRepository.findNearbyFacilities(latitude, longitude, radiusKm, categories);
    }

    public Facility findByIdWithDetails(Long facilityId) {
        return facilityRepository.findByIdWithAmenities(facilityId)
                .orElseThrow(() -> new IllegalArgumentException("시설을 찾을 수 없습니다."));
    }

    public List<Facility> findByCategories(List<InterestCategory> categories) {
        return facilityRepository.findByCategoryIn(categories);
    }

    public List<Facility> findByDistrict(String district) {
        return facilityRepository.findByDistrict(district);
    }

    @Transactional
    public Facility saveFacility(Facility facility) {
        return facilityRepository.save(facility);
    }

    public double calculateDistance(BigDecimal lat1, BigDecimal lng1, BigDecimal lat2, BigDecimal lng2) {
        double earthRadius = 6371; // km
        
        double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double dLng = Math.toRadians(lng2.doubleValue() - lng1.doubleValue());
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue())) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return earthRadius * c * 1000; // meters
    }
}
