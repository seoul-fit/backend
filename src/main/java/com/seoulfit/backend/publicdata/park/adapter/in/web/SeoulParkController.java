package com.seoulfit.backend.publicdata.park.adapter.in.web;

import com.seoulfit.backend.publicdata.park.application.port.in.ParkQueryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 서울시 공원 정보 REST Controller
 * 헥사고날 아키텍처의 입력 어댑터
 */
@Slf4j
@RestController
@RequestMapping("/api/parks")
@RequiredArgsConstructor
@Tag(name = "서울시 공원 정보 관련 API", description = "SeoulParkController.class")
public class SeoulParkController {
    private final ParkQueryUseCase queryUseCase;

    @Operation(summary = "서울시 공원 전체 조회 API.", description = "공원 전체 조회")
    @GetMapping("/all")
    public ResponseEntity<?> getAllParks() {
        return ResponseEntity.ok(queryUseCase.getAllPark());
    }

    @Operation(summary = "위도, 경도 기반 2km 내에 공원 정보를 가져온다.", description = "공원 정보 조회(반경2km)")
    @GetMapping("/nearby")
    public ResponseEntity<?> getParksNearby(
            @RequestParam String latitude,
            @RequestParam String longitude
    ) {
        return ResponseEntity.ok(queryUseCase.getParkByLatitudeAndLongitude(latitude, longitude));
    }
}
