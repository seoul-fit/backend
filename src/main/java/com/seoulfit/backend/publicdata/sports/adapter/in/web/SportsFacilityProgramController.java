package com.seoulfit.backend.publicdata.sports.adapter.in.web;

import com.seoulfit.backend.publicdata.sports.application.port.in.SportsFacilityProgramBatchUseCase;
import com.seoulfit.backend.publicdata.sports.application.port.in.SportsFacilityProgramQueryUseCase;
import com.seoulfit.backend.publicdata.sports.domain.SportsFacilityProgram;
import com.seoulfit.backend.publicdata.sports.infrastructure.batch.SportsFacilityProgramDailyBatch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 서울시 공공체육시설 프로그램 정보 REST Controller
 * 헥사고날 아키텍처의 입력 어댑터
 */
@Slf4j
@RestController
@RequestMapping("/api/sports-programs")
@RequiredArgsConstructor
@Tag(name = "Sports Facility Programs", description = "서울시 공공체육시설 프로그램 정보 API")
public class SportsFacilityProgramController {

    private final SportsFacilityProgramQueryUseCase queryUseCase;
    private final SportsFacilityProgramDailyBatch dailyBatch;

    @Operation(summary = "최신 프로그램 정보 조회", description = "가장 최근에 수집된 서울시 공공체육시설 프로그램 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<SportsFacilityProgram>> getLatestPrograms() {
        log.info("최신 프로그램 정보 조회 요청");
        List<SportsFacilityProgram> programs = queryUseCase.findLatestPrograms();
        return ResponseEntity.ok(programs);
    }

    @Operation(summary = "특정 날짜 프로그램 정보 조회", description = "지정된 날짜의 서울시 공공체육시설 프로그램 정보를 조회합니다.")
    @GetMapping("/date/{dataDate}")
    public ResponseEntity<List<SportsFacilityProgram>> getProgramsByDate(
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식)", example = "20250807")
            @PathVariable String dataDate) {
        log.info("특정 날짜 프로그램 정보 조회 요청 - 날짜: {}", dataDate);
        List<SportsFacilityProgram> programs = queryUseCase.findProgramsByDate(dataDate);
        return ResponseEntity.ok(programs);
    }

    @Operation(summary = "시설별 프로그램 정보 조회", description = "특정 시설의 프로그램 정보를 조회합니다.")
    @GetMapping("/center/{centerName}")
    public ResponseEntity<List<SportsFacilityProgram>> getProgramsByCenter(
            @Parameter(description = "시설명", example = "강남구민체육센터")
            @PathVariable String centerName,
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식, 생략시 최신 데이터)", example = "20250807")
            @RequestParam(required = false) String dataDate) {
        log.info("시설별 프로그램 정보 조회 요청 - 시설: {}, 날짜: {}", centerName, dataDate);
        List<SportsFacilityProgram> programs = queryUseCase.findProgramsByCenter(centerName, dataDate);
        return ResponseEntity.ok(programs);
    }

    @Operation(summary = "종목별 프로그램 정보 조회", description = "특정 종목의 프로그램 정보를 조회합니다.")
    @GetMapping("/subject/{subjectName}")
    public ResponseEntity<List<SportsFacilityProgram>> getProgramsBySubject(
            @Parameter(description = "종목명", example = "수영")
            @PathVariable String subjectName,
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식, 생략시 최신 데이터)", example = "20250807")
            @RequestParam(required = false) String dataDate) {
        log.info("종목별 프로그램 정보 조회 요청 - 종목: {}, 날짜: {}", subjectName, dataDate);
        List<SportsFacilityProgram> programs = queryUseCase.findProgramsBySubject(subjectName, dataDate);
        return ResponseEntity.ok(programs);
    }

    @Operation(summary = "프로그램명 검색", description = "프로그램명으로 프로그램 정보를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<SportsFacilityProgram>> searchProgramsByName(
            @Parameter(description = "검색할 프로그램명", example = "요가")
            @RequestParam String name,
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식, 생략시 최신 데이터)", example = "20250807")
            @RequestParam(required = false) String dataDate) {
        log.info("프로그램명 검색 요청 - 검색어: {}, 날짜: {}", name, dataDate);
        List<SportsFacilityProgram> programs = queryUseCase.searchProgramsByName(name, dataDate);
        return ResponseEntity.ok(programs);
    }

    @Operation(summary = "프로그램 상세 정보 조회", description = "프로그램 ID로 특정 프로그램의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<SportsFacilityProgram> getProgramById(
            @Parameter(description = "프로그램 ID", example = "1")
            @PathVariable Long id) {
        log.info("프로그램 상세 정보 조회 요청 - ID: {}", id);
        Optional<SportsFacilityProgram> program = queryUseCase.findProgramById(id);
        
        return program.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "사용 중인 프로그램 조회", description = "현재 사용 중인 프로그램 목록을 조회합니다.")
    @GetMapping("/active")
    public ResponseEntity<List<SportsFacilityProgram>> getActivePrograms(
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식, 생략시 최신 데이터)", example = "20250807")
            @RequestParam(required = false) String dataDate) {
        log.info("사용 중인 프로그램 조회 요청 - 날짜: {}", dataDate);
        List<SportsFacilityProgram> programs = queryUseCase.findActivePrograms(dataDate);
        return ResponseEntity.ok(programs);
    }

    @Operation(summary = "무료 프로그램 조회", description = "무료로 이용 가능한 프로그램 목록을 조회합니다.")
    @GetMapping("/free")
    public ResponseEntity<List<SportsFacilityProgram>> getFreePrograms(
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식, 생략시 최신 데이터)", example = "20250807")
            @RequestParam(required = false) String dataDate) {
        log.info("무료 프로그램 조회 요청 - 날짜: {}", dataDate);
        List<SportsFacilityProgram> programs = queryUseCase.findFreePrograms(dataDate);
        return ResponseEntity.ok(programs);
    }

    @Operation(summary = "사용 가능한 데이터 날짜 목록", description = "조회 가능한 데이터 날짜 목록을 반환합니다.")
    @GetMapping("/available-dates")
    public ResponseEntity<List<String>> getAvailableDataDates() {
        log.info("사용 가능한 데이터 날짜 목록 조회 요청");
        List<String> dates = queryUseCase.getAvailableDataDates();
        return ResponseEntity.ok(dates);
    }

    @Operation(summary = "프로그램 데이터 통계", description = "특정 날짜의 프로그램 데이터 통계를 조회합니다.")
    @GetMapping("/statistics")
    public ResponseEntity<SportsFacilityProgramQueryUseCase.ProgramDataStatistics> getProgramStatistics(
            @Parameter(description = "데이터 날짜 (YYYYMMDD 형식, 생략시 최신 데이터)", example = "20250807")
            @RequestParam(required = false) String dataDate) {
        log.info("프로그램 데이터 통계 조회 요청 - 날짜: {}", dataDate);
        SportsFacilityProgramQueryUseCase.ProgramDataStatistics statistics = queryUseCase.getProgramStatistics(dataDate);
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "수동 배치 실행", description = "프로그램 정보 수집 배치를 수동으로 실행합니다. (관리자용)")
    @PostMapping("/batch/manual")
    public ResponseEntity<SportsFacilityProgramBatchUseCase.SportsFacilityProgramBatchResult> executeManualBatch(
            @Parameter(description = "배치 실행 날짜 (YYYYMMDD 형식, 생략시 오늘 날짜)", example = "20250807")
            @RequestParam(required = false) String dataDate) {
        log.info("수동 배치 실행 요청 - 날짜: {}", dataDate);
        
        SportsFacilityProgramBatchUseCase.SportsFacilityProgramBatchResult result;
        if (dataDate != null) {
            result = dailyBatch.executeManualBatch(dataDate);
        } else {
            result = dailyBatch.executeManualBatch();
        }
        
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "종목별 수동 배치 실행", description = "특정 종목의 프로그램 정보 수집 배치를 수동으로 실행합니다. (관리자용)")
    @PostMapping("/batch/manual/subject")
    public ResponseEntity<SportsFacilityProgramBatchUseCase.SportsFacilityProgramBatchResult> executeManualBatchBySubject(
            @Parameter(description = "종목명", example = "수영")
            @RequestParam String subjectName,
            @Parameter(description = "배치 실행 날짜 (YYYYMMDD 형식, 생략시 오늘 날짜)", example = "20250807")
            @RequestParam(required = false) String dataDate) {
        log.info("종목별 수동 배치 실행 요청 - 종목: {}, 날짜: {}", subjectName, dataDate);
        
        String targetDate = dataDate != null ? dataDate : 
            java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        SportsFacilityProgramBatchUseCase.SportsFacilityProgramBatchResult result = 
            dailyBatch.executeManualBatchBySubject(targetDate, subjectName);
        
        return ResponseEntity.ok(result);
    }
}
