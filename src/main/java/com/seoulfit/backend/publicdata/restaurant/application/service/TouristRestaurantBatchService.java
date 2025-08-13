package com.seoulfit.backend.publicdata.restaurant.application.service;

import com.seoulfit.backend.publicdata.restaurant.adapter.out.api.dto.TouristRestaurantApiResponse;
import com.seoulfit.backend.publicdata.restaurant.application.port.in.RestaurantBatchUseCase;
import com.seoulfit.backend.publicdata.restaurant.application.port.out.RestaurantCommandPort;
import com.seoulfit.backend.publicdata.restaurant.application.port.out.TouristRestaurantApiClient;
import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;
import com.seoulfit.backend.publicdata.restaurant.infrastructure.mapper.RestaurantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 서울시 관광 음식점 정보 배치 처리 서비스
 * 헥사고날 아키텍처의 애플리케이션 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TouristRestaurantBatchService implements RestaurantBatchUseCase {

    private final TouristRestaurantApiClient apiClient;
    private final RestaurantCommandPort restaurantCommandPort;
    private final RestaurantMapper restaurantMapper;

    @Override
    public void processDailyBatch() {
        log.info("서울시 관광 음식점 정보 일일 배치 처리 시작");
        try {
            TouristRestaurantApiResponse apiResponse1 = apiClient.fetchRestaurantInfo(1, 1000);
            TouristRestaurantApiResponse apiResponse2 = apiClient.fetchRestaurantInfo(1001, 2000);
            TouristRestaurantApiResponse apiResponse3 = apiClient.fetchRestaurantInfo(2001, 3000);
            TouristRestaurantApiResponse apiResponse4 = apiClient.fetchRestaurantInfo(3001, 4000);
            TouristRestaurantApiResponse apiResponse5 = apiClient.fetchRestaurantInfo(4001, 5000);
            TouristRestaurantApiResponse apiResponse6 = apiClient.fetchRestaurantInfo(5001, 6000);
            TouristRestaurantApiResponse apiResponse7 = apiClient.fetchRestaurantInfo(6001, 7000);

            if (!apiResponse1.isSuccess())
                throw new RuntimeException("서울시 음식점 API 호출 실패");


            List<TouristRestaurantApiResponse.RestaurantInfo> restaurantInfoList1 = apiResponse1.getRestaurantInfoList();
            List<TouristRestaurantApiResponse.RestaurantInfo> restaurantInfoList2 = apiResponse2.getRestaurantInfoList();
            List<TouristRestaurantApiResponse.RestaurantInfo> restaurantInfoList3 = apiResponse3.getRestaurantInfoList();
            List<TouristRestaurantApiResponse.RestaurantInfo> restaurantInfoList4 = apiResponse4.getRestaurantInfoList();
            List<TouristRestaurantApiResponse.RestaurantInfo> restaurantInfoList5 = apiResponse5.getRestaurantInfoList();
            List<TouristRestaurantApiResponse.RestaurantInfo> restaurantInfoList6 = apiResponse6.getRestaurantInfoList();
            List<TouristRestaurantApiResponse.RestaurantInfo> restaurantInfoList7 = apiResponse7.getRestaurantInfoList();

            log.info("API에서 {} 개의 음식점 정보 조회 완료", restaurantInfoList1.size());

            List<Restaurant> restaurants1 = restaurantMapper.mapToEntity(restaurantInfoList1);
            List<Restaurant> restaurants2 = restaurantMapper.mapToEntity(restaurantInfoList2);
            List<Restaurant> restaurants3 = restaurantMapper.mapToEntity(restaurantInfoList3);
            List<Restaurant> restaurants4 = restaurantMapper.mapToEntity(restaurantInfoList4);
            List<Restaurant> restaurants5 = restaurantMapper.mapToEntity(restaurantInfoList5);
            List<Restaurant> restaurants6 = restaurantMapper.mapToEntity(restaurantInfoList6);
            List<Restaurant> restaurants7 = restaurantMapper.mapToEntity(restaurantInfoList7);


            restaurantCommandPort.truncate();
            restaurantCommandPort.saveRestaurantList(restaurants1);
            restaurantCommandPort.saveRestaurantList(restaurants2);
            restaurantCommandPort.saveRestaurantList(restaurants3);
            restaurantCommandPort.saveRestaurantList(restaurants4);
            restaurantCommandPort.saveRestaurantList(restaurants5);
            restaurantCommandPort.saveRestaurantList(restaurants6);
            restaurantCommandPort.saveRestaurantList(restaurants7);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }



}
