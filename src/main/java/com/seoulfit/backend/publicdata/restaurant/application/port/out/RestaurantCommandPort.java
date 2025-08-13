package com.seoulfit.backend.publicdata.restaurant.application.port.out;

import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;

import java.util.List;

public interface RestaurantCommandPort {

    void saveRestaurantList(List<Restaurant> restaurants);

    void truncate();
}
