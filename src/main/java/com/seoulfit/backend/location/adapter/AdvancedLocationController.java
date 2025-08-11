package com.seoulfit.backend.location.adapter;

import com.seoulfit.backend.location.adapter.dto.AdvancedLocationRequest;
import com.seoulfit.backend.location.adapter.dto.AdvancedLocationResponse;
import com.seoulfit.backend.location.application.AdvancedLocationDataService;
import com.seoulfit.backend.location.domain.MapScale;
import com.seoulfit.backend.user.adapter.out.persistence.UserInterestPort;
import com.seoulfit.backend.user.adapter.out.persistence.UserPort;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 고도화된 위치 기반 데이터 조회 컨트롤러
 * 
 * 지도 축적정보를 활용한 최적화된 위치 기반 데이터 조회 API
 * 2단계 필터링과 성능 최적화 기법을 적용한 고급 기능 제공
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/location/advanced")
@RequiredArgsConstructor
@Tag(name = "Advanced Location Data", description = "고도화된 위치 기반 데이터 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class AdvancedLocationController {

    private final AdvancedLocationDataService advancedLocationDataService;
    private final UserPort userPort;
    private final UserInterestPort userInterestPort;

    @Operation(
        summary = "지도 축적정보 기반 고도화된 위치 데이터 조회", 
        description = "지도의 축적(Scale) 정보를 활용하여 최적화된 위치 기반 데이터를 조회합니다. " +
                     "2단계 필터링(바운딩 박스 → 원형 반경)으로 성능을 최적화하고, " +
                     "사용자 관심사에 따른 맞춤형 데이터를 제공합니다."
    )
    @PostMapping("/personalized")
    public ResponseEntity<AdvancedLocationResponse> getAdvancedPersonalizedData(
            @Valid @RequestBody AdvancedLocationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("고도화된 개인화 위치 데이터 조회 요청: userId={}, lat={}, lng={}, scale={}", 
                userDetails.getUsername(), request.getLatitude(), request.getLongitude(), 
                request.getMapScale() != null ? request.getMapScale().getDisplayName() : "AUTO");

        // 사용자 정보 및 관심사 조회
        User user = userPort.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        List<InterestCategory> userInterests = userInterestPort.findInterestsByUserId(user.getId());
        List<String> interestStrings = userInterests.stream().map(Enum::name).toList();

        // 지도 축적 정보 결정
        MapScale mapScale = determineMapScale(request);

        // 고도화된 위치 데이터 조회
        AdvancedLocationDataService.AdvancedLocationData data = 
                advancedLocationDataService.findAdvancedLocationData(
                        request.getLatitude(), 
                        request.getLongitude(), 
                        mapScale, 
                        interestStrings, 
                        request.getMaxResults());

        AdvancedLocationResponse response = AdvancedLocationResponse.from(data, userInterests);

        log.info("고도화된 개인화 위치 데이터 조회 완료: userId={}, 총 {}개 항목, 평균거리 {:.2f}km", 
                user.getId(), response.getTotalCount(), response.getAverageDistance());

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "지도 축적정보 기반 통합 위치 데이터 조회", 
        description = "사용자 관심사와 관계없이 모든 카테고리의 위치 데이터를 조회합니다."
    )
    @PostMapping("/comprehensive")
    public ResponseEntity<AdvancedLocationResponse> getAdvancedComprehensiveData(
            @Valid @RequestBody AdvancedLocationRequest request) {
        
        log.info("고도화된 통합 위치 데이터 조회 요청: lat={}, lng={}, scale={}", 
                request.getLatitude(), request.getLongitude(), 
                request.getMapScale() != null ? request.getMapScale().getDisplayName() : "AUTO");

        // 모든 관심사 포함
        List<String> allInterests = List.of("RESTAURANTS", "LIBRARIES", "PARKS", 
                                          "SPORTS_FACILITIES", "COOLING_CENTERS");

        // 지도 축적 정보 결정
        MapScale mapScale = determineMapScale(request);

        // 고도화된 위치 데이터 조회
        AdvancedLocationDataService.AdvancedLocationData data = 
                advancedLocationDataService.findAdvancedLocationData(
                        request.getLatitude(), 
                        request.getLongitude(), 
                        mapScale, 
                        allInterests, 
                        request.getMaxResults());

        AdvancedLocationResponse response = AdvancedLocationResponse.from(data, List.of());

        log.info("고도화된 통합 위치 데이터 조회 완료: 총 {}개 항목, 평균거리 {:.2f}km", 
                response.getTotalCount(), response.getAverageDistance());

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "지도 축적정보 추천", 
        description = "현재 위치와 요청 조건에 따라 최적의 지도 축적정보를 추천합니다."
    )
    @GetMapping("/recommend-scale")
    public ResponseEntity<MapScaleRecommendation> recommendMapScale(
            @Parameter(description = "위도", example = "37.5665", required = true)
            @RequestParam Double latitude,
            
            @Parameter(description = "경도", example = "126.9780", required = true)
            @RequestParam Double longitude,
            
            @Parameter(description = "줌 레벨 (Google Maps 스타일)", example = "15")
            @RequestParam(required = false) Integer zoomLevel,
            
            @Parameter(description = "픽셀당 미터", example = "10.5")
            @RequestParam(required = false) Double metersPerPixel,
            
            @Parameter(description = "바운딩 박스 너비 (km)", example = "2.0")
            @RequestParam(required = false) Double boundingBoxWidthKm,
            
            @Parameter(description = "바운딩 박스 높이 (km)", example = "1.5")
            @RequestParam(required = false) Double boundingBoxHeightKm) {
        
        log.info("지도 축적정보 추천 요청: lat={}, lng={}, zoom={}, metersPerPixel={}, bbox=[{}, {}]", 
                latitude, longitude, zoomLevel, metersPerPixel, boundingBoxWidthKm, boundingBoxHeightKm);

        MapScale recommendedScale;

        // 우선순위에 따라 축적 정보 결정
        if (zoomLevel != null) {
            recommendedScale = MapScale.fromZoomLevel(zoomLevel);
        } else if (metersPerPixel != null) {
            recommendedScale = MapScale.fromPixelResolution(metersPerPixel);
        } else if (boundingBoxWidthKm != null && boundingBoxHeightKm != null) {
            recommendedScale = MapScale.fromBoundingBoxSize(boundingBoxWidthKm, boundingBoxHeightKm);
        } else {
            recommendedScale = MapScale.DISTRICT_MEDIUM; // 기본값
        }

        MapScaleRecommendation recommendation = MapScaleRecommendation.builder()
                .recommendedScale(recommendedScale)
                .searchRadiusKm(recommendedScale.getRadiusKm())
                .boundingBox(recommendedScale.getBoundingBox(latitude, longitude))
                .description(recommendedScale.getDescription())
                .reason(determineRecommendationReason(zoomLevel, metersPerPixel, boundingBoxWidthKm, boundingBoxHeightKm))
                .build();

        log.info("지도 축적정보 추천 완료: scale={}, radius={}km", 
                recommendedScale.getDisplayName(), recommendedScale.getRadiusKm());

        return ResponseEntity.ok(recommendation);
    }

    /**
     * 요청에서 지도 축적 정보 결정
     */
    private MapScale determineMapScale(AdvancedLocationRequest request) {
        if (request.getMapScale() != null) {
            return request.getMapScale();
        }
        
        if (request.getZoomLevel() != null) {
            return MapScale.fromZoomLevel(request.getZoomLevel());
        }
        
        if (request.getMetersPerPixel() != null) {
            return MapScale.fromPixelResolution(request.getMetersPerPixel());
        }
        
        if (request.getBoundingBoxWidthKm() != null && request.getBoundingBoxHeightKm() != null) {
            return MapScale.fromBoundingBoxSize(request.getBoundingBoxWidthKm(), request.getBoundingBoxHeightKm());
        }
        
        // 기본값
        return MapScale.DISTRICT_MEDIUM;
    }

    /**
     * 추천 이유 결정
     */
    private String determineRecommendationReason(Integer zoomLevel, Double metersPerPixel, 
                                                Double boundingBoxWidthKm, Double boundingBoxHeightKm) {
        if (zoomLevel != null) {
            return "줌 레벨 " + zoomLevel + "을 기반으로 추천";
        } else if (metersPerPixel != null) {
            return "픽셀 해상도 " + metersPerPixel + "m/px를 기반으로 추천";
        } else if (boundingBoxWidthKm != null && boundingBoxHeightKm != null) {
            return String.format("바운딩 박스 크기 %.1f×%.1fkm를 기반으로 추천", boundingBoxWidthKm, boundingBoxHeightKm);
        } else {
            return "기본 설정으로 추천";
        }
    }

    /**
     * 지도 축적정보 추천 응답
     */
    @lombok.Builder
    @lombok.Getter
    public static class MapScaleRecommendation {
        private final MapScale recommendedScale;
        private final Double searchRadiusKm;
        private final MapScale.BoundingBox boundingBox;
        private final String description;
        private final String reason;
    }
}
