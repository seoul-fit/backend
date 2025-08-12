package com.seoulfit.backend.publicdata.facilities.adapter.in.web;

import com.seoulfit.backend.publicdata.facilities.adapter.in.web.dto.CoolingCenterResponse;
import com.seoulfit.backend.publicdata.facilities.application.port.in.query.QueryCoolingCenterUseCase;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = "서울시 무더위쉼터", description = "서울시 무더위쉼터 정보 조회 API")
@RequestMapping("/api/v1/cooling-shelters")
@RequiredArgsConstructor
@RestController
@Slf4j
public class CoolingShelterController {
    private final QueryCoolingCenterUseCase queryCoolingCenterUseCase;

    @Operation(
        summary = "무더위쉼터 전체 조회", 
        description = "서울시 모든 무더위쉼터 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CoolingCenterResponse.class)
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/all")
    public ResponseEntity<List<CoolingCenterResponse>> getAllCoolingCenters() {
        log.info("무더위쉼터 전체 조회 요청");
        
        try {
            List<CoolingCenter> centers = queryCoolingCenterUseCase.getAllCoolingCenter();
            List<CoolingCenterResponse> response = CoolingCenterResponse.from(centers);
            
            log.info("무더위쉼터 {} 개 조회 완료", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("무더위쉼터 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "근처 무더위쉼터 조회", 
        description = "지정된 위도, 경도 기준 반경 2km 내의 무더위쉼터 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CoolingCenterResponse.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/nearby")
    public ResponseEntity<List<CoolingCenterResponse>> getCoolingCentersNearby(
            @Parameter(description = "위도", example = "37.5665", required = true)
            @RequestParam String latitude,
            @Parameter(description = "경도", example = "126.9780", required = true)
            @RequestParam String longitude
    ) {
        log.info("근처 무더위쉼터 조회 요청 - 위도: {}, 경도: {}", latitude, longitude);
        
        try {
            List<CoolingCenter> centers = queryCoolingCenterUseCase.getCoolingCenterByLatitudeAndLongitude(latitude, longitude);
            List<CoolingCenterResponse> response = CoolingCenterResponse.from(centers);
            
            log.info("근처 무더위쉼터 {} 개 조회 완료", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("근처 무더위쉼터 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
