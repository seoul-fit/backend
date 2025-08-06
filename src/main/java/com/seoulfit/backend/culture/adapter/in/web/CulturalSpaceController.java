package com.seoulfit.backend.culture.adapter.in.web;

import com.seoulfit.backend.tmp.application.service.impl.CulturalSpaceServiceImpl;
import com.seoulfit.backend.culture.adapter.in.web.dto.response.SeoulCulturalSpaceApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "서울시 문화공간 정보 관련 API", description = "CulturalSpaceController.class")
@RestController
@RequestMapping("/api/v1/cultural-space")
@RequiredArgsConstructor
@Slf4j
public class CulturalSpaceController {
    
    private final CulturalSpaceServiceImpl culturalSpaceService;

    @Operation(summary = "문화 공간 정보 관련 API", description = "문화 공간 정보를 가져온다.")
    @GetMapping("/fetch")
    public ResponseEntity<Map<String, Object>> fetchCulturalSpaces(
            @RequestParam(defaultValue = "1") int startIndex,
            @RequestParam(defaultValue = "1000") int endIndex) {
        try {
            log.info("Fetching cultural spaces from {} to {}", startIndex, endIndex);
            
            SeoulCulturalSpaceApiResponse response = culturalSpaceService.saveCultureSpace(startIndex, endIndex);
            
            if (response == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "API 응답이 null입니다.",
                        "data", null
                ));
            }
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "문화 공간 정보 관련 데이터 가져오기 완료",
                    "totalCount", response.getListTotalCount(),
                    "resultCode", response.getResult() != null ? response.getResult().getCode() : "UNKNOWN",
                    "resultMessage", response.getResult() != null ? response.getResult().getMessage() : "No result info",
                    "dataCount", response.getRow() != null ? response.getRow().size() : 0,
                    "data", response
            ));
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
