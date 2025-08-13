package com.seoulfit.backend.publicdata.restaurant.application.port.out;

import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;

import java.util.List;

public interface RestaurantQueryPort {
    List<Restaurant> getAllRestaurant();

    List<Restaurant> getRestaurantLocation(double latitude, double longitude);
}
