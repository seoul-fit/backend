package com.seoulfit.backend.env.adapter.in.web;

import com.seoulfit.backend.env.application.port.in.GetAirQualityUseCase;
import com.seoulfit.backend.env.adapter.in.web.dto.AirQualityWebResponse;
import com.seoulfit.backend.env.domain.AirQualityEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 대기환경 웹 어댑터
 * 헥사고날 아키텍처의 Adapter In
 */
@Tag(name = "서울시 대기환경 관련 API", description = "AirEnvironmentController.class")
@RequestMapping("/api/v1/environment")
@RequiredArgsConstructor
@RestController
public class AirEnvironmentController {
    
    private final GetAirQualityUseCase getAirQualityUseCase;

    @Operation(summary = "서울시 대기질 정보 조회 API", description = "서울시 실시간 대기질 정보를 조회한다.")
    @GetMapping("/air-quality")
    public ResponseEntity<Map<String, Object>> getAirQuality(
            @Parameter(description = "시작 인덱스") @RequestParam(defaultValue = "1") int startIndex,
            @Parameter(description = "종료 인덱스") @RequestParam(defaultValue = "100") int endIndex,
            @Parameter(description = "측정소 지역명") @RequestParam(required = false) String district,
            @Parameter(description = "측정소명") @RequestParam(required = false) String stationName) {
        
        try {
            GetAirQualityUseCase.GetAirQualityQuery query = 
                new GetAirQualityUseCase.GetAirQualityQuery(startIndex, endIndex, district, stationName);
            
            List<AirQualityEntity> airQualityList = getAirQualityUseCase.getAirQuality(query);
            
            List<AirQualityWebResponse> response = airQualityList.stream()
                .map(AirQualityWebResponse::from)
                .toList();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "대기질 정보 조회 성공",
                    "data", response,
                    "count", response.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status", "fail",
                    "message", "대기질 정보 조회 실패",
                    "error", e.getMessage()
            ));
        }
    }

    @Operation(summary = "지역별 대기질 정보 조회 API", description = "특정 지역의 대기질 정보를 조회한다.")
    @GetMapping("/air-quality/district/{district}")
    public ResponseEntity<Map<String, Object>> getAirQualityByDistrict(
            @Parameter(description = "지역명") @PathVariable String district) {
        
        try {
            AirQualityEntity airQuality = getAirQualityUseCase.getAirQualityByDistrict(district);
            AirQualityWebResponse response = AirQualityWebResponse.from(airQuality);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "지역별 대기질 정보 조회 성공",
                    "data", response
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status", "fail",
                    "message", "지역별 대기질 정보 조회 실패",
                    "error", e.getMessage()
            ));
        }
    }

    @Operation(summary = "측정소별 대기질 정보 조회 API", description = "특정 측정소의 대기질 정보를 조회한다.")
    @GetMapping("/air-quality/station/{stationName}")
    public ResponseEntity<Map<String, Object>> getAirQualityByStation(
            @Parameter(description = "측정소명") @PathVariable String stationName) {
        
        try {
            AirQualityEntity airQuality = getAirQualityUseCase.getAirQualityByStation(stationName);
            AirQualityWebResponse response = AirQualityWebResponse.from(airQuality);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "측정소별 대기질 정보 조회 성공",
                    "data", response
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status", "fail",
                    "message", "측정소별 대기질 정보 조회 실패",
                    "error", e.getMessage()
            ));
        }
    }

    @Operation(summary = "외부활동 적합 지역 조회 API", description = "대기질이 좋아 외부활동에 적합한 지역을 조회한다.")
    @GetMapping("/air-quality/good-areas")
    public ResponseEntity<Map<String, Object>> getGoodAirQualityAreas() {
        
        try {
            List<AirQualityEntity> goodAreas = getAirQualityUseCase.getGoodAirQualityAreas();
            
            List<AirQualityWebResponse> response = goodAreas.stream()
                .map(AirQualityWebResponse::from)
                .toList();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "외부활동 적합 지역 조회 성공",
                    "data", response,
                    "count", response.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status", "fail",
                    "message", "외부활동 적합 지역 조회 실패",
                    "error", e.getMessage()
            ));
        }
    }
}
