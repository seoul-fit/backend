package com.seoulfit.backend.publicdata.restaurant.adapter.in.web;

import com.seoulfit.backend.publicdata.restaurant.adapter.in.web.dto.TouristRestaurantResponse;
import com.seoulfit.backend.publicdata.restaurant.application.port.in.TouristRestaurantBatchUseCase;
import com.seoulfit.backend.publicdata.restaurant.application.port.in.TouristRestaurantQueryUseCase;
import com.seoulfit.backend.publicdata.restaurant.domain.TouristRestaurant;
import com.seoulfit.backend.publicdata.restaurant.infrastructure.batch.TouristRestaurantDailyBatch;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 서울시 관광 음식점 정보 REST Controller
 * 헥사고날 아키텍처의 입력 어댑터
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tourist-restaurants")
@RequiredArgsConstructor
@Tag(name = "서울시 관광 음식점", description = "서울시 관광 음식점 정보 조회 API")
public class TouristRestaurantController {

    private final TouristRestaurantQueryUseCase queryUseCase;
    private final TouristRestaurantDailyBatch dailyBatch;

    @Operation(
        summary = "최신 음식점 정보 조회", 
        description = "가장 최근에 수집된 서울시 관광 음식점 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TouristRestaurantResponse.class)
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/latest")
    public ResponseEntity<List<TouristRestaurantResponse>> getLatestRestaurants() {
        log.info("최신 음식점 정보 조회 요청");
        
        try {
            List<TouristRestaurant> restaurants = queryUseCase.findLatestRestaurants();
            List<TouristRestaurantResponse> response = TouristRestaurantResponse.from(restaurants);
            
            log.info("최신 음식점 {} 개 조회 완료", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("최신 음식점 정보 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "특정 날짜 음식점 정보 조회", 
        description = "지정된 날짜의 서울시 관광 음식점 정보를 조회합니다."
    )
    @GetMapping("/date/{dataDate}")
    public ResponseEntity<List<TouristRestaurantResponse>> getRestaurantsByDate(
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식)", example = "20250807")
            @PathVariable String dataDate
    ) {
        log.info("특정 날짜 음식점 정보 조회 요청 - 날짜: {}", dataDate);
        
        try {
            List<TouristRestaurant> restaurants = queryUseCase.findRestaurantsByDate(dataDate);
            List<TouristRestaurantResponse> response = TouristRestaurantResponse.from(restaurants);
            
            log.info("특정 날짜 음식점 {} 개 조회 완료", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("특정 날짜 음식점 정보 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "언어별 음식점 정보 조회", 
        description = "특정 언어의 음식점 정보를 조회합니다."
    )
    @GetMapping("/language/{langCodeId}")
    public ResponseEntity<List<TouristRestaurantResponse>> getRestaurantsByLanguage(
            @Parameter(description = "언어 코드", example = "ko")
            @PathVariable String langCodeId,
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식, 생략시 최신 데이터)", example = "20250807")
            @RequestParam(required = false) String dataDate
    ) {
        log.info("언어별 음식점 정보 조회 요청 - 언어: {}, 날짜: {}", langCodeId, dataDate);
        
        try {
            List<TouristRestaurant> restaurants = queryUseCase.findRestaurantsByLanguage(langCodeId, dataDate);
            List<TouristRestaurantResponse> response = TouristRestaurantResponse.from(restaurants);
            
            log.info("언어별 음식점 {} 개 조회 완료", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("언어별 음식점 정보 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "음식점명 검색", 
        description = "음식점명으로 음식점 정보를 검색합니다."
    )
    @GetMapping("/search/name")
    public ResponseEntity<List<TouristRestaurantResponse>> searchRestaurantsByName(
            @Parameter(description = "검색할 음식점명", example = "한식당")
            @RequestParam String name,
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식, 생략시 최신 데이터)", example = "20250807")
            @RequestParam(required = false) String dataDate
    ) {
        log.info("음식점명 검색 요청 - 검색어: {}, 날짜: {}", name, dataDate);
        
        try {
            List<TouristRestaurant> restaurants = queryUseCase.searchRestaurantsByName(name, dataDate);
            List<TouristRestaurantResponse> response = TouristRestaurantResponse.from(restaurants);
            
            log.info("음식점명 검색 {} 개 조회 완료", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("음식점명 검색 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "주소 검색", 
        description = "주소로 음식점 정보를 검색합니다."
    )
    @GetMapping("/search/address")
    public ResponseEntity<List<TouristRestaurantResponse>> searchRestaurantsByAddress(
            @Parameter(description = "검색할 주소", example = "강남구")
            @RequestParam String address,
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식, 생략시 최신 데이터)", example = "20250807")
            @RequestParam(required = false) String dataDate
    ) {
        log.info("주소 검색 요청 - 검색어: {}, 날짜: {}", address, dataDate);
        
        try {
            List<TouristRestaurant> restaurants = queryUseCase.searchRestaurantsByAddress(address, dataDate);
            List<TouristRestaurantResponse> response = TouristRestaurantResponse.from(restaurants);
            
            log.info("주소 검색 {} 개 조회 완료", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("주소 검색 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "음식점 상세 정보 조회", 
        description = "음식점 ID로 특정 음식점의 상세 정보를 조회합니다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<TouristRestaurantResponse> getRestaurantById(
            @Parameter(description = "음식점 ID", example = "1")
            @PathVariable Long id
    ) {
        log.info("음식점 상세 정보 조회 요청 - ID: {}", id);
        
        try {
            Optional<TouristRestaurant> restaurant = queryUseCase.findRestaurantById(id);
            
            if (restaurant.isPresent()) {
                TouristRestaurantResponse response = TouristRestaurantResponse.from(restaurant.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("음식점 상세 정보 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "사용 가능한 데이터 날짜 목록", 
        description = "조회 가능한 데이터 날짜 목록을 반환합니다."
    )
    @GetMapping("/available-dates")
    public ResponseEntity<List<String>> getAvailableDataDates() {
        log.info("사용 가능한 데이터 날짜 목록 조회 요청");
        
        try {
            List<String> dates = queryUseCase.getAvailableDataDates();
            return ResponseEntity.ok(dates);
        } catch (Exception e) {
            log.error("사용 가능한 데이터 날짜 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "음식점 데이터 통계", 
        description = "특정 날짜의 음식점 데이터 통계를 조회합니다."
    )
    @GetMapping("/statistics")
    public ResponseEntity<TouristRestaurantQueryUseCase.RestaurantDataStatistics> getRestaurantStatistics(
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식, 생략시 최신 데이터)", example = "20250807")
            @RequestParam(required = false) String dataDate
    ) {
        log.info("음식점 데이터 통계 조회 요청 - 날짜: {}", dataDate);
        
        try {
            TouristRestaurantQueryUseCase.RestaurantDataStatistics statistics = queryUseCase.getRestaurantStatistics(dataDate);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("음식점 데이터 통계 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "수동 배치 실행", 
        description = "음식점 정보 수집 배치를 수동으로 실행합니다. (관리자용)"
    )
    @PostMapping("/batch/manual")
    public ResponseEntity<TouristRestaurantBatchUseCase.TouristRestaurantBatchResult> executeManualBatch(
            @Parameter(description = "배치 실행 날짜 (YYYYMMDD 형식, 생략시 오늘 날짜)", example = "20250807")
            @RequestParam(required = false) String dataDate
    ) {
        log.info("수동 배치 실행 요청 - 날짜: {}", dataDate);
        
        try {
            TouristRestaurantBatchUseCase.TouristRestaurantBatchResult result;
            if (dataDate != null) {
                result = dailyBatch.executeManualBatch(dataDate);
            } else {
                result = dailyBatch.executeManualBatch();
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("수동 배치 실행 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
