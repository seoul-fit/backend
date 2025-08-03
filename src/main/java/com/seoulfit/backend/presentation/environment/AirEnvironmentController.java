package com.seoulfit.backend.presentation.environment;

import com.seoulfit.backend.application.service.AirEnvService;
import com.seoulfit.backend.presentation.environment.dtos.response.SeoulAirQualityApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "서울시 권역별 실시간 대기환경 현황 API", description = "AirEnvironmentController.class")
@RequestMapping("/api/v1/env")
@RequiredArgsConstructor
@RestController
public class AirEnvironmentController {
    private final AirEnvService airEnvService;

    @Operation(summary = "대기환경 API 상태 확인", description = "Seoul API 연결 상태를 확인한다.")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkApiStatus() {
        try {
            // 최소한의 데이터로 API 호출 테스트
            SeoulAirQualityApiResponse response = airEnvService.fetchAirEnvInfo(1, 5, "", "");
            
            boolean isConnected = response != null;
            boolean hasData = isConnected && response.getRealtimeCityAir() != null &&
                             response.getRealtimeCityAir().hasData();
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "API 상태 확인 완료",
                    "apiConnected", isConnected,
                    "hasData", hasData,
                    "serviceName", "RealtimeCityAir"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "API 연결 실패",
                    "error", e.getMessage(),
                    "serviceName", "RealtimeCityAir"
            ));
        }
    }

    @Operation(summary = "대기환경 정보를 가져온다.", description = "준 실시간 대기 환경 정보를 가져온다.")
    @GetMapping("/fetch")
    public ResponseEntity<Map<String, Object>> syncCulturalEvents(
            @RequestParam(defaultValue = "1") int startIndex,
            @RequestParam(defaultValue = "10") int endIndex,
            @RequestParam(defaultValue = "", required = false) String msrgnNmms,
            @RequestParam(defaultValue = "", required = false) String msrsteNm
    ) {
        try {
            SeoulAirQualityApiResponse seoulAirQualityApiResponse = airEnvService.fetchAirEnvInfo(startIndex, endIndex, msrgnNmms, msrsteNm);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "대기 환경 정보 가져오기 성공.",
                    "data", seoulAirQualityApiResponse
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "대기 환경 정보 가져오기 실패.",
                    "error", e.getMessage()
            ));
        }
    }
}
