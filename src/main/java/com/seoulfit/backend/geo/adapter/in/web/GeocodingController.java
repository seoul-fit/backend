package com.seoulfit.backend.geo.adapter.in.web;

import com.seoulfit.backend.geo.application.port.in.GeocodingUseCase;
import com.seoulfit.backend.geo.domain.AdministrativeDistrict;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 지오코딩 관련 REST API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/geocoding")
@RequiredArgsConstructor
@Tag(name = "Geocoding", description = "지오코딩 API - 위경도를 행정구역으로 변환")
public class GeocodingController {
    
    private final GeocodingUseCase geocodingUseCase;
    
    @GetMapping("/administrative-district")
    @Operation(
        summary = "위경도로 행정구역 조회",
        description = "위도와 경도를 입력받아 해당하는 행정구역 정보를 반환합니다."
    )
    public ResponseEntity<AdministrativeDistrictResponse> getAdministrativeDistrict(
            @Parameter(description = "위도", example = "37.5665")
            @RequestParam double latitude,
            @Parameter(description = "경도", example = "126.9780")
            @RequestParam double longitude) {
        
        log.debug("행정구역 조회 요청: lat={}, lng={}", latitude, longitude);
        
        Optional<AdministrativeDistrict> district = geocodingUseCase
                .getAdministrativeDistrict(latitude, longitude);
        
        if (district.isPresent()) {
            AdministrativeDistrictResponse response = AdministrativeDistrictResponse
                    .from(district.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/administrative-district")
    @Operation(
        summary = "위경도로 행정구역 조회 (POST)",
        description = "요청 본문에 위도와 경도를 포함하여 해당하는 행정구역 정보를 반환합니다."
    )
    public ResponseEntity<AdministrativeDistrictResponse> getAdministrativeDistrictPost(
            @RequestBody CoordinateRequest request) {
        
        log.debug("행정구역 조회 요청 (POST): {}", request);
        
        Optional<AdministrativeDistrict> district = geocodingUseCase
                .getAdministrativeDistrict(request.getLatitude(), request.getLongitude());
        
        if (district.isPresent()) {
            AdministrativeDistrictResponse response = AdministrativeDistrictResponse
                    .from(district.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
