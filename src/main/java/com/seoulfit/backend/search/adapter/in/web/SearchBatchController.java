package com.seoulfit.backend.search.adapter.in.web;

import com.seoulfit.backend.search.application.port.in.SearchIndexBatchUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "POI 검색 배치", description = "POI 인덱스 동기화 배치 관리 API")
@RestController
@RequestMapping("/api/search/batch")
public class SearchBatchController {
    
    private final SearchIndexBatchUseCase searchIndexBatchUseCase;
    
    public SearchBatchController(SearchIndexBatchUseCase searchIndexBatchUseCase) {
        this.searchIndexBatchUseCase = searchIndexBatchUseCase;
    }
    
    @Operation(summary = "전체 POI 인덱스 동기화", 
               description = "모든 공공데이터를 POI 인덱스 테이블에 동기화합니다.")
    @PostMapping("/sync-all")
    public ResponseEntity<String> syncAllPublicData() {
        try {
            searchIndexBatchUseCase.syncAllPublicDataToIndex();
            return ResponseEntity.ok("POI 인덱스 전체 동기화가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("POI 인덱스 동기화 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(summary = "무더위쉼터 동기화", 
               description = "무더위쉼터 데이터를 POI 인덱스 테이블에 동기화합니다.")
    @PostMapping("/sync-cooling-centers")
    public ResponseEntity<String> syncCoolingCenters() {
        try {
            searchIndexBatchUseCase.syncCoolingCentersToIndex();
            return ResponseEntity.ok("무더위쉼터 POI 인덱스 동기화가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("무더위쉼터 동기화 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(summary = "문화행사 동기화", 
               description = "문화행사 데이터를 POI 인덱스 테이블에 동기화합니다.")
    @PostMapping("/sync-cultural-events")
    public ResponseEntity<String> syncCulturalEvents() {
        try {
            searchIndexBatchUseCase.syncCulturalEventsToIndex();
            return ResponseEntity.ok("문화행사 POI 인덱스 동기화가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("문화행사 동기화 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(summary = "문화예약 동기화", 
               description = "문화예약 데이터를 POI 인덱스 테이블에 동기화합니다.")
    @PostMapping("/sync-cultural-reservations")
    public ResponseEntity<String> syncCulturalReservations() {
        try {
            searchIndexBatchUseCase.syncCulturalReservationsToIndex();
            return ResponseEntity.ok("문화예약 POI 인덱스 동기화가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("문화예약 동기화 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(summary = "도서관 동기화", 
               description = "도서관 데이터를 POI 인덱스 테이블에 동기화합니다.")
    @PostMapping("/sync-libraries")
    public ResponseEntity<String> syncLibraries() {
        try {
            searchIndexBatchUseCase.syncLibrariesToIndex();
            return ResponseEntity.ok("도서관 POI 인덱스 동기화가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("도서관 동기화 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(summary = "공원 동기화", 
               description = "공원 데이터를 POI 인덱스 테이블에 동기화합니다.")
    @PostMapping("/sync-parks")
    public ResponseEntity<String> syncParks() {
        try {
            searchIndexBatchUseCase.syncParksToIndex();
            return ResponseEntity.ok("공원 POI 인덱스 동기화가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("공원 동기화 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(summary = "음식점 동기화", 
               description = "음식점 데이터를 POI 인덱스 테이블에 동기화합니다.")
    @PostMapping("/sync-restaurants")
    public ResponseEntity<String> syncRestaurants() {
        try {
            searchIndexBatchUseCase.syncRestaurantsToIndex();
            return ResponseEntity.ok("음식점 POI 인덱스 동기화가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("음식점 동기화 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(summary = "POI 인덱스 전체 삭제", 
               description = "POI 인덱스 테이블의 모든 데이터를 삭제합니다.")
    @PostMapping("/clear-index")
    public ResponseEntity<String> clearAllIndexData() {
        try {
            searchIndexBatchUseCase.clearAllIndexData();
            return ResponseEntity.ok("POI 인덱스 데이터가 모두 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("POI 인덱스 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}