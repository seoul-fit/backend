package com.seoulfit.backend.publicdata.culture.adapter.in.web;

import com.seoulfit.backend.publicdata.culture.application.port.in.QueryCulturalReservationUseCase;
import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "서울시 문화행사 예약 정보 관련 API", description = "CulturalReservationController.class")
@RestController
@RequestMapping("/api/v1/cultural-reservation")
@RequiredArgsConstructor
@Slf4j
public class CulturalReservationController {

    private final QueryCulturalReservationUseCase queryCulturalReservationUseCase;

    @Operation(summary = "문화 생활 예약 정보 관련 API", description = "문화 행사 예약 정보 전체 조회.")
    @GetMapping("/fetch")
    public ResponseEntity<?> fetchCulturalReservations(
            @RequestParam(defaultValue = "1") int startIndex,
            @RequestParam(defaultValue = "1000") int endIndex,
            @RequestParam(defaultValue = "") String minClassName) {
        try {
            log.info("Fetching cultural reservations from {} to {} with category: {}", startIndex, endIndex, minClassName);

            List<CulturalReservation> allCulturalReservation = queryCulturalReservationUseCase.getAllCulturalReservation();

            if (allCulturalReservation == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "API 응답이 null입니다.",
                        "data", null
                ));
            }
            return ResponseEntity.ok(allCulturalReservation);
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


    // 내일 와서 헥사고날 아키텍쳐 기반으로 틀 잡고 만들기!
    @Operation(summary = "위도, 경도 기반 2km 내에 문화 행사 예약 정보를 가져온다.", description = "문화 행사 예약 정보 조회 반경(2km)")
    @GetMapping("/nearby")
    public ResponseEntity<?> getNearby(@RequestParam String latitude,
                                       @RequestParam String longitude) {
        return ResponseEntity.ok(queryCulturalReservationUseCase.getCulturalSpaceByLatitudeAndLongitude(latitude, longitude));
    }

}
