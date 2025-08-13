package com.seoulfit.backend.publicdata.restaurant.adapter.in.web;

import com.seoulfit.backend.publicdata.restaurant.application.port.in.RestaurantQueryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 서울시 관광 음식점 정보 REST Controller
 * 헥사고날 아키텍처의 입력 어댑터
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "서울시 관광 음식점 조회 API", description = "RestaurantController.class")
public class RestaurantController {
    private final RestaurantQueryUseCase restaurantQueryUseCase;

    @Operation()
    @GetMapping("/all")
    public ResponseEntity<?> getAllRestaurant() {
        return ResponseEntity.ok(restaurantQueryUseCase.getRestaurantList());
    }


}
