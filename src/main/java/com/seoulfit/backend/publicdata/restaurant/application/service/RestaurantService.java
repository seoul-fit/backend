package com.seoulfit.backend.publicdata.restaurant.application.service;

import com.seoulfit.backend.publicdata.restaurant.application.port.in.RestaurantQueryUseCase;
import com.seoulfit.backend.publicdata.restaurant.application.port.out.RestaurantQueryPort;
import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService implements RestaurantQueryUseCase {
    private final RestaurantQueryPort restaurantQueryPort;

    @Override
    public List<Restaurant> getRestaurantList() {
        List<Restaurant> allRestaurant = restaurantQueryPort.getAllRestaurant();
        log.info("음식점 전체 개수 : {}", allRestaurant.size());
        return allRestaurant;
    }

}
