package com.seoulfit.backend.env.application.service;

import com.seoulfit.backend.env.application.port.in.GetAirQualityUseCase;
import com.seoulfit.backend.env.application.port.out.LoadAirQualityPort;
import com.seoulfit.backend.env.domain.AirQualityEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 대기질 애플리케이션 서비스
 * 헥사고날 아키텍처의 Application Layer
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AirQualityService implements GetAirQualityUseCase {
    
    private final LoadAirQualityPort loadAirQualityPort;

    @Override
    public List<AirQualityEntity> getAirQuality(GetAirQualityQuery query) {
        log.info("Fetching air quality with query: {}", query);
        
        try {
            List<AirQualityEntity> airQualityList = loadAirQualityPort.loadAirQuality(
                query.startIndex(),
                query.endIndex(),
                query.district(),
                query.stationName()
            );
            
            log.info("Successfully retrieved {} air quality records", airQualityList.size());
            return airQualityList;
            
        } catch (Exception e) {
            log.error("Error fetching air quality: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch air quality data: " + e.getMessage(), e);
        }
    }

    @Override
    public AirQualityEntity getAirQualityByDistrict(String district) {
        log.info("Fetching air quality for district: {}", district);
        
        try {
            return loadAirQualityPort.loadAirQualityByDistrict(district)
                .orElseThrow(() -> new RuntimeException("Air quality data not found for district: " + district));
            
        } catch (Exception e) {
            log.error("Error fetching air quality for district {}: {}", district, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch air quality for district: " + e.getMessage(), e);
        }
    }

    @Override
    public AirQualityEntity getAirQualityByStation(String stationName) {
        log.info("Fetching air quality for station: {}", stationName);
        
        try {
            return loadAirQualityPort.loadAirQualityByStation(stationName)
                .orElseThrow(() -> new RuntimeException("Air quality data not found for station: " + stationName));
            
        } catch (Exception e) {
            log.error("Error fetching air quality for station {}: {}", stationName, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch air quality for station: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AirQualityEntity> getGoodAirQualityAreas() {
        log.info("Fetching areas with good air quality");
        
        try {
            List<AirQualityEntity> goodAreas = loadAirQualityPort.loadGoodAirQualityAreas();
            
            log.info("Found {} areas with good air quality", goodAreas.size());
            return goodAreas;
            
        } catch (Exception e) {
            log.error("Error fetching good air quality areas: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch good air quality areas: " + e.getMessage(), e);
        }
    }
}
