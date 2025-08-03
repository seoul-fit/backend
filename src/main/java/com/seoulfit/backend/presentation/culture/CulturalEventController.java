package com.seoulfit.backend.presentation.culture;

import com.seoulfit.backend.application.service.CulturalEventService;
import com.seoulfit.backend.application.service.SeoulCulturalApiService;
import com.seoulfit.backend.domain.CulturalEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "서울시 문화행사 정보 관련 API", description = "CulturalEventController.class")
@RestController
@RequestMapping("/api/v1/cultural-events")
@RequiredArgsConstructor
public class CulturalEventController {

    private final CulturalEventService culturalEventService;
    private final SeoulCulturalApiService seoulCulturalApiService;

    @Operation(summary = "문화 생활 정보를 가져온다.", description = "문화 생활 데이터를 DB에 동기화 시킨다.")
    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncCulturalEvents() {
        try {
            int syncedCount = culturalEventService.saveCultureEvents();
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "문화행사 데이터 동기화가 완료되었습니다.",
                    "syncedCount", syncedCount
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "문화행사 데이터 동기화에 실패했습니다.",
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<CulturalEventResponse>> getNearbyEvents(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "2.0") double radius,
            @RequestParam(required = false) List<String> categories) {

        try {
            if (categories == null || categories.isEmpty()) {
                categories = culturalEventService.getAllCategories();
            }

            List<CulturalEvent> events = culturalEventService.findNearbyEvents(lat, lng, radius, categories);
            List<CulturalEventResponse> response = events.stream()
                    .map(this::convertToResponse)
                    .toList();

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 요청 파라미터: " + e.getMessage());
        }
    }

    @GetMapping("/ongoing")
    public ResponseEntity<List<CulturalEventResponse>> getOngoingEvents() {
        List<CulturalEvent> events = culturalEventService.findOngoingEvents();
        List<CulturalEventResponse> response = events.stream()
                .map(this::convertToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/districts/{district}")
    public ResponseEntity<List<CulturalEventResponse>> getEventsByDistrict(@PathVariable String district) {
        try {
            List<CulturalEvent> events = culturalEventService.findOngoingEventsByDistrict(district);
            List<CulturalEventResponse> response = events.stream()
                    .map(this::convertToResponse)
                    .toList();

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 구 이름: " + e.getMessage());
        }
    }

    @GetMapping("/free")
    public ResponseEntity<List<CulturalEventResponse>> getFreeEvents() {
        List<CulturalEvent> events = culturalEventService.findFreeEvents();
        List<CulturalEventResponse> response = events.stream()
                .map(this::convertToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<CulturalEventResponse>> getEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<CulturalEvent> events = culturalEventService.findEventsByDateRange(startDate, endDate);
            List<CulturalEventResponse> response = events.stream()
                    .map(this::convertToResponse)
                    .toList();

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 날짜 범위: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<CulturalEventResponse>> searchEvents(@RequestParam String keyword) {
        try {
            List<CulturalEvent> events = culturalEventService.searchEventsByKeyword(keyword);
            List<CulturalEventResponse> response = events.stream()
                    .map(this::convertToResponse)
                    .toList();

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 검색 키워드: " + e.getMessage());
        }
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<CulturalEventDetailResponse> getEventDetail(@PathVariable Long eventId) {
        try {
            CulturalEvent event = culturalEventService.findById(eventId);
            CulturalEventDetailResponse response = convertToDetailResponse(event);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("문화행사를 찾을 수 없습니다: " + e.getMessage());
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = culturalEventService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/districts")
    public ResponseEntity<List<String>> getDistricts() {
        List<String> districts = culturalEventService.getAllDistricts();
        return ResponseEntity.ok(districts);
    }

    private CulturalEventResponse convertToResponse(CulturalEvent event) {
        return new CulturalEventResponse(
                event.getId(),
                event.getTitle(),
                event.getCodeName(),
                event.getDistrict(),
                event.getPlace(),
                event.getStartDate(),
                event.getEndDate(),
                event.getLatitude(),
                event.getLongitude(),
                event.getIsFree(),
                event.getMainImg(),
                event.getOrgName()
        );
    }

    private CulturalEventDetailResponse convertToDetailResponse(CulturalEvent event) {
        return new CulturalEventDetailResponse(
                event.getId(),
                event.getTitle(),
                event.getCodeName(),
                event.getDistrict(),
                event.getPlace(),
                event.getEventDate(),
                event.getStartDate(),
                event.getEndDate(),
                event.getOrgName(),
                event.getUseTarget(),
                event.getUseFee(),
                event.getPlayer(),
                event.getProgram(),
                event.getEtcDesc(),
                event.getOrgLink(),
                event.getMainImg(),
                event.getTicket(),
                event.getThemeCode(),
                event.getLatitude(),
                event.getLongitude(),
                event.getIsFree(),
                event.getHomepageAddr()
        );
    }

    public record CulturalEventResponse(
            Long id,
            String title,
            String category,
            String district,
            String place,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal latitude,
            BigDecimal longitude,
            String isFree,
            String mainImg,
            String orgName
    ) {}

    public record CulturalEventDetailResponse(
            Long id,
            String title,
            String category,
            String district,
            String place,
            String eventDate,
            LocalDate startDate,
            LocalDate endDate,
            String orgName,
            String useTarget,
            String useFee,
            String player,
            String program,
            String etcDesc,
            String orgLink,
            String mainImg,
            String ticket,
            String themeCode,
            BigDecimal latitude,
            BigDecimal longitude,
            String isFree,
            String homepageAddr
    ) {}
}
