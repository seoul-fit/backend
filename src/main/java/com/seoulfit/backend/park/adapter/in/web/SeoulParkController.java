package com.seoulfit.backend.park.adapter.in.web;

import com.seoulfit.backend.park.application.port.in.SeoulParkBatchUseCase;
import com.seoulfit.backend.park.application.port.in.SeoulParkQueryUseCase;
import com.seoulfit.backend.park.domain.SeoulPark;
import com.seoulfit.backend.park.infrastructure.batch.SeoulParkDailyBatch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 서울시 공원 정보 REST Controller
 * 헥사고날 아키텍처의 입력 어댑터
 */
@Slf4j
@RestController
@RequestMapping("/api/parks")
@RequiredArgsConstructor
@Tag(name = "Seoul Parks", description = "서울시 공원 정보 API")
public class SeoulParkController {

    private final SeoulParkQueryUseCase queryUseCase;
    private final SeoulParkDailyBatch dailyBatch;

    @Operation(summary = "최신 공원 정보 조회", description = "가장 최근에 수집된 서울시 공원 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<SeoulPark>> getLatestParks() {
        log.info("최신 공원 정보 조회 요청");
        List<SeoulPark> parks = queryUseCase.findLatestParks();
        return ResponseEntity.ok(parks);
    }

    @Operation(summary = "특정 날짜 공원 정보 조회", description = "지정된 날짜의 서울시 공원 정보를 조회합니다.")
    @GetMapping("/date/{dataDate}")
    public ResponseEntity<List<SeoulPark>> getParksByDate(
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식)", example = "20250807")
            @PathVariable String dataDate) {
        log.info("특정 날짜 공원 정보 조회 요청 - 날짜: {}", dataDate);
        List<SeoulPark> parks = queryUseCase.findParksByDate(dataDate);
        return ResponseEntity.ok(parks);
    }

    @Operation(summary = "지역별 공원 정보 조회", description = "특정 지역의 공원 정보를 조회합니다.")
    @GetMapping("/zone/{zone}")
    public ResponseEntity<List<SeoulPark>> getParksByZone(
            @Parameter(description = "지역명", example = "강남구")
            @PathVariable String zone,
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식, 생략시 최신 데이터)", example = "20250807")
            @RequestParam(required = false) String dataDate) {
        log.info("지역별 공원 정보 조회 요청 - 지역: {}, 날짜: {}", zone, dataDate);
        List<SeoulPark> parks = queryUseCase.findParksByZone(zone, dataDate);
        return ResponseEntity.ok(parks);
    }

    @Operation(summary = "공원명 검색", description = "공원명으로 공원 정보를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<SeoulPark>> searchParksByName(
            @Parameter(description = "검색할 공원명", example = "한강공원")
            @RequestParam String name,
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식, 생략시 최신 데이터)", example = "20250807")
            @RequestParam(required = false) String dataDate) {
        log.info("공원명 검색 요청 - 검색어: {}, 날짜: {}", name, dataDate);
        List<SeoulPark> parks = queryUseCase.searchParksByName(name, dataDate);
        return ResponseEntity.ok(parks);
    }

    @Operation(summary = "공원 상세 정보 조회", description = "공원 번호로 특정 공원의 상세 정보를 조회합니다.")
    @GetMapping("/{parkIdx}")
    public ResponseEntity<SeoulPark> getParkByIdx(
            @Parameter(description = "공원 번호", example = "1")
            @PathVariable Long parkIdx,
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식, 생략시 최신 데이터)", example = "20250807")
            @RequestParam(required = false) String dataDate) {
        log.info("공원 상세 정보 조회 요청 - 공원번호: {}, 날짜: {}", parkIdx, dataDate);
        Optional<SeoulPark> park = queryUseCase.findParkByIdx(parkIdx, dataDate);
        
        return park.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "좌표 정보가 있는 공원 조회", description = "GPS 좌표 정보가 있는 공원 목록을 조회합니다.")
    @GetMapping("/with-coordinates")
    public ResponseEntity<List<SeoulPark>> getParksWithCoordinates(
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식, 생략시 최신 데이터)", example = "20250807")
            @RequestParam(required = false) String dataDate) {
        log.info("좌표 정보가 있는 공원 조회 요청 - 날짜: {}", dataDate);
        List<SeoulPark> parks = queryUseCase.findParksWithCoordinates(dataDate);
        return ResponseEntity.ok(parks);
    }

    @Operation(summary = "사용 가능한 데이터 날짜 목록", description = "조회 가능한 데이터 날짜 목록을 반환합니다.")
    @GetMapping("/available-dates")
    public ResponseEntity<List<String>> getAvailableDataDates() {
        log.info("사용 가능한 데이터 날짜 목록 조회 요청");
        List<String> dates = queryUseCase.getAvailableDataDates();
        return ResponseEntity.ok(dates);
    }

    @Operation(summary = "공원 데이터 통계", description = "특정 날짜의 공원 데이터 통계를 조회합니다.")
    @GetMapping("/statistics")
    public ResponseEntity<SeoulParkQueryUseCase.ParkDataStatistics> getParkStatistics(
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식, 생략시 최신 데이터)", example = "20250807")
            @RequestParam(required = false) String dataDate) {
        log.info("공원 데이터 통계 조회 요청 - 날짜: {}", dataDate);
        SeoulParkQueryUseCase.ParkDataStatistics statistics = queryUseCase.getParkStatistics(dataDate);
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "수동 배치 실행", description = "공원 정보 수집 배치를 수동으로 실행합니다. (관리자용)")
    @PostMapping("/batch/manual")
    public ResponseEntity<SeoulParkBatchUseCase.SeoulParkBatchResult> executeManualBatch(
            @Parameter(description = "배치 실행 날짜 (YYYYMMDD 형식, 생략시 오늘 날짜)", example = "20250807")
            @RequestParam(required = false) String dataDate) {
        log.info("수동 배치 실행 요청 - 날짜: {}", dataDate);
        
        SeoulParkBatchUseCase.SeoulParkBatchResult result;
        if (dataDate != null) {
            result = dailyBatch.executeManualBatch(dataDate);
        } else {
            result = dailyBatch.executeManualBatch();
        }
        
        return ResponseEntity.ok(result);
    }
}
