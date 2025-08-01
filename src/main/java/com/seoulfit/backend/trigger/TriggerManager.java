package com.seoulfit.backend.trigger;

import com.seoulfit.backend.trigger.dto.TriggerContext;
import com.seoulfit.backend.trigger.dto.TriggerResult;
import com.seoulfit.backend.trigger.strategy.TriggerStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 트리거 매니저
 * 
 * 등록된 모든 트리거 전략을 관리하고 실행하는 중앙 관리자
 * 오픈소스 확장성을 위해 Strategy Pattern과 Factory Pattern을 조합
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TriggerManager {
    
    private final List<TriggerStrategy> triggerStrategies;
    
    /**
     * 주어진 컨텍스트에 대해 모든 트리거 전략을 평가합니다.
     * 
     * @param context 트리거 평가 컨텍스트
     * @return 가장 높은 우선순위의 트리거 결과 (발동된 경우)
     */
    public Optional<TriggerResult> evaluateAll(TriggerContext context) {
        log.debug("트리거 평가 시작: userId={}, strategiesCount={}", 
                context.getUser().getId(), triggerStrategies.size());
        
        return triggerStrategies.stream()
                .filter(TriggerStrategy::isEnabled)
                .sorted((s1, s2) -> Integer.compare(s1.getPriority(), s2.getPriority()))
                .peek(strategy -> log.debug("트리거 전략 실행: type={}, priority={}", 
                        strategy.getSupportedTriggerType(), strategy.getPriority()))
                .map(strategy -> {
                    try {
                        return strategy.evaluate(context);
                    } catch (Exception e) {
                        log.error("트리거 전략 실행 중 오류 발생: type={}, userId={}", 
                                strategy.getSupportedTriggerType(), context.getUser().getId(), e);
                        return TriggerResult.notTriggered();
                    }
                })
                .filter(TriggerResult::isTriggered)
                .findFirst();
    }
    
    /**
     * 특정 타입의 트리거 전략을 평가합니다.
     * 
     * @param context 트리거 평가 컨텍스트
     * @param triggerType 트리거 타입
     * @return 트리거 결과
     */
    public Optional<TriggerResult> evaluateByType(TriggerContext context, String triggerType) {
        log.debug("특정 타입 트리거 평가: userId={}, type={}", 
                context.getUser().getId(), triggerType);
        
        return triggerStrategies.stream()
                .filter(strategy -> strategy.getSupportedTriggerType().equals(triggerType))
                .filter(TriggerStrategy::isEnabled)
                .findFirst()
                .map(strategy -> {
                    try {
                        return strategy.evaluate(context);
                    } catch (Exception e) {
                        log.error("트리거 전략 실행 중 오류 발생: type={}, userId={}", 
                                triggerType, context.getUser().getId(), e);
                        return TriggerResult.notTriggered();
                    }
                });
    }
    
    /**
     * 등록된 모든 트리거 전략의 정보를 반환합니다.
     * 
     * @return 트리거 전략 정보 목록
     */
    public List<TriggerStrategyInfo> getAllStrategiesInfo() {
        return triggerStrategies.stream()
                .map(strategy -> TriggerStrategyInfo.builder()
                        .type(strategy.getSupportedTriggerType())
                        .description(strategy.getDescription())
                        .priority(strategy.getPriority())
                        .enabled(strategy.isEnabled())
                        .className(strategy.getClass().getSimpleName())
                        .build())
                .toList();
    }
    
    /**
     * 활성화된 트리거 전략의 개수를 반환합니다.
     * 
     * @return 활성화된 전략 개수
     */
    public long getEnabledStrategiesCount() {
        return triggerStrategies.stream()
                .filter(TriggerStrategy::isEnabled)
                .count();
    }
    
    /**
     * 트리거 전략 정보를 담는 클래스
     */
    @lombok.Builder
    @lombok.Getter
    public static class TriggerStrategyInfo {
        private final String type;
        private final String description;
        private final int priority;
        private final boolean enabled;
        private final String className;
    }
}
