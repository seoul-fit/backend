package com.seoulfit.backend.facilities.application.port.out;

import com.seoulfit.backend.facilities.domain.CoolingShelter;

import java.util.List;

/**
 * 무더위 쉼터 데이터 로드 포트
 */
public interface LoadCoolingShelterPort {
    
    /**
     * 외부 API 에서 무더위 쉼터  데이터 조회
     */
    List<CoolingShelter> loadAmenities(int startIndex, int endIndex);
    
    /**
     * 특정 지역의 무더위 쉼터 데이터 조회
     */
    default List<CoolingShelter> loadAmenitiesByDistrict(String district) {
        return List.of();
    }

    
    /**
     * 무더위 쉼터 타입별 데이터 조회
     */
    default List<CoolingShelter> loadAmenitiesByType(String facilityType) {
        return List.of();
    }
}
