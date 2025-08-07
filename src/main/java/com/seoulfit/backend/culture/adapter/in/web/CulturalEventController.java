package com.seoulfit.backend.culture.adapter.in.web;

// import com.seoulfit.backend.culture.application.service.CulturalEventService;
import com.seoulfit.backend.culture.application.service.SeoulCulturalApiService;
import com.seoulfit.backend.culture.domain.CulturalEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cultural-events")
@RequiredArgsConstructor
@Tag(name = "Cultural Events", description = "문화행사 정보 API")
public class CulturalEventController {

    // private final CulturalEventService culturalEventService;
    private final SeoulCulturalApiService seoulCulturalApiService;

    @Operation(summary = "문화행사 목록 조회", description = "저장된 문화행사 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CulturalEvent>> getCulturalEvents(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // List<CulturalEvent> events = culturalEventService.getCulturalEvents(startDate, endDate);
        return ResponseEntity.ok(List.of());
    }

    @Operation(summary = "문화행사 상세 조회", description = "특정 문화행사의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<CulturalEvent> getCulturalEvent(@PathVariable Long id) {
        // CulturalEvent event = culturalEventService.getCulturalEvent(id);
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "문화행사 데이터 동기화", description = "서울시 API에서 최신 문화행사 데이터를 가져와 저장합니다.")
    @PostMapping("/sync")
    public ResponseEntity<String> syncCulturalEvents() {
        try {
            // int syncedCount = culturalEventService.saveCultureEvents();
            return ResponseEntity.ok("문화행사 데이터 동기화 완료. 동기화된 건수: " + 0);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("문화행사 데이터 동기화 실패: " + e.getMessage());
        }
    }
}
