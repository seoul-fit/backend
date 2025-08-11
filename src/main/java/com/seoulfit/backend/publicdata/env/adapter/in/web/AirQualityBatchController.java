package com.seoulfit.backend.publicdata.env.adapter.in.web;

import com.seoulfit.backend.publicdata.env.application.port.in.AirQualityBatchUseCase;
import com.seoulfit.backend.publicdata.env.infrastructure.batch.AirQualityDailyBatch;
import com.seoulfit.backend.publicdata.env.infrastructure.batch.AirQualityRealTimeBatch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 대기질 정보 배치 관리 컨트롤러
 * 관리자용 배치 작업 수동 실행 API
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/env/air-quality/batch")
@RequiredArgsConstructor
@Tag(name = "대기질 배치 관리", description = "대기질 정보 배치 작업 관리 API (관리자용)")
public class AirQualityBatchController {

    private final AirQualityBatchUseCase airQualityBatchUseCase;
    private final AirQualityRealTimeBatch realTimeBatch;
    private final AirQualityDailyBatch dailyBatch;

    @Operation(summary = "실시간 배치 수동 실행", description = "대기질 정보 실시간 배치를 수동으로 실행합니다.")
    @PostMapping("/realtime")
    public ResponseEntity<AirQualityBatchUseCase.AirQualityBatchResult> executeRealTimeBatch() {
        log.info("실시간 배치 수동 실행 요청");
        
        AirQualityBatchUseCase.AirQualityBatchResult result = 
            realTimeBatch.executeManualRealTimeBatch();
        
        log.info("실시간 배치 수동 실행 완료 - 성공: {}", result.success());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 시간대 배치 실행", description = "특정 시간대의 대기질 정보 배치를 실행합니다.")
    @PostMapping("/time-range")
    public ResponseEntity<AirQualityBatchUseCase.AirQualityBatchResult> executeTimeBatch(
            @Parameter(description = "시작 시간", example = "2024-08-10T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "종료 시간", example = "2024-08-10T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("특정 시간대 배치 실행 요청 - 시작: {}, 종료: {}", startTime, endTime);
        
        AirQualityBatchUseCase.AirQualityBatchResult result = 
            realTimeBatch.executeTimeBatch(startTime, endTime);
        
        log.info("특정 시간대 배치 실행 완료 - 성공: {}", result.success());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "배치 상태 확인", description = "현재 배치 작업 상태를 확인합니다.")
    @GetMapping("/status")
    public ResponseEntity<BatchStatus> getBatchStatus() {
        log.info("배치 상태 확인 요청");
        
        // 간단한 상태 정보 반환
        BatchStatus status = new BatchStatus(
            true,  // 실시간 배치 활성화
            true,  // 일일 배치 활성화
            "0 */5 * * * ?",  // 실시간 배치 스케줄
            "0 0 3 * * ?",    // 일일 배치 스케줄
            LocalDateTime.now()
        );
        
        return ResponseEntity.ok(status);
    }

    /**
     * 배치 상태 정보
     */
    public record BatchStatus(
        @Parameter(description = "실시간 배치 활성화 여부")
        boolean realTimeBatchEnabled,
        
        @Parameter(description = "일일 배치 활성화 여부")
        boolean dailyBatchEnabled,
        
        @Parameter(description = "실시간 배치 스케줄")
        String realTimeBatchSchedule,
        
        @Parameter(description = "일일 배치 스케줄")
        String dailyBatchSchedule,
        
        @Parameter(description = "상태 확인 시간")
        LocalDateTime statusCheckTime
    ) {}
}
