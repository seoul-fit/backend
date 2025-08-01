package com.seoulfit.backend.trigger.application.port.in;

import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationCommand;
import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationResult;

import java.util.List;

/**
 * 트리거 관리 Use Case
 * <p>
 * 헥사고날 아키텍처의 입력 포트
 * 트리거 평가 및 알림 발송과 관련된 비즈니스 로직을 정의
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
public interface ManageTriggerUseCase {

    /**
     * 모든 트리거 평가 실행
     *
     * @return 트리거 평가 결과 목록
     */
    List<TriggerEvaluationResult> evaluateAllTriggers();

    /**
     * 특정 사용자에 대한 트리거 평가 실행
     *
     * @param command 트리거 평가 명령
     * @return 트리거 평가 결과 목록
     */
    List<TriggerEvaluationResult> evaluateTriggersForUser(TriggerEvaluationCommand command);

    /**
     * 실시간 트리거 평가 실행
     *
     * @return 트리거 평가 결과 목록
     */
    List<TriggerEvaluationResult> evaluateRealtimeTriggers();

    /**
     * 문화행사 트리거 평가 실행
     *
     * @return 트리거 평가 결과 목록
     */
    List<TriggerEvaluationResult> evaluateCulturalEventTriggers();
}
