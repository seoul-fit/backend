package com.seoulfit.backend.publicdata.restaurant.adapter.out.persistence;

import com.seoulfit.backend.publicdata.restaurant.adapter.out.persistence.repository.RestaurantRepository;
import com.seoulfit.backend.publicdata.restaurant.application.port.out.RestaurantQueryPort;
import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class RestaurantQueryAdapter implements RestaurantQueryPort {
    private final RestaurantRepository restaurantRepository;

    @Override
    public List<Restaurant> getAllRestaurant() {
        return restaurantRepository.findAll();
    }

    @Override
    public List<Restaurant> getRestaurantLocation(double latitude, double longitude) {
        double radiusKm = 2.0;
        return restaurantRepository.findByLocationWithinRadius(latitude, longitude, radiusKm);
    }
}
