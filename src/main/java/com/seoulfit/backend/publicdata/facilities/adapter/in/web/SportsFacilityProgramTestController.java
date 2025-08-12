package com.seoulfit.backend.publicdata.facilities.adapter.in.web;

import com.seoulfit.backend.publicdata.facilities.application.port.out.SportsFacilityProgramApiClient;
import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.api.dto.SportsFacilityProgramApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 서울시 공공체육시설 프로그램 정보 API 테스트 Controller
 */
@Slf4j
//@RestController
@RequestMapping("/api/sports-programs/test")
@RequiredArgsConstructor
@Tag(name = "Sports Facility Programs Test", description = "서울시 공공체육시설 프로그램 정보 API 테스트")
public class SportsFacilityProgramTestController {

    private final SportsFacilityProgramApiClient apiClient;

    @Operation(summary = "API 연결 테스트", description = "서울시 공공체육시설 프로그램 정보 API 연결을 테스트합니다.")
    @GetMapping("/api-connection")
    public ResponseEntity<?> testApiConnection() {
        log.info("API 연결 테스트 시작");
        
        try {
            // 첫 5개 데이터만 가져와서 테스트
            SportsFacilityProgramApiResponse response = apiClient.fetchProgramInfo(1, 5, null);
            
            if (response.isSuccess()) {
                log.info("API 호출 성공 - 총 데이터 수: {}, 조회된 데이터 수: {}", 
                    response.getTotalCount(), response.getProgramInfoList().size());
                
                return ResponseEntity.ok().body(new ApiTestResult(
                    true,
                    "API 호출 성공",
                    response.getTotalCount(),
                    response.getProgramInfoList().size(),
                    response.getProgramInfoList()
                ));
            } else {
                String errorMessage = response.getListProgramByPublicSportsFacilitiesService() != null &&
                                    response.getListProgramByPublicSportsFacilitiesService().getResult() != null ?
                                    response.getListProgramByPublicSportsFacilitiesService().getResult().getMessage() :
                                    "Unknown error";
                
                log.error("API 호출 실패: {}", errorMessage);
                return ResponseEntity.ok().body(new ApiTestResult(
                    false,
                    "API 호출 실패: " + errorMessage,
                    0,
                    0,
                    null
                ));
            }
            
        } catch (Exception e) {
            log.error("API 호출 중 예외 발생", e);
            return ResponseEntity.ok().body(new ApiTestResult(
                false,
                "API 호출 중 예외 발생: " + e.getMessage(),
                0,
                0,
                null
            ));
        }
    }

    @Operation(summary = "특정 종목 API 테스트", description = "특정 종목의 프로그램 정보 API를 테스트합니다.")
    @GetMapping("/api-connection/subject/{subjectName}")
    public ResponseEntity<?> testApiConnectionBySubject(@PathVariable String subjectName) {
        log.info("특정 종목 API 연결 테스트 시작 - 종목: {}", subjectName);
        
        try {
            // 특정 종목의 첫 5개 데이터만 가져와서 테스트
            SportsFacilityProgramApiResponse response = apiClient.fetchProgramInfo(1, 5, subjectName);
            
            if (response.isSuccess()) {
                log.info("특정 종목 API 호출 성공 - 종목: {}, 총 데이터 수: {}, 조회된 데이터 수: {}", 
                    subjectName, response.getTotalCount(), response.getProgramInfoList().size());
                
                return ResponseEntity.ok().body(new ApiTestResult(
                    true,
                    "특정 종목 API 호출 성공 - 종목: " + subjectName,
                    response.getTotalCount(),
                    response.getProgramInfoList().size(),
                    response.getProgramInfoList()
                ));
            } else {
                String errorMessage = response.getListProgramByPublicSportsFacilitiesService() != null &&
                                    response.getListProgramByPublicSportsFacilitiesService().getResult() != null ?
                                    response.getListProgramByPublicSportsFacilitiesService().getResult().getMessage() :
                                    "Unknown error";
                
                log.error("특정 종목 API 호출 실패: {}", errorMessage);
                return ResponseEntity.ok().body(new ApiTestResult(
                    false,
                    "특정 종목 API 호출 실패: " + errorMessage,
                    0,
                    0,
                    null
                ));
            }
            
        } catch (Exception e) {
            log.error("특정 종목 API 호출 중 예외 발생", e);
            return ResponseEntity.ok().body(new ApiTestResult(
                false,
                "특정 종목 API 호출 중 예외 발생: " + e.getMessage(),
                0,
                0,
                null
            ));
        }
    }

    /**
     * API 테스트 결과 DTO
     */
    public record ApiTestResult(
        boolean success,
        String message,
        int totalCount,
        int fetchedCount,
        Object data
    ) {}
}
