package com.seoulfit.backend.presentation.culture;

import com.seoulfit.backend.presentation.culture.dtos.response.SeoulReservationApiResponse;
import com.seoulfit.backend.application.service.impl.CulturalReservationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "서울시 문화생활 예약 정보 관련 API", description = "CulturalReservationController.class")
@RestController
@RequestMapping("/api/v1/cultural-reservation")
@RequiredArgsConstructor
@Slf4j
public class CulturalReservationController {
    
    private final CulturalReservationServiceImpl reservationService;

    @Operation(summary = "문화 생활 예약 정보 관련 API", description = "문화 생활 예약 정보를 가져온다.")
    @GetMapping("/fetch")
    public ResponseEntity<Map<String, Object>> fetchCulturalReservations(
            @RequestParam(defaultValue = "1") int startIndex,
            @RequestParam(defaultValue = "10") int endIndex,
            @RequestParam(defaultValue = "") String minClassName) {
        try {
            log.info("Fetching cultural reservations from {} to {} with category: {}", startIndex, endIndex, minClassName);
            
            SeoulReservationApiResponse response = reservationService.fetchCulturalReservations(startIndex, endIndex, minClassName);
            
            if (response == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "API 응답이 null입니다.",
                        "data", null
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "문화 생활 예약 정보 관련 데이터 가져오기 완료",
                    "totalCount", response.getListTotalCount(),
                    "resultCode", response.getResult() != null ? response.getResult().getCode() : "UNKNOWN",
                    "resultMessage", response.getResult() != null ? response.getResult().getMessage() : "No result info",
                    "dataCount", response.getRow() != null ? response.getRow().size() : 0,
                    "data", response
            ));
        } catch (Exception e) {
            log.error("Error fetching cultural reservations", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "문화생활 예약 정보 데이터 가져오기에 실패했습니다.",
                    "error", e.getMessage(),
                    "errorType", e.getClass().getSimpleName()
            ));
        }
    }

    @Operation(summary = "카테고리별 예약 정보 조회", description = "소분류명으로 예약 정보를 조회합니다.")
    @GetMapping("/category/{minClassName}")
    public ResponseEntity<Map<String, Object>> fetchReservationsByCategory(@PathVariable String minClassName) {
        try {
            log.info("Fetching reservations by category: {}", minClassName);
            
            SeoulReservationApiResponse response = reservationService.fetchReservationsByCategory(minClassName);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "카테고리별 예약 정보 조회 완료",
                    "category", minClassName,
                    "totalCount", response.getListTotalCount(),
                    "dataCount", response.getRow() != null ? response.getRow().size() : 0,
                    "data", response
            ));
        } catch (Exception e) {
            log.error("Error fetching reservations by category: {}", minClassName, e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "카테고리별 예약 정보 조회에 실패했습니다.",
                    "error", e.getMessage()
            ));
        }
    }

    @Operation(summary = "지역별 예약 정보 조회", description = "지역명으로 예약 정보를 조회합니다.")
    @GetMapping("/area/{areaName}")
    public ResponseEntity<Map<String, Object>> fetchReservationsByArea(@PathVariable String areaName) {
        try {
            log.info("Fetching reservations by area: {}", areaName);
            
            SeoulReservationApiResponse response = reservationService.fetchReservationsByArea(areaName);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "지역별 예약 정보 조회 완료",
                    "area", areaName,
                    "totalCount", response.getListTotalCount(),
                    "dataCount", response.getRow() != null ? response.getRow().size() : 0,
                    "data", response
            ));
        } catch (Exception e) {
            log.error("Error fetching reservations by area: {}", areaName, e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "지역별 예약 정보 조회에 실패했습니다.",
                    "error", e.getMessage()
            ));
        }
    }

    @Operation(summary = "예약 API 상태 확인", description = "서울시 예약 API의 상태를 확인합니다.")
    @GetMapping("/api-status")
    public ResponseEntity<Map<String, Object>> getApiStatus() {
        try {
            boolean isHealthy = reservationService.isApiHealthy();
            boolean isKeyValid = reservationService.validateApiKey();
            
            return ResponseEntity.ok(Map.of(
                    "apiHealthy", isHealthy,
                    "apiKeyValid", isKeyValid,
                    "status", isHealthy && isKeyValid ? "HEALTHY" : "UNHEALTHY",
                    "message", isHealthy && isKeyValid ? 
                              "서울시 예약 API가 정상 작동 중입니다." : 
                              "서울시 예약 API에 문제가 있습니다."
            ));
        } catch (Exception e) {
            log.error("Error checking API status", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "apiHealthy", false,
                    "apiKeyValid", false,
                    "status", "ERROR",
                    "message", "API 상태 확인 중 오류가 발생했습니다.",
                    "error", e.getMessage()
            ));
        }
    }

    @Operation(summary = "예약 API 테스트 호출", description = "단일 데이터로 API 테스트")
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testApiCall() {
        try {
            log.info("Testing cultural reservation API with single record");
            
            SeoulReservationApiResponse response = reservationService.fetchCulturalReservations(1, 1, "");
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "테스트 호출 완료",
                    "hasResponse", response != null,
                    "hasResult", response != null && response.getResult() != null,
                    "resultCode", response != null && response.getResult() != null ? response.getResult().getCode() : "NO_RESULT",
                    "resultMessage", response != null && response.getResult() != null ? response.getResult().getMessage() : "NO_MESSAGE",
                    "hasData", response != null && response.getRow() != null && !response.getRow().isEmpty(),
                    "dataCount", response != null && response.getRow() != null ? response.getRow().size() : 0,
                    "totalCount", response != null ? response.getListTotalCount() : 0,
                    "rawResponse", response
            ));
        } catch (Exception e) {
            log.error("Error in test API call", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "테스트 호출 실패",
                    "error", e.getMessage(),
                    "stackTrace", e.getStackTrace()
            ));
        }
    }
}
