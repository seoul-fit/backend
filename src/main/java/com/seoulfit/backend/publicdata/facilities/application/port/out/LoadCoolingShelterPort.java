package com.seoulfit.backend.publicdata.facilities.application.port.out;

import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;

import java.util.List;

/**
 * 무더위 쉼터 데이터 로드 포트
 */
public interface LoadCoolingShelterPort {

    /**
     * 외부 API 에서 무더위 쉼터  데이터 조회
     */
    List<CoolingCenter> loadAmenities(int startIndex, int endIndex);

    /**
     * 특정 지역의 무더위 쉼터 데이터 조회
     */
    default List<CoolingCenter> loadAmenitiesByDistrict(String district) {
        return List.of();
    }


    /**
     * 무더위 쉼터 타입별 데이터 조회
     */
    default List<CoolingCenter> loadAmenitiesByType(String facilityType) {
        return List.of();
    }
}
