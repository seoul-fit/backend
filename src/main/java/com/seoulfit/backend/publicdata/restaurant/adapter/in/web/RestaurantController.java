package com.seoulfit.backend.publicdata.restaurant.adapter.in.web;

import com.seoulfit.backend.publicdata.restaurant.application.port.in.RestaurantQueryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Operation(summary = "서울시 관광 음식점 전체 조회", description = "서울시 모든 관광 음식점 정보를 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<?> getAllRestaurant() {
        return ResponseEntity.ok(restaurantQueryUseCase.getRestaurantList());
    }

    @Operation(summary = "근처 관광 음식점 조회", description = "지정된 위도, 경도 기준 반경 2km 내의 관광 음식점 정보를 조회합니다.")
    @GetMapping("/nearby")
    public ResponseEntity<?> getNearByRestaurant(
            @Parameter(description = "위도", example = "37.5665", required = true)
            @RequestParam String latitude,
            @Parameter(description = "경도", example = "126.9780", required = true)
            @RequestParam String longitude
    ) {

        return ResponseEntity.ok(restaurantQueryUseCase.getRestaurantByLatitudeAndLongitude(latitude, longitude));
    }


}
