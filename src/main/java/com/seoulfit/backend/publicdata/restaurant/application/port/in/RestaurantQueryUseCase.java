package com.seoulfit.backend.publicdata.restaurant.application.port.in;

import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;

import java.util.List;

/**
 * 서울시 관광 음식점 정보 조회 유스케이스
 * 헥사고날 아키텍처의 입력 포트
 */
public interface RestaurantQueryUseCase {
    List<Restaurant> getRestaurantList();
}
