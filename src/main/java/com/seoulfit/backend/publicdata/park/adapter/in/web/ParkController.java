package com.seoulfit.backend.publicdata.park.adapter.in.web;

import com.seoulfit.backend.publicdata.park.adapter.in.web.dto.ParkResponse;
import com.seoulfit.backend.publicdata.park.adapter.in.web.dto.ParkSummaryResponse;
import com.seoulfit.backend.publicdata.park.application.port.in.ParkQueryUseCase;
import com.seoulfit.backend.publicdata.park.domain.Park;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 서울시 공원 정보 REST Controller
 * 헥사고날 아키텍처의 입력 어댑터
 */
@Slf4j
@RestController
@RequestMapping("/api/parks")
@RequiredArgsConstructor
@Tag(name = "서울시 공원 정보", description = "서울시 공원 정보 조회 API")
public class ParkController {
    private final ParkQueryUseCase queryUseCase;

    @Operation(
        summary = "서울시 공원 전체 조회", 
        description = "서울시 모든 공원의 요약 정보를 조회합니다. 목록 표시에 최적화된 응답을 제공합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ParkSummaryResponse.class)
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/all")
    public ResponseEntity<List<ParkSummaryResponse>> getAllParks() {
        log.info("공원 전체 조회 요청");
        
        List<Park> parks = queryUseCase.getAllPark();
        List<ParkSummaryResponse> response = ParkSummaryResponse.from(parks);
        
        log.info("공원 {} 개 조회 완료", response.size());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "근처 공원 조회", 
        description = "지정된 위도, 경도 기준 반경 2km 내의 공원 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ParkResponse.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/nearby")
    public ResponseEntity<List<ParkResponse>> getParksNearby(
            @Parameter(description = "위도", example = "37.5665", required = true)
            @RequestParam String latitude,
            @Parameter(description = "경도", example = "126.9780", required = true)
            @RequestParam String longitude
    ) {
        log.info("근처 공원 조회 요청 - 위도: {}, 경도: {}", latitude, longitude);
        
        List<Park> parks = queryUseCase.getParkByLatitudeAndLongitude(latitude, longitude);
        List<ParkResponse> response = ParkResponse.from(parks);
        
        log.info("근처 공원 {} 개 조회 완료", response.size());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "공원 상세 정보 조회", 
        description = "특정 공원의 상세 정보를 조회합니다. (향후 구현 예정)"
    )
    @GetMapping("/detail")
    public ResponseEntity<ParkResponse> getParkDetail(
            @Parameter(description = "공원 ID", example = "1", required = true)
            @RequestParam Long parkId
    ) {
        // TODO: 상세 조회 기능 구현 예정
        log.info("공원 상세 조회 요청 - ID: {}", parkId);
        return ResponseEntity.ok().build();
    }
}
