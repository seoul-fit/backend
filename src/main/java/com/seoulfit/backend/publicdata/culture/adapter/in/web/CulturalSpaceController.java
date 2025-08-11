package com.seoulfit.backend.publicdata.culture.adapter.in.web;

import com.seoulfit.backend.publicdata.culture.application.port.in.QueryCulturalSpaceUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "서울시 문화공간 정보 관련 API", description = "CulturalSpaceController.class")
@RestController
@RequestMapping("/api/v1/cultural-space")
@RequiredArgsConstructor
@Slf4j
public class CulturalSpaceController {
    private final QueryCulturalSpaceUseCase queryCulturalSpaceUseCase;

    @Operation(summary = "문화 공간 정보 관련 API", description = "문화 공간 정보를 가져온다.")
    @GetMapping("/fetch")
    public ResponseEntity<?> fetchCulturalSpaces() {
        try {
            return ResponseEntity.ok(queryCulturalSpaceUseCase.getAllCulturalSpace());
        } catch (Exception e) {
            log.error("Error fetching cultural spaces", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "문화공간 정보 데이터 가져오기에 실패했습니다.",
                    "error", e.getMessage(),
                    "errorType", e.getClass().getSimpleName()
            ));
        }
    }

}
