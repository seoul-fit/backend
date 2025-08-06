package com.seoulfit.backend.amenities.adapter.in.web;

import com.seoulfit.backend.amenities.application.service.AmenitiesService;
import com.seoulfit.backend.amenities.adapter.in.web.dto.AmenitiesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "서울시 편의시설 관련 API", description = "AmenitiesController.class")
@RequestMapping("/api/v1/amenities")
@RequiredArgsConstructor
@RestController
public class AmenitiesController {
    private final AmenitiesService amenitiesService;

    @Operation(summary = "서울시 무더위 심터 현황 확인 API", description = "서울시 무더위 심터 현황을 확인한다.")
    @GetMapping
    public ResponseEntity<Map<String, Object>> fetchAmenitiesInfo(int startIndex, int endIndex) {
        try {
            AmenitiesResponse amenitiesResponse = amenitiesService.fetchAmenitiesInfo(startIndex, endIndex);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "성공",
                    "data", amenitiesResponse
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status" , "fail",
                    "message", "API 연결 실패",
                    "error", e.getMessage()
            ));
        }
    }


}
