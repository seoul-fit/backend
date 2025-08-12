package com.seoulfit.backend.publicdata.facilities.adapter.in.web;

import com.seoulfit.backend.publicdata.facilities.application.port.in.query.QueryCoolingCenterUseCase;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 서울시 무더위 쉼터 웹 어댑터
 * 헥사고날 아키텍처의 Adapter In
 */
@Tag(name = "서울시 무더위 쉼터 관련 API", description = "CoolingShelterController.class")
@RequestMapping("/api/v1/cooling-shelter")
@RequiredArgsConstructor
@RestController
public class CoolingShelterController {
    private final QueryCoolingCenterUseCase queryCoolingCenterUseCase;

    @Operation(summary = "무더위 쉼터 정보 조회 API.", description = "무더위 쉼터 정보 전체 조회")
    @GetMapping("/all")
    public ResponseEntity<?> getAllCoolingCenter() {
        List<CoolingCenter> allCoolingCenter = queryCoolingCenterUseCase.getAllCoolingCenter();

        return ResponseEntity.ok(allCoolingCenter);
    }

    @Operation(summary = "위도, 경도 기반 2km 내에 무더위 쉼터 정보를 가져온다.", description = "무더위 쉼터 조회(반경2km)")
    @GetMapping("/nearby")
    public ResponseEntity<?> getCoolingCenterNearby(
            @RequestParam String latitude,
            @RequestParam String longitude
    ) {
        return ResponseEntity.ok(queryCoolingCenterUseCase.getCoolingCenterByLatitudeAndLongitude(latitude, longitude));
    }
}
