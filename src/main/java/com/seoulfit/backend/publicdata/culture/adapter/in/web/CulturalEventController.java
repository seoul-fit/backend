package com.seoulfit.backend.publicdata.culture.adapter.in.web;

import com.seoulfit.backend.publicdata.culture.application.port.in.QueryCulturalEventsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "서울시 문화행사 정보 관련 API", description = "CulturalEventController.class")
@RestController
@RequestMapping("/api/v1/cultural-events")
@RequiredArgsConstructor
public class CulturalEventController {
    private final QueryCulturalEventsUseCase queryCulturalEventsUseCase;

    @Operation(summary = "문화 생활 정보를 가져온다.", description = "문화 생활 데이터를 DB에 동기화 시킨다.")
    @GetMapping("/fetch")
    public ResponseEntity<?> syncCulturalEvents() {
        try {
            return ResponseEntity.ok(queryCulturalEventsUseCase.getAllCulturalEvents());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
