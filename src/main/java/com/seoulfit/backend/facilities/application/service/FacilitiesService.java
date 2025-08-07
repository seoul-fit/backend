package com.seoulfit.backend.facilities.application.service;

import com.seoulfit.backend.facilities.application.port.in.CommandCoolingShelterUseCase;
import com.seoulfit.backend.facilities.application.port.out.CommandCoolingShelterPort;
import com.seoulfit.backend.facilities.application.port.out.LoadCoolingShelterPort;
import com.seoulfit.backend.facilities.domain.CoolingShelter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class FacilitiesService implements CommandCoolingShelterUseCase {
    
    private final LoadCoolingShelterPort loadCoolingShelterPort;
    private final CommandCoolingShelterPort commandCoolingShelterPort;

    @Transactional
    @Override
    public List<CoolingShelter> saveCoolingShelter(GetAmenitiesQuery query) {
        try {
            List<CoolingShelter> coolingShelterList = loadCoolingShelterPort.loadAmenities(
                query.startIndex(), 
                query.endIndex()
            );

            log.info("Successfully Cooling-Shelter Size : {}", coolingShelterList.size());

            commandCoolingShelterPort.truncate();
            commandCoolingShelterPort.save(coolingShelterList);

            return coolingShelterList;
            
        } catch (Exception e) {
            log.error("Error fetching Cooling-Shelter: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch Cooling-Shelter data: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<CoolingShelter> getAmenitiesNearby(BigDecimal latitude, BigDecimal longitude, double radiusKm) {
        log.info("Fetching amenities near location: lat={}, lon={}, radius={}km", 
            latitude, longitude, radiusKm);
        
        try {
            // 전체 편의시설 조회 후 거리 기반 필터링
            List<CoolingShelter> allAmenities = loadCoolingShelterPort.loadAmenities(1, 1000);
            
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
