package com.seoulfit.backend.search.adapter.in.web;

import com.seoulfit.backend.search.adapter.in.web.dto.SearchRequest;
import com.seoulfit.backend.search.adapter.in.web.dto.SearchResultResponse;
import com.seoulfit.backend.search.application.port.in.SearchUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "POI 검색", description = "공공데이터 통합 검색 API")
@RestController
@RequestMapping("/api/search")
public class SearchController {
    
    private final SearchUseCase searchUseCase;
    
    public SearchController(SearchUseCase searchUseCase) {
        this.searchUseCase = searchUseCase;
    }
    
    @Operation(summary = "POI 인덱스 검색", 
               description = "키워드를 이용하여 POI 인덱스 테이블을 검색합니다. 키워드가 없으면 전체 조회합니다.")
    @GetMapping("/index")
    public ResponseEntity<SearchResultResponse> searchIndex(
            @Parameter(description = "검색 키워드") @RequestParam(required = false) String keyword,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        
        SearchResultResponse result = searchUseCase.searchIndex(keyword, page, size);
        return ResponseEntity.ok(result);
    }
    
    @Operation(summary = "인덱스 기반 공공데이터 조회", 
               description = "인덱스 ID를 이용하여 실제 공공데이터를 조회합니다.")
    @GetMapping("/data/{indexId}")
    public ResponseEntity<Object> getPublicDataByIndex(
            @Parameter(description = "POI 인덱스 ID") @PathVariable Long indexId) {
        
        Object publicData = searchUseCase.getPublicDataByIndex(indexId);
        
        if (publicData == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(publicData);
    }
}