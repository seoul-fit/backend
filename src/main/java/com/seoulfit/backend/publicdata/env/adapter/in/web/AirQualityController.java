package com.seoulfit.backend.publicdata.env.adapter.in.web;

import com.seoulfit.backend.publicdata.env.adapter.in.web.dto.AirQualityResponse;
import com.seoulfit.backend.publicdata.env.application.port.in.AirQualityQueryUseCase;
import com.seoulfit.backend.publicdata.env.domain.AirQuality;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 대기질 정보 조회 컨트롤러
 * 헥사고날 아키텍처의 Adapter In
 */
@Slf4j
// @RestController Todo: 필요할 때 api open
@RequestMapping("/api/env/air-quality")
@RequiredArgsConstructor
@Tag(name = "대기질 정보", description = "서울시 대기질 정보 조회 API")
public class AirQualityController {

    private final AirQualityQueryUseCase airQualityQueryUseCase;

    @Operation(summary = "최신 대기질 정보 조회", description = "서울시 전체 측정소의 최신 대기질 정보를 조회합니다.")
    @GetMapping("/latest")
    public ResponseEntity<List<AirQualityResponse>> getLatestAirQuality() {
        log.info("최신 대기질 정보 조회 요청");
        
        List<AirQuality> airQualities = airQualityQueryUseCase.getLatestAirQuality();
        List<AirQualityResponse> responses = airQualities.stream()
            .map(AirQualityResponse::from)
            .toList();
        
        log.info("최신 대기질 정보 조회 완료 - 조회된 측정소 수: {}", responses.size());
        return ResponseEntity.ok(responses);
    }

}
