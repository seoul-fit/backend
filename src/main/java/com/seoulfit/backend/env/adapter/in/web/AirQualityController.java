package com.seoulfit.backend.env.adapter.in.web;

import com.seoulfit.backend.env.adapter.in.web.dto.AirQualityResponse;
import com.seoulfit.backend.env.application.port.in.AirQualityQueryUseCase;
import com.seoulfit.backend.env.application.service.AirQualityQueryService;
import com.seoulfit.backend.env.domain.AirQuality;
import com.seoulfit.backend.env.domain.AirQualityStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 대기질 정보 조회 컨트롤러
 * 헥사고날 아키텍처의 Adapter In
 */
@Slf4j
@RestController
@RequestMapping("/api/env/air-quality")
@RequiredArgsConstructor
@Tag(name = "대기질 정보", description = "서울시 대기질 정보 조회 API")
public class AirQualityController {

    private final AirQualityQueryUseCase airQualityQueryUseCase;
    private final AirQualityQueryService airQualityQueryService;

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

    @Operation(summary = "측정소별 최신 대기질 정보 조회", description = "특정 측정소의 최신 대기질 정보를 조회합니다.")
    @GetMapping("/latest/station/{stationName}")
    public ResponseEntity<AirQualityResponse> getLatestAirQualityByStation(
            @Parameter(description = "측정소명", example = "종로구")
            @PathVariable String stationName) {
        log.info("측정소별 최신 대기질 정보 조회 요청 - 측정소: {}", stationName);
        
        Optional<AirQuality> airQuality = airQualityQueryUseCase.getLatestAirQualityByStation(stationName);
        
        if (airQuality.isPresent()) {
            AirQualityResponse response = AirQualityResponse.from(airQuality.get());
            log.info("측정소별 최신 대기질 정보 조회 완료 - 측정소: {}", stationName);
            return ResponseEntity.ok(response);
        } else {
            log.warn("측정소별 최신 대기질 정보 없음 - 측정소: {}", stationName);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "지역별 최신 대기질 정보 조회", description = "특정 지역의 최신 대기질 정보를 조회합니다.")
    @GetMapping("/latest/region/{regionName}")
    public ResponseEntity<List<AirQualityResponse>> getLatestAirQualityByRegion(
            @Parameter(description = "지역명", example = "강남구")
            @PathVariable String regionName) {
        log.info("지역별 최신 대기질 정보 조회 요청 - 지역: {}", regionName);
        
        List<AirQuality> airQualities = airQualityQueryUseCase.getLatestAirQualityByRegion(regionName);
        List<AirQualityResponse> responses = airQualities.stream()
            .map(AirQualityResponse::from)
            .toList();
        
        log.info("지역별 최신 대기질 정보 조회 완료 - 지역: {}, 조회된 측정소 수: {}", regionName, responses.size());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "기간별 대기질 정보 조회", description = "특정 기간의 대기질 정보를 조회합니다.")
    @GetMapping("/period")
    public ResponseEntity<List<AirQualityResponse>> getAirQualityByPeriod(
            @Parameter(description = "시작 시간", example = "2024-08-10T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "종료 시간", example = "2024-08-10T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("기간별 대기질 정보 조회 요청 - 시작: {}, 종료: {}", startTime, endTime);
        
        List<AirQuality> airQualities = airQualityQueryUseCase.getAirQualityByPeriod(startTime, endTime);
        List<AirQualityResponse> responses = airQualities.stream()
            .map(AirQualityResponse::from)
            .toList();
        
        log.info("기간별 대기질 정보 조회 완료 - 조회된 데이터 수: {}", responses.size());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "상태별 대기질 정보 조회", description = "특정 상태 이상의 대기질 정보를 조회합니다.")
    @GetMapping("/status/{minStatus}")
    public ResponseEntity<List<AirQualityResponse>> getAirQualityByStatus(
            @Parameter(description = "최소 대기질 상태", example = "UNHEALTHY_FOR_SENSITIVE")
            @PathVariable AirQualityStatus minStatus) {
        log.info("상태별 대기질 정보 조회 요청 - 최소 상태: {}", minStatus);
        
        List<AirQuality> airQualities = airQualityQueryUseCase.getAirQualityByStatus(minStatus);
        List<AirQualityResponse> responses = airQualities.stream()
            .map(AirQualityResponse::from)
            .toList();
        
        log.info("상태별 대기질 정보 조회 완료 - 조회된 데이터 수: {}", responses.size());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "대기질 통계 정보 조회", description = "특정 기간의 대기질 통계 정보를 조회합니다.")
    @GetMapping("/statistics")
    public ResponseEntity<AirQualityQueryUseCase.AirQualityStatistics> getAirQualityStatistics(
            @Parameter(description = "시작 시간", example = "2024-08-10T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "종료 시간", example = "2024-08-10T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("대기질 통계 정보 조회 요청 - 시작: {}, 종료: {}", startTime, endTime);
        
        AirQualityQueryUseCase.AirQualityStatistics statistics = 
            airQualityQueryUseCase.getAirQualityStatistics(startTime, endTime);
        
        log.info("대기질 통계 정보 조회 완료");
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "서울시 대기질 현황 요약", description = "서울시 전체 대기질 현황을 요약하여 제공합니다.")
    @GetMapping("/summary")
    public ResponseEntity<AirQualityQueryService.AirQualitySummary> getSeoulAirQualitySummary() {
        log.info("서울시 대기질 현황 요약 조회 요청");
        
        AirQualityQueryService.AirQualitySummary summary = 
            airQualityQueryService.getSeoulAirQualitySummary();
        
        log.info("서울시 대기질 현황 요약 조회 완료");
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "측정소별 대기질 변화 추이", description = "특정 측정소의 최근 시간별 대기질 변화를 조회합니다.")
    @GetMapping("/trend/station/{stationName}")
    public ResponseEntity<List<AirQualityResponse>> getAirQualityTrend(
            @Parameter(description = "측정소명", example = "종로구")
            @PathVariable String stationName,
            @Parameter(description = "조회할 시간(시간 단위)", example = "24")
            @RequestParam(defaultValue = "24") int hours) {
        log.info("측정소별 대기질 변화 추이 조회 요청 - 측정소: {}, 시간: {}시간", stationName, hours);
        
        List<AirQuality> airQualities = airQualityQueryService.getAirQualityTrend(stationName, hours);
        List<AirQualityResponse> responses = airQualities.stream()
            .map(AirQualityResponse::from)
            .toList();
        
        log.info("측정소별 대기질 변화 추이 조회 완료 - 조회된 데이터 수: {}", responses.size());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "알림 필요 대기질 정보 조회", description = "알림이 필요한 수준의 대기질 정보를 조회합니다.")
    @GetMapping("/alert")
    public ResponseEntity<List<AirQualityResponse>> getAirQualityRequiringNotification() {
        log.info("알림 필요 대기질 정보 조회 요청");
        
        List<AirQuality> airQualities = airQualityQueryService.getAirQualityRequiringNotification();
        List<AirQualityResponse> responses = airQualities.stream()
            .map(AirQualityResponse::from)
            .toList();
        
        log.info("알림 필요 대기질 정보 조회 완료 - 조회된 데이터 수: {}", responses.size());
        return ResponseEntity.ok(responses);
    }
}
