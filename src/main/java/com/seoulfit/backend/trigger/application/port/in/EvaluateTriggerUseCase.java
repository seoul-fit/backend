package com.seoulfit.backend.trigger.application.port.in;

import com.seoulfit.backend.trigger.adapter.in.web.dto.TriggerStrategyInfoResponse;
import com.seoulfit.backend.trigger.adapter.in.web.dto.TriggerEvaluationResponse;
import com.seoulfit.backend.trigger.application.port.in.dto.LocationTriggerCommand;
import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationResult;

import java.util.List;

/**
 * 트리거 평가 Use Case
 * 
 * 헥사고날 아키텍처의 입력 포트
 * 트리거 평가와 관련된 모든 비즈니스 로직을 정의
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
public interface EvaluateTriggerUseCase {

    /**
     * 위치 기반 트리거 평가
     * 
     * @param command 위치 기반 트리거 명령
     * @return 트리거 평가 결과
     */
    TriggerEvaluationResult evaluateLocationBasedTriggers(LocationTriggerCommand command);


    /**
     * 사용자 트리거 히스토리 조회
     * 
     * @param userId 사용자 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 트리거 히스토리 목록
     */
    List<TriggerEvaluationResponse> getTriggerHistory(String userId, int page, int size);
}
