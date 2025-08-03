package com.seoulfit.backend.trigger.strategy;

import com.seoulfit.backend.trigger.dto.TriggerContext;
import com.seoulfit.backend.trigger.dto.TriggerResult;

/**
 * 트리거 전략 인터페이스
 * 
 * 오픈소스 확장성을 위한 Strategy Pattern 구현
 * 새로운 트리거 조건을 쉽게 추가할 수 있도록 설계
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
public interface TriggerStrategy {
    
    /**
     * 트리거 조건을 평가합니다.
     * 
     * @param context 트리거 평가에 필요한 컨텍스트 정보
     * @return 트리거 평가 결과
     */
    TriggerResult evaluate(TriggerContext context);
    
    /**
     * 이 전략이 지원하는 트리거 타입을 반환합니다.
     * 
     * @return 지원하는 트리거 타입
     */
    String getSupportedTriggerType();
    
    /**
     * 트리거 전략의 우선순위를 반환합니다.
     * 낮은 값일수록 높은 우선순위를 가집니다.
     * 
     * @return 우선순위 (기본값: 100)
     */
    default int getPriority() {
        return 100;
    }
    
    /**
     * 트리거 전략의 설명을 반환합니다.
     * 
     * @return 전략 설명
     */
    String getDescription();
    
    /**
     * 이 전략이 활성화되어 있는지 확인합니다.
     * 
     * @return 활성화 여부 (기본값: true)
     */
    default boolean isEnabled() {
        return true;
    }
}
