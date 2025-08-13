package com.seoulfit.backend.publicdata.restaurant.application.port.in;

/**
 * 서울시 관광 음식점 정보 배치 처리 유스케이스
 * 헥사고날 아키텍처의 입력 포트
 */
public interface RestaurantBatchUseCase {

    void processDailyBatch();

}
