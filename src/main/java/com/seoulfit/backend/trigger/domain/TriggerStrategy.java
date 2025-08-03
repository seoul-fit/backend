package com.seoulfit.backend.trigger.domain;

import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationCommand;
import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationResult;

/**
 * 트리거 전략 인터페이스
 * 다양한 트리거 조건을 평가하는 전략 패턴을 구현합니다.
 */
public interface TriggerStrategy {
    
    /**
     * 트리거 조건을 평가합니다.
     * 
     * @param command 트리거 평가 명령
     * @return 트리거 평가 결과
     */
    TriggerEvaluationResult evaluate(TriggerEvaluationCommand command);
    
    /**
     * 이 전략이 지원하는 트리거 타입을 반환합니다.
     * 
     * @return 트리거 타입
     */
    String getSupportedTriggerType();
    
    /**
     * 트리거 조건이 만족되었는지 확인합니다.
     * 
     * @param command 트리거 평가 명령
     * @return 조건 만족 여부
     */
    boolean isConditionMet(TriggerEvaluationCommand command);
}
