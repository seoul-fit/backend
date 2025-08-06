package com.seoulfit.backend.env.application.port.out;

import com.seoulfit.backend.env.domain.AirQualityEntity;

import java.util.List;
import java.util.Optional;

/**
 * 대기질 데이터 로드 포트
 */
public interface LoadAirQualityPort {
    
    /**
     * 외부 API에서 대기질 데이터 조회
     */
    List<AirQualityEntity> loadAirQuality(int startIndex, int endIndex, String district, String stationName);
    
    /**
     * 특정 지역의 대기질 데이터 조회
     */
    Optional<AirQualityEntity> loadAirQualityByDistrict(String district);
    
    /**
     * 특정 측정소의 대기질 데이터 조회
     */
    Optional<AirQualityEntity> loadAirQualityByStation(String stationName);
    
    /**
     * 외부 활동에 적합한 지역의 대기질 데이터 조회
     */
    List<AirQualityEntity> loadGoodAirQualityAreas();
}
