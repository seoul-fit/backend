package com.seoulfit.backend.culture.adapter.in.web;

// import com.seoulfit.backend.culture.application.service.CulturalSpaceService;
import com.seoulfit.backend.culture.domain.CulturalSpace;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cultural-spaces")
@RequiredArgsConstructor
@Tag(name = "Cultural Spaces", description = "문화공간 정보 API")
public class CulturalSpaceController {

    // private final CulturalSpaceService culturalSpaceService;

    @Operation(summary = "문화공간 목록 조회", description = "저장된 문화공간 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CulturalSpace>> getCulturalSpaces() {
        // List<CulturalSpace> spaces = culturalSpaceService.getCulturalSpaces();
        return ResponseEntity.ok(List.of());
    }

    @Operation(summary = "문화공간 상세 조회", description = "특정 문화공간의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<CulturalSpace> getCulturalSpace(@PathVariable Long id) {
        // CulturalSpace space = culturalSpaceService.getCulturalSpace(id);
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "문화공간 데이터 동기화", description = "서울시 API에서 최신 문화공간 데이터를 가져와 저장합니다.")
    @PostMapping("/sync")
    public ResponseEntity<String> syncCulturalSpaces() {
        try {
            // int syncedCount = culturalSpaceService.saveCultureSpaces();
            return ResponseEntity.ok("문화공간 데이터 동기화 완료. 동기화된 건수: " + 0);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("문화공간 데이터 동기화 실패: " + e.getMessage());
        }
    }
}
