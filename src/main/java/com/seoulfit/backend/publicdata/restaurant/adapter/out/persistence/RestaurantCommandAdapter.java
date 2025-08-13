package com.seoulfit.backend.publicdata.restaurant.adapter.out.persistence;

import com.seoulfit.backend.publicdata.restaurant.adapter.out.persistence.repository.RestaurantRepository;
import com.seoulfit.backend.publicdata.restaurant.application.port.out.RestaurantCommandPort;
import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class RestaurantCommandAdapter implements RestaurantCommandPort {
    private final RestaurantRepository restaurantRepository;
    private final EntityManager entityManager;

    @Override
    public void saveRestaurantList(List<Restaurant> restaurants) {
        restaurantRepository.saveAll(restaurants);
    }

    @Override
    public void truncate() {
        entityManager.createNativeQuery("TRUNCATE TABLE restaurants").executeUpdate();
    }
}
