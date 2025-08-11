package com.seoulfit.backend.publicdata.facilities.application.port.in;

import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 무더위 쉼터 조회 유스케이스
 */
public interface CommandCoolingShelterUseCase {
    
    /**
     * 무더위 쉼터 리스트 저장
     */
    List<CoolingCenter> saveCoolingShelter(GetAmenitiesQuery query);
    
    /**
     * 특정 위치 주변 무더위 쉼터 조회
     */
    List<CoolingCenter> getAmenitiesNearby(BigDecimal latitude, BigDecimal longitude, double radiusKm);
    
    /**
     * 편의시설 조회 쿼리
     */
    record GetAmenitiesQuery(
        int startIndex,
        int endIndex,
        String district,
        String facilityType
    ) {}
}
