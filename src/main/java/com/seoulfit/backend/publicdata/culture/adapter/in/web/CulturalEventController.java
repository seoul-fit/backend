package com.seoulfit.backend.publicdata.culture.adapter.in.web;

import com.seoulfit.backend.publicdata.culture.adapter.in.web.dto.CulturalEventResponse;
import com.seoulfit.backend.publicdata.culture.adapter.in.web.dto.CulturalEventSummaryResponse;
import com.seoulfit.backend.publicdata.culture.application.port.in.QueryCulturalEventsUseCase;
import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
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
 * 문화행사 컨트롤러
 * <p>
 * 서울시 문화행사 정보 조회 API를 제공하는 REST 컨트롤러입니다.
 * 헥사고날 아키텍처의 입력 어댑터로서 HTTP 요청을 처리하고
 * 애플리케이션 서비스를 호출합니다.
 * </p>
 * 
 * @author Seoul Fit
 * @since 1.0.0
 * @see QueryCulturalEventsUseCase
 */
@Tag(name = "서울시 문화행사 정보", description = "서울시 문화행사 정보 조회 API")
@RestController
@RequestMapping("/api/v1/cultural-events")
@RequiredArgsConstructor
@Slf4j
public class CulturalEventController {
    private final QueryCulturalEventsUseCase queryCulturalEventsUseCase;

    @Operation(
        summary = "문화행사 전체 조회", 
        description = "서울시 모든 문화행사의 요약 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CulturalEventSummaryResponse.class)
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    /**
     * 모든 문화행사 정보를 조회합니다.
     * 
     * @return 문화행사 요약 정보 목록
     */
    @GetMapping("/all")
    public ResponseEntity<List<CulturalEventSummaryResponse>> getAllCulturalEvents() {
        log.info("문화행사 전체 조회 요청");
        
        try {
            List<CulturalEvent> events = queryCulturalEventsUseCase.getAllCulturalEvents();
            List<CulturalEventSummaryResponse> response = CulturalEventSummaryResponse.from(events);
            
            log.info("문화행사 {} 개 조회 완료", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("문화행사 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "근처 문화행사 조회", 
        description = "지정된 위도, 경도 기준 반경 2km 내의 문화행사 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CulturalEventResponse.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    /**
     * 지정된 위치 주변의 문화행사를 조회합니다.
     * 
     * @param latitude 위도 (WGS84 좌표계)
     * @param longitude 경도 (WGS84 좌표계)
     * @param radius 검색 반경 (미터 단위, 기본값 2000m)
     * @return 주변 문화행사 정보 목록
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<CulturalEventResponse>> getNearbyEvents(
            @Parameter(description = "위도", example = "37.5665", required = true)
            @RequestParam String latitude,
            @Parameter(description = "경도", example = "126.9780", required = true)
            @RequestParam String longitude
    ) {
        log.info("근처 문화행사 조회 요청 - 위도: {}, 경도: {}", latitude, longitude);
        
        try {
            List<CulturalEvent> events = queryCulturalEventsUseCase.getCulturalEventByLatitudeAndLongitude(latitude, longitude);
            List<CulturalEventResponse> response = CulturalEventResponse.from(events);
            
            log.info("근처 문화행사 {} 개 조회 완료", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("근처 문화행사 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
