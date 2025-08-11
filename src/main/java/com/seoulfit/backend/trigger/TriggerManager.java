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
 * 트리거 시스템의 중앙 관리자 클래스입니다.
 * 
 * <p>등록된 모든 트리거 전략을 관리하고 실행하는 역할을 담당합니다.
 * Strategy Pattern과 Factory Pattern을 조합하여 오픈소스 확장성을 제공합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>모든 트리거 전략의 평가 및 실행</li>
 *   <li>특정 타입의 트리거 전략 실행</li>
 *   <li>트리거 전략 우선순위 관리</li>
 *   <li>트리거 전략 정보 조회</li>
 *   <li>예외 처리 및 로깅</li>
 * </ul>
 * 
 * <p>트리거 전략은 우선순위에 따라 정렬되어 실행되며, 
 * 가장 높은 우선순위의 발동된 트리거 결과를 반환합니다.</p>
 * 
 * @author Seoul Fit
 * @version 1.0.0
 * @since 2025-01-01
 * @see TriggerStrategy
 * @see TriggerContext
 * @see TriggerResult
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TriggerManager {
    
    /**
     * 등록된 모든 트리거 전략 목록입니다.
     * Spring의 의존성 주입을 통해 자동으로 수집됩니다.
     */
    private final List<TriggerStrategy> triggerStrategies;
    
    /**
     * 주어진 컨텍스트에 대해 모든 트리거 전략을 평가합니다.
     * 
     * <p>활성화된 트리거 전략들을 우선순위 순으로 정렬하여 실행하고,
     * 가장 먼저 발동된 트리거의 결과를 반환합니다.</p>
     * 
     * @param context 트리거 평가에 필요한 컨텍스트 정보
     * @return 발동된 트리거 결과 (발동된 트리거가 없으면 empty)
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
     * <p>지정된 타입과 일치하는 활성화된 트리거 전략을 찾아 실행합니다.
     * 해당 타입의 전략이 없거나 비활성화된 경우 empty를 반환합니다.</p>
     * 
     * @param context 트리거 평가에 필요한 컨텍스트 정보
     * @param triggerType 실행할 트리거 타입
     * @return 트리거 실행 결과 (해당 타입의 전략이 없으면 empty)
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
     * <p>각 전략의 타입, 설명, 우선순위, 활성화 상태, 클래스명 등의
     * 메타데이터를 포함한 정보 목록을 제공합니다.</p>
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
     * 현재 활성화된 트리거 전략의 개수를 반환합니다.
     * 
     * @return 활성화된 트리거 전략 개수
     */
    public long getEnabledStrategiesCount() {
        return triggerStrategies.stream()
                .filter(TriggerStrategy::isEnabled)
                .count();
    }
    
    /**
     * 트리거 전략의 메타데이터 정보를 담는 내부 클래스입니다.
     * 
     * <p>트리거 전략의 기본 정보와 상태를 조회할 때 사용됩니다.</p>
     * 
     * @author Seoul Fit
     * @since 1.0.0
     */
    @lombok.Builder
    @lombok.Getter
    public static class TriggerStrategyInfo {
        
        /**
         * 트리거 전략의 타입입니다.
         */
        private final String type;
        
        /**
         * 트리거 전략의 설명입니다.
         */
        private final String description;
        
        /**
         * 트리거 전략의 우선순위입니다.
         * 낮은 값일수록 높은 우선순위를 가집니다.
         */
        private final int priority;
        
        /**
         * 트리거 전략의 활성화 상태입니다.
         */
        private final boolean enabled;
        
        /**
         * 트리거 전략 구현 클래스의 이름입니다.
         */
        private final String className;
    }
}
