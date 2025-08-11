package com.seoulfit.backend.publicdata.facilities.application.service;

import com.seoulfit.backend.publicdata.facilities.application.port.in.CommandCoolingShelterUseCase;
import com.seoulfit.backend.publicdata.facilities.application.port.out.CommandCoolingShelterPort;
import com.seoulfit.backend.publicdata.facilities.application.port.out.LoadCoolingShelterPort;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
    public List<CoolingCenter> saveCoolingShelter(GetAmenitiesQuery query) {
        try {
            commandCoolingShelterPort.truncate();
            List<CoolingCenter> coolingShelterList = loadCoolingShelterPort.loadAmenities(
                query.startIndex(), 
                query.endIndex()
            );
            List<CoolingCenter> coolingShelterListV2 = loadCoolingShelterPort.loadAmenities(1001,2000);
            List<CoolingCenter> coolingShelterListV3 = loadCoolingShelterPort.loadAmenities(2001,3000);
            List<CoolingCenter> coolingShelterListV4 = loadCoolingShelterPort.loadAmenities(3001,4000);
            List<CoolingCenter> coolingShelterListV5 = loadCoolingShelterPort.loadAmenities(4001,5000);

            log.info("Successfully Cooling-Shelter Size : {}", coolingShelterList.size());

            commandCoolingShelterPort.save(coolingShelterList);
            commandCoolingShelterPort.save(coolingShelterListV2);
            commandCoolingShelterPort.save(coolingShelterListV3);
            commandCoolingShelterPort.save(coolingShelterListV4);
            commandCoolingShelterPort.save(coolingShelterListV5);

            return coolingShelterList;
            
        } catch (Exception e) {
            log.error("Error fetching Cooling-Shelter: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch Cooling-Shelter data: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<CoolingCenter> getAmenitiesNearby(BigDecimal latitude, BigDecimal longitude, double radiusKm) {
        log.info("Fetching amenities near location: lat={}, lon={}, radius={}km", 
            latitude, longitude, radiusKm);
        
/*        try {
            // 전체 편의시설 조회 후 거리 기반 필터링
            List<CoolingCenter> allAmenities = loadCoolingShelterPort.loadAmenities(1, 1000);
            
            List<CoolingCenter> nearbyAmenities = allAmenities.stream()
                .filter(amenity -> amenity.isWithinRange(latitude, longitude, radiusKm))
                .collect(Collectors.toList());
            
            log.info("Found {} amenities within {}km radius", nearbyAmenities.size(), radiusKm);
            return nearbyAmenities;
            
        } catch (Exception e) {
            log.error("Error fetching nearby amenities: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch nearby amenities: " + e.getMessage(), e);
        }*/
        return null;
    }
}
