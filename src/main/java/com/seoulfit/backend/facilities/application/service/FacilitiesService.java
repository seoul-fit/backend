package com.seoulfit.backend.facilities.application.service;

import com.seoulfit.backend.facilities.application.port.in.GetCoolingShelterUseCase;
import com.seoulfit.backend.facilities.application.port.out.LoadAmenitiesPort;
import com.seoulfit.backend.facilities.domain.CoolingShelter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 편의시설 애플리케이션 서비스
 * 헥사고날 아키텍처의 Application Layer
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FacilitiesService implements GetCoolingShelterUseCase {
    
    private final LoadAmenitiesPort loadAmenitiesPort;

    @Override
    public List<CoolingShelter> getAmenities(GetAmenitiesQuery query) {
        log.info("Fetching amenities with query: {}", query);
        
        try {
            List<CoolingShelter> coolingShelterList = loadAmenitiesPort.loadAmenities(
                query.startIndex(), 
                query.endIndex()
            );
            
/*            // 필터링 로직 적용
            List<CoolingShelter> filteredAmenities = amenities.stream()
                .filter(amenity -> query.district() == null || 
                    query.district().equals(amenity.getDistrict()))
                .filter(amenity -> query.facilityType() == null || 
                    query.facilityType().equals(amenity.getFacilityType()))
                .collect(Collectors.toList());*/
            
            log.info("Successfully retrieved {} amenities", coolingShelterList.size());
            return coolingShelterList;
            
        } catch (Exception e) {
            log.error("Error fetching amenities: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch amenities data: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CoolingShelter> getAmenitiesNearby(BigDecimal latitude, BigDecimal longitude, double radiusKm) {
        log.info("Fetching amenities near location: lat={}, lon={}, radius={}km", 
            latitude, longitude, radiusKm);
        
        try {
            // 전체 편의시설 조회 후 거리 기반 필터링
            List<CoolingShelter> allAmenities = loadAmenitiesPort.loadAmenities(1, 1000);
            
            List<CoolingShelter> nearbyAmenities = allAmenities.stream()
                .filter(amenity -> amenity.isWithinRange(latitude, longitude, radiusKm))
                .collect(Collectors.toList());
            
            log.info("Found {} amenities within {}km radius", nearbyAmenities.size(), radiusKm);
            return nearbyAmenities;
            
        } catch (Exception e) {
            log.error("Error fetching nearby amenities: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch nearby amenities: " + e.getMessage(), e);
        }
    }
}
