package com.seoulfit.backend.env.application.port.in;

import com.seoulfit.backend.env.domain.AirQualityEntity;

import java.util.List;

/**
 * 대기질 조회 유스케이스
 */
public interface GetAirQualityUseCase {
    
    /**
     * 대기질 정보 조회
     */
    List<AirQualityEntity> getAirQuality(GetAirQualityQuery query);
    
    /**
     * 특정 지역의 대기질 정보 조회
     */
    AirQualityEntity getAirQualityByDistrict(String district);
    
    /**
     * 특정 측정소의 대기질 정보 조회
     */
    AirQualityEntity getAirQualityByStation(String stationName);
    
    /**
     * 외부 활동에 적합한 지역 조회
     */
    List<AirQualityEntity> getGoodAirQualityAreas();
    
    /**
     * 대기질 조회 쿼리
     */
    record GetAirQualityQuery(
        int startIndex,
        int endIndex,
        String district,
        String stationName
    ) {}
}
