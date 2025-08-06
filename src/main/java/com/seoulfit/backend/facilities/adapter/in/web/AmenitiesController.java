package com.seoulfit.backend.facilities.adapter.in.web;

import com.seoulfit.backend.facilities.adapter.in.web.dto.AmenitiesWebResponse;
import com.seoulfit.backend.facilities.application.port.in.GetCoolingShelterUseCase;
import com.seoulfit.backend.facilities.domain.CoolingShelter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 편의시설 웹 어댑터
 * 헥사고날 아키텍처의 Adapter In
 */
@Tag(name = "서울시 편의시설 관련 API", description = "AmenitiesController.class")
@RequestMapping("/api/v1/amenities")
@RequiredArgsConstructor
@RestController
public class AmenitiesController {
    
    private final GetCoolingShelterUseCase coolingShelterUseCase;

    @Operation(summary = "서울시 편의시설 현황 조회 API", description = "서울시 편의시설 현황을 조회한다.")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAmenities(
            @Parameter(description = "시작 인덱스") @RequestParam(defaultValue = "1") int startIndex,
            @Parameter(description = "종료 인덱스") @RequestParam(defaultValue = "100") int endIndex,
            @Parameter(description = "지역구") @RequestParam(required = false) String district,
            @Parameter(description = "시설 유형") @RequestParam(required = false) String facilityType) {
        
        try {
            GetCoolingShelterUseCase.GetAmenitiesQuery query =
                new GetCoolingShelterUseCase.GetAmenitiesQuery(startIndex, endIndex, district, facilityType);
            
            List<CoolingShelter> amenities = coolingShelterUseCase.getAmenities(query);
            
            List<AmenitiesWebResponse> response = amenities.stream()
                .map(AmenitiesWebResponse::from)
                .toList();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "편의시설 조회 성공",
                    "data", response,
                    "count", response.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status", "fail",
                    "message", "편의시설 조회 실패",
                    "error", e.getMessage()
            ));
        }
    }

    @Operation(summary = "주변 편의시설 조회 API", description = "특정 위치 주변의 편의시설을 조회한다.")
    @GetMapping("/nearby")
    public ResponseEntity<Map<String, Object>> getAmenitiesNearby(
            @Parameter(description = "위도") @RequestParam BigDecimal latitude,
            @Parameter(description = "경도") @RequestParam BigDecimal longitude,
            @Parameter(description = "반경(km)") @RequestParam(defaultValue = "1.0") double radiusKm) {
        
        try {
            List<CoolingShelter> amenities = coolingShelterUseCase.getAmenitiesNearby(latitude, longitude, radiusKm);
            
            List<AmenitiesWebResponse> response = amenities.stream()
                .map(AmenitiesWebResponse::from)
                .toList();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "주변 편의시설 조회 성공",
                    "data", response,
                    "count", response.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status", "fail",
                    "message", "주변 편의시설 조회 실패",
                    "error", e.getMessage()
            ));
        }
    }
}
