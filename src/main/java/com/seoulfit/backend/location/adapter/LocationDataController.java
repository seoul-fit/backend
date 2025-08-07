package com.seoulfit.backend.location.adapter;

import com.seoulfit.backend.location.adapter.dto.LocationDataResponse;
import com.seoulfit.backend.location.application.LocationBasedDataService;
import com.seoulfit.backend.user.adapter.out.persistence.UserInterestPort;
import com.seoulfit.backend.user.adapter.out.persistence.UserPort;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 위치 기반 데이터 조회 컨트롤러
 * 
 * 사용자 위치를 기반으로 주변의 다양한 시설 정보를 제공하는 API
 * 사용자 관심사에 따라 맞춤형 데이터를 제공하며, 지도에 마커를 그리기 위한 데이터를 반환
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
@Tag(name = "Location Data", description = "위치 기반 데이터 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class LocationDataController {

    private final LocationBasedDataService locationBasedDataService;
    private final UserPort userPort;
    private final UserInterestPort userInterestPort;

    @Operation(
        summary = "위치 기반 통합 데이터 조회", 
        description = "사용자 위치 주변의 모든 시설 정보를 통합하여 반환합니다. 지도 마커 표시용 데이터입니다."
    )
    @GetMapping("/nearby")
    public ResponseEntity<LocationDataResponse> getNearbyData(
            @Parameter(description = "위도", example = "37.5665", required = true)
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            
            @Parameter(description = "경도", example = "126.9780", required = true)
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            
            @Parameter(description = "검색 반경 (km)", example = "2.0")
            @RequestParam(defaultValue = "2.0") @Positive Double radius) {
        
        log.info("위치 기반 통합 데이터 조회 요청: lat={}, lng={}, radius={}km", latitude, longitude, radius);

        LocationBasedDataService.LocationBasedData data = locationBasedDataService.findNearbyData(
                latitude, longitude, radius);
        
        LocationDataResponse response = LocationDataResponse.from(data);
        
        log.info("위치 기반 데이터 조회 완료: 총 {}개 항목", response.getTotalCount());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "사용자 관심사 기반 위치 데이터 조회", 
        description = "사용자가 선택한 관심사에 따라 맞춤형 위치 기반 데이터를 조회합니다."
    )
    @GetMapping("/nearby/personalized")
    public ResponseEntity<LocationDataResponse> getPersonalizedNearbyData(
            @Parameter(description = "위도", example = "37.5665", required = true)
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            
            @Parameter(description = "경도", example = "126.9780", required = true)
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            
            @Parameter(description = "검색 반경 (km)", example = "2.0")
            @RequestParam(defaultValue = "2.0") @Positive Double radius,
            
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("개인화된 위치 기반 데이터 조회 요청: userId={}, lat={}, lng={}, radius={}km", 
                userDetails.getUsername(), latitude, longitude, radius);

        // 사용자 관심사 조회
        User user = userPort.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        List<InterestCategory> userInterests = userInterestPort.findInterestsByUserId(user.getId());
        
        // 관심사를 문자열 리스트로 변환
        List<String> interestStrings = userInterests.stream()
                .map(Enum::name)
                .toList();

        LocationBasedDataService.LocationBasedData data = locationBasedDataService.findNearbyDataByInterests(
                latitude, longitude, radius, interestStrings);
        
        LocationDataResponse response = LocationDataResponse.from(data);
        
        log.info("개인화된 위치 기반 데이터 조회 완료: userId={}, 관심사 개수={}, 총 {}개 항목", 
                user.getId(), userInterests.size(), response.getTotalCount());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "위치 기반 맛집 조회", 
        description = "사용자 위치 주변의 맛집 정보만 조회합니다."
    )
    @GetMapping("/restaurants")
    public ResponseEntity<LocationDataResponse.RestaurantsResponse> getNearbyRestaurants(
            @Parameter(description = "위도", example = "37.5665", required = true)
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            
            @Parameter(description = "경도", example = "126.9780", required = true)
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            
            @Parameter(description = "검색 반경 (km)", example = "2.0")
            @RequestParam(defaultValue = "2.0") @Positive Double radius) {
        
        log.info("위치 기반 맛집 조회 요청: lat={}, lng={}, radius={}km", latitude, longitude, radius);

        var restaurants = locationBasedDataService.findNearbyRestaurants(latitude, longitude, radius);
        var response = LocationDataResponse.RestaurantsResponse.from(restaurants);
        
        log.info("맛집 조회 완료: {}개", restaurants.size());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "위치 기반 도서관 조회", 
        description = "사용자 위치 주변의 도서관 정보만 조회합니다."
    )
    @GetMapping("/libraries")
    public ResponseEntity<LocationDataResponse.LibrariesResponse> getNearbyLibraries(
            @Parameter(description = "위도", example = "37.5665", required = true)
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            
            @Parameter(description = "경도", example = "126.9780", required = true)
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            
            @Parameter(description = "검색 반경 (km)", example = "2.0")
            @RequestParam(defaultValue = "2.0") @Positive Double radius) {
        
        log.info("위치 기반 도서관 조회 요청: lat={}, lng={}, radius={}km", latitude, longitude, radius);

        var libraries = locationBasedDataService.findNearbyLibraries(latitude, longitude, radius);
        var response = LocationDataResponse.LibrariesResponse.from(libraries);
        
        log.info("도서관 조회 완료: {}개", libraries.size());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "위치 기반 공원 조회", 
        description = "사용자 위치 주변의 공원 정보만 조회합니다."
    )
    @GetMapping("/parks")
    public ResponseEntity<LocationDataResponse.ParksResponse> getNearbyParks(
            @Parameter(description = "위도", example = "37.5665", required = true)
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            
            @Parameter(description = "경도", example = "126.9780", required = true)
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            
            @Parameter(description = "검색 반경 (km)", example = "2.0")
            @RequestParam(defaultValue = "2.0") @Positive Double radius) {
        
        log.info("위치 기반 공원 조회 요청: lat={}, lng={}, radius={}km", latitude, longitude, radius);

        var parks = locationBasedDataService.findNearbyParks(latitude, longitude, radius);
        var response = LocationDataResponse.ParksResponse.from(parks);
        
        log.info("공원 조회 완료: {}개", parks.size());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "위치 기반 체육시설 조회", 
        description = "사용자 위치 주변의 체육시설 정보만 조회합니다."
    )
    @GetMapping("/sports-facilities")
    public ResponseEntity<LocationDataResponse.SportsFacilitiesResponse> getNearbySportsFacilities(
            @Parameter(description = "위도", example = "37.5665", required = true)
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            
            @Parameter(description = "경도", example = "126.9780", required = true)
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            
            @Parameter(description = "검색 반경 (km)", example = "2.0")
            @RequestParam(defaultValue = "2.0") @Positive Double radius) {
        
        log.info("위치 기반 체육시설 조회 요청: lat={}, lng={}, radius={}km", latitude, longitude, radius);

        var facilities = locationBasedDataService.findNearbySportsFacilities(latitude, longitude, radius);
        var response = LocationDataResponse.SportsFacilitiesResponse.from(facilities);
        
        log.info("체육시설 조회 완료: {}개", facilities.size());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "위치 기반 무더위쉼터 조회", 
        description = "사용자 위치 주변의 무더위쉼터 정보만 조회합니다."
    )
    @GetMapping("/cooling-centers")
    public ResponseEntity<LocationDataResponse.CoolingCentersResponse> getNearbyCoolingCenters(
            @Parameter(description = "위도", example = "37.5665", required = true)
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            
            @Parameter(description = "경도", example = "126.9780", required = true)
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            
            @Parameter(description = "검색 반경 (km)", example = "2.0")
            @RequestParam(defaultValue = "2.0") @Positive Double radius) {
        
        log.info("위치 기반 무더위쉼터 조회 요청: lat={}, lng={}, radius={}km", latitude, longitude, radius);

        var centers = locationBasedDataService.findNearbyCoolingCenters(latitude, longitude, radius);
        var response = LocationDataResponse.CoolingCentersResponse.from(centers);
        
        log.info("무더위쉼터 조회 완료: {}개", centers.size());
        return ResponseEntity.ok(response);
    }
}
