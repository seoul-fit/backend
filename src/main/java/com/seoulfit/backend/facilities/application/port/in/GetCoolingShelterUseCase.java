package com.seoulfit.backend.facilities.application.port.in;

import com.seoulfit.backend.facilities.domain.CoolingShelter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 무더위 쉼터 조회 유스케이스
 */
public interface GetCoolingShelterUseCase {
    
    /**
     * 편의시설 목록 조회
     */
    List<CoolingShelter> getAmenities(GetAmenitiesQuery query);
    
    /**
     * 특정 위치 주변 편의시설 조회
     */
    List<CoolingShelter> getAmenitiesNearby(BigDecimal latitude, BigDecimal longitude, double radiusKm);
    
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
