package com.seoulfit.backend.publicdata.restaurant.application.port.out;

import com.seoulfit.backend.publicdata.restaurant.adapter.out.api.dto.TouristRestaurantApiResponse;

/**
 * 서울시 관광 음식점 정보 API 클라이언트 포트
 * 외부 API 호출을 위한 출력 포트
 */
public interface TouristRestaurantApiClient {

    /**
     * 서울시 관광 음식점 정보 API 호출
     * @param startIndex 시작 인덱스
     * @param endIndex 종료 인덱스
     * @return API 응답 데이터
     */
    TouristRestaurantApiResponse fetchRestaurantInfo(int startIndex, int endIndex);

    /**
     * 전체 관광 음식점 정보 조회
     * @return API 응답 데이터
     */
    TouristRestaurantApiResponse fetchAllRestaurantInfo();
}
