package com.seoulfit.backend.trigger.adapter.in.web;

import com.seoulfit.backend.trigger.adapter.in.web.dto.LocationTriggerRequest;
import com.seoulfit.backend.trigger.adapter.in.web.dto.TriggerEvaluationResponse;
import com.seoulfit.backend.trigger.adapter.in.web.dto.TriggerStrategyInfoResponse;
import com.seoulfit.backend.trigger.application.port.in.EvaluateTriggerUseCase;
import com.seoulfit.backend.trigger.application.port.in.dto.LocationTriggerCommand;
import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 트리거 컨트롤러
 * <p>
 * 실시간 트리거 평가 및 관리를 위한 REST API 제공
 * 사용자 위치 기반 트리거와 수동 트리거 평가 지원
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/triggers")
@RequiredArgsConstructor
@Tag(name = "Trigger", description = "트리거 평가 및 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class TriggerController {

    private final EvaluateTriggerUseCase evaluateTriggerUseCase;

    @Operation(
            summary = "위치 기반 트리거 평가",
            description = "사용자의 현재 위치를 기반으로 모든 트리거 조건을 평가하고 알림을 발송합니다."
    )
    @PostMapping("/evaluate/location")
    public ResponseEntity<TriggerEvaluationResponse> evaluateLocationBasedTriggers(
            @Valid @RequestBody LocationTriggerRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("위치 기반 트리거 평가 요청: userId={}, lat={}, lng={}",
                userDetails.getUsername(), request.getLatitude(), request.getLongitude());

        LocationTriggerCommand command = LocationTriggerCommand.of(
                userDetails.getUsername(),
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius(),
                request.getTriggerTypes()
        );

        TriggerEvaluationResult result = evaluateTriggerUseCase.evaluateLocationBasedTriggers(command);
        return ResponseEntity.ok(TriggerEvaluationResponse.from(result));
    }

    @Operation(
            summary = "특정 타입 트리거 평가",
            description = "특정 타입의 트리거만 평가합니다."
    )
    @PostMapping("/evaluate/{triggerType}")
    public ResponseEntity<TriggerEvaluationResponse> evaluateSpecificTrigger(
            @Parameter(description = "트리거 타입") @PathVariable String triggerType,
            @Valid @RequestBody LocationTriggerRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("특정 타입 트리거 평가 요청: userId={}, type={}",
                userDetails.getUsername(), triggerType);

        LocationTriggerCommand command = LocationTriggerCommand.of(
                userDetails.getUsername(),
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius(),
                List.of(triggerType)
        );

        TriggerEvaluationResult result = evaluateTriggerUseCase.evaluateSpecificTrigger(command, triggerType);
        return ResponseEntity.ok(TriggerEvaluationResponse.from(result));
    }

    @Operation(
            summary = "모든 트리거 전략 정보 조회",
            description = "시스템에 등록된 모든 트리거 전략의 정보를 조회합니다."
    )
    @GetMapping("/strategies")
    public ResponseEntity<List<TriggerStrategyInfoResponse>> getAllTriggerStrategies() {
        List<TriggerStrategyInfoResponse> strategies = evaluateTriggerUseCase.getAllTriggerStrategies();
        return ResponseEntity.ok(strategies);
    }

    @Operation(
            summary = "트리거 전략 활성화/비활성화",
            description = "특정 트리거 전략을 활성화하거나 비활성화합니다."
    )
    @PutMapping("/strategies/{triggerType}/toggle")
    public ResponseEntity<Void> toggleTriggerStrategy(
            @Parameter(description = "트리거 타입") @PathVariable String triggerType,
            @Parameter(description = "활성화 여부") @RequestParam boolean enabled) {

        log.info("트리거 전략 토글 요청: type={}, enabled={}", triggerType, enabled);
        evaluateTriggerUseCase.toggleTriggerStrategy(triggerType, enabled);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "사용자 트리거 히스토리 조회",
            description = "사용자의 트리거 발동 히스토리를 조회합니다."
    )
    @GetMapping("/history")
    public ResponseEntity<List<TriggerEvaluationResponse>> getTriggerHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {

        List<TriggerEvaluationResponse> history = evaluateTriggerUseCase.getTriggerHistory(
                userDetails.getUsername(), page, size);
        return ResponseEntity.ok(history);
    }
}
