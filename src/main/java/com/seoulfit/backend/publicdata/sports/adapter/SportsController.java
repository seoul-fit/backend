package com.seoulfit.backend.publicdata.sports.adapter;

import com.seoulfit.backend.publicdata.sports.application.SportsService;
import com.seoulfit.backend.publicdata.sports.domain.Sports;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 체육시설 정보 API 컨트롤러
 * 
 * 서울시 체육시설 정보 조회 및 관리 API를 제공합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Tag(name = "Sports", description = "체육시설 정보 API")
@Slf4j
@RestController
@RequestMapping("/api/sports")
@RequiredArgsConstructor
public class SportsController {

    private final SportsService sportsService;

    /**
     * 모든 체육시설 조회
     */
    @Operation(summary = "모든 체육시설 조회", description = "등록된 모든 체육시설 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<Sports>> getAllSports() {
        List<Sports> sports = sportsService.getAllSports();
        return ResponseEntity.ok(sports);
    }

    /**
     * 위치 기반 체육시설 조회
     */
    @Operation(summary = "위치 기반 체육시설 조회", description = "특정 위치 반경 내의 체육시설을 조회합니다.")
    @GetMapping("/nearby")
    public ResponseEntity<List<Sports>> getSportsNearby(
            @Parameter(description = "위도", example = "37.5665")
            @RequestParam double latitude,
            @Parameter(description = "경도", example = "126.9780")
            @RequestParam double longitude,
            @Parameter(description = "반경(km)", example = "2.0")
            @RequestParam(defaultValue = "2.0") double radiusKm) {
        
        List<Sports> sports = sportsService.getSportsNearby(latitude, longitude, radiusKm);
        return ResponseEntity.ok(sports);
    }

    /**
     * 체육시설 통계 정보 조회
     */
    @Operation(summary = "체육시설 통계 정보 조회", description = "체육시설 등록 현황 및 통계 정보를 조회합니다.")
    @GetMapping("/stats")
    public ResponseEntity<SportsService.SportsStats> getSportsStats() {
        SportsService.SportsStats stats = sportsService.getSportsStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 체육시설 데이터 동기화 (관리자용)
     */
    @Operation(summary = "체육시설 데이터 동기화", description = "서울시 API에서 최신 체육시설 데이터를 가져와 동기화합니다.")
    @PostMapping("/sync")
    public ResponseEntity<SyncResponse> syncSportsData() {
        log.info("체육시설 데이터 동기화 요청");
        
        try {
            int syncedCount = sportsService.syncSportsData();
            int geoCodedCount = sportsService.processGeoCoding();
            
            SyncResponse response = SyncResponse.builder()
                    .success(true)
                    .syncedCount(syncedCount)
                    .geoCodedCount(geoCodedCount)
                    .message("체육시설 데이터 동기화가 완료되었습니다.")
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("체육시설 데이터 동기화 실패", e);
            
            SyncResponse response = SyncResponse.builder()
                    .success(false)
                    .syncedCount(0)
                    .geoCodedCount(0)
                    .message("체육시설 데이터 동기화 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 지오코딩 처리 (관리자용)
     */
    @Operation(summary = "지오코딩 처리", description = "위경도 정보가 없는 체육시설들의 지오코딩을 처리합니다.")
    @PostMapping("/geocoding")
    public ResponseEntity<GeoCodingResponse> processGeoCoding() {
        log.info("체육시설 지오코딩 처리 요청");
        
        try {
            int processedCount = sportsService.processGeoCoding();
            
            GeoCodingResponse response = GeoCodingResponse.builder()
                    .success(true)
                    .processedCount(processedCount)
                    .message("지오코딩 처리가 완료되었습니다.")
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("지오코딩 처리 실패", e);
            
            GeoCodingResponse response = GeoCodingResponse.builder()
                    .success(false)
                    .processedCount(0)
                    .message("지오코딩 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 동기화 응답 DTO
     */
    public static class SyncResponse {
        private boolean success;
        private int syncedCount;
        private int geoCodedCount;
        private String message;

        public static SyncResponseBuilder builder() {
            return new SyncResponseBuilder();
        }

        public static class SyncResponseBuilder {
            private boolean success;
            private int syncedCount;
            private int geoCodedCount;
            private String message;

            public SyncResponseBuilder success(boolean success) {
                this.success = success;
                return this;
            }

            public SyncResponseBuilder syncedCount(int syncedCount) {
                this.syncedCount = syncedCount;
                return this;
            }

            public SyncResponseBuilder geoCodedCount(int geoCodedCount) {
                this.geoCodedCount = geoCodedCount;
                return this;
            }

            public SyncResponseBuilder message(String message) {
                this.message = message;
                return this;
            }

            public SyncResponse build() {
                SyncResponse response = new SyncResponse();
                response.success = this.success;
                response.syncedCount = this.syncedCount;
                response.geoCodedCount = this.geoCodedCount;
                response.message = this.message;
                return response;
            }
        }

        // Getters
        public boolean isSuccess() { return success; }
        public int getSyncedCount() { return syncedCount; }
        public int getGeoCodedCount() { return geoCodedCount; }
        public String getMessage() { return message; }
    }

    /**
     * 지오코딩 응답 DTO
     */
    public static class GeoCodingResponse {
        private boolean success;
        private int processedCount;
        private String message;

        public static GeoCodingResponseBuilder builder() {
            return new GeoCodingResponseBuilder();
        }

        public static class GeoCodingResponseBuilder {
            private boolean success;
            private int processedCount;
            private String message;

            public GeoCodingResponseBuilder success(boolean success) {
                this.success = success;
                return this;
            }

            public GeoCodingResponseBuilder processedCount(int processedCount) {
                this.processedCount = processedCount;
                return this;
            }

            public GeoCodingResponseBuilder message(String message) {
                this.message = message;
                return this;
            }

            public GeoCodingResponse build() {
                GeoCodingResponse response = new GeoCodingResponse();
                response.success = this.success;
                response.processedCount = this.processedCount;
                response.message = this.message;
                return response;
            }
        }

        // Getters
        public boolean isSuccess() { return success; }
        public int getProcessedCount() { return processedCount; }
        public String getMessage() { return message; }
    }
}
