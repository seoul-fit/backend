package com.seoulfit.backend.publicdata.culture.adapter.in.web;

import com.seoulfit.backend.publicdata.culture.adapter.in.web.dto.CulturalReservationResponse;
import com.seoulfit.backend.publicdata.culture.application.port.in.QueryCulturalReservationUseCase;
import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
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

@Tag(name = "서울시 문화행사 예약 정보", description = "서울시 문화행사 예약 정보 조회 API")
@RestController
@RequestMapping("/api/v1/cultural-reservations")
@RequiredArgsConstructor
@Slf4j
public class CulturalReservationController {

    private final QueryCulturalReservationUseCase queryCulturalReservationUseCase;

    @Operation(
        summary = "문화행사 예약 정보 전체 조회", 
        description = "서울시 모든 문화행사 예약 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CulturalReservationResponse.class)
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/all")
    public ResponseEntity<List<CulturalReservationResponse>> getAllCulturalReservations(
            @Parameter(description = "시작 인덱스", example = "1")
            @RequestParam(defaultValue = "1") int startIndex,
            @Parameter(description = "종료 인덱스", example = "1000")
            @RequestParam(defaultValue = "1000") int endIndex,
            @Parameter(description = "최소 클래스명", example = "")
            @RequestParam(defaultValue = "") String minClassName
    ) {
        log.info("문화행사 예약 정보 조회 요청 - 시작: {}, 종료: {}, 카테고리: {}", startIndex, endIndex, minClassName);
        
        try {
            List<CulturalReservation> reservations = queryCulturalReservationUseCase.getAllCulturalReservation();
            List<CulturalReservationResponse> response = CulturalReservationResponse.from(reservations);
            
            log.info("문화행사 예약 정보 {} 개 조회 완료", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("문화행사 예약 정보 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "근처 문화행사 예약 정보 조회", 
        description = "지정된 위도, 경도 기준 반경 2km 내의 문화행사 예약 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CulturalReservationResponse.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/nearby")
    public ResponseEntity<List<CulturalReservationResponse>> getNearbyReservations(
            @Parameter(description = "위도", example = "37.5665", required = true)
            @RequestParam String latitude,
            @Parameter(description = "경도", example = "126.9780", required = true)
            @RequestParam String longitude
    ) {
        log.info("근처 문화행사 예약 정보 조회 요청 - 위도: {}, 경도: {}", latitude, longitude);
        
        try {
            List<CulturalReservation> reservations = queryCulturalReservationUseCase.getCulturalSpaceByLatitudeAndLongitude(latitude, longitude);
            List<CulturalReservationResponse> response = CulturalReservationResponse.from(reservations);
            
            log.info("근처 문화행사 예약 정보 {} 개 조회 완료", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("근처 문화행사 예약 정보 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
