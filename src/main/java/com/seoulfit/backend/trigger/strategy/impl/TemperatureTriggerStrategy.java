package com.seoulfit.backend.trigger.strategy.impl;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import com.seoulfit.backend.trigger.dto.TriggerContext;
import com.seoulfit.backend.trigger.dto.TriggerResult;
import com.seoulfit.backend.trigger.strategy.TriggerStrategy;
import com.seoulfit.backend.user.domain.InterestCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 온도 기반 트리거 전략 구현 클래스입니다.
 * 
 * <p>기온이 설정된 임계값을 초과하거나 미만일 때 사용자에게 알림을 발송하는 전략입니다.
 * 폭염과 한파 상황에서 사용자의 안전을 위한 주의사항을 제공합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>고온 임계값 초과 시 폭염 주의보 발송</li>
 *   <li>저온 임계값 미만 시 한파 주의보 발송</li>
 *   <li>사용자 관심사 기반 필터링 (날씨 관심사 필요)</li>
 *   <li>설정 가능한 온도 임계값</li>
 * </ul>
 * 
 * <p>임계값 설정:</p>
 * <ul>
 *   <li>고온 임계값: urbanping.trigger.temperature.high-threshold (기본값: 35.0°C)</li>
 *   <li>저온 임계값: urbanping.trigger.temperature.low-threshold (기본값: 0.0°C)</li>
 * </ul>
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
public class TemperatureTriggerStrategy implements TriggerStrategy {
    
    /**
     * 고온 알림을 발송할 온도 임계값입니다.
     * 이 값 이상의 온도에서 폭염 주의보가 발송됩니다.
     */
    @Value("${urbanping.trigger.temperature.high-threshold:35.0}")
    private double highTemperatureThreshold;
    
    /**
     * 저온 알림을 발송할 온도 임계값입니다.
     * 이 값 이하의 온도에서 한파 주의보가 발송됩니다.
     */
    @Value("${urbanping.trigger.temperature.low-threshold:0.0}")
    private double lowTemperatureThreshold;
    
    /**
     * 온도 기반 트리거 조건을 평가합니다.
     * 
     * <p>사용자가 날씨에 관심이 있는지 확인한 후, 현재 온도가 설정된 임계값을
     * 초과하거나 미만인지 검사합니다. 조건에 해당하는 경우 적절한 알림을 생성합니다.</p>
     * 
     * @param context 트리거 평가에 필요한 컨텍스트 정보
     * @return 트리거 평가 결과 (발동 시 알림 정보 포함)
     */
    @Override
    public TriggerResult evaluate(TriggerContext context) {
        log.debug("온도 기반 트리거 평가 시작: userId={}", context.getUser().getId());
        
        // 사용자가 날씨에 관심이 있는지 확인
        if (!context.getUserInterests().contains(InterestCategory.WEATHER)) {
            log.debug("사용자가 날씨에 관심이 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        // 공공 API 데이터에서 온도 정보 추출
        Double currentTemp = extractTemperature(context);
        if (currentTemp == null) {
            log.warn("온도 정보를 찾을 수 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        log.debug("현재 온도: {}°C, 고온 임계값: {}°C, 저온 임계값: {}°C", 
                currentTemp, highTemperatureThreshold, lowTemperatureThreshold);
        
        // 고온 알림 체크
        if (currentTemp >= highTemperatureThreshold) {
            return TriggerResult.highPriorityTriggered(
                    NotificationType.WEATHER,
                    TriggerCondition.TEMPERATURE_HIGH,
                    "폭염 주의보",
                    String.format("현재 기온이 %.1f°C입니다. 외출 시 충분한 수분 섭취와 그늘에서 휴식을 취하세요.", currentTemp),
                    context.getPublicApiData("locationInfo", String.class),
                    10 // 높은 우선순위
            );
        }
        
        // 저온 알림 체크
        if (currentTemp <= lowTemperatureThreshold) {
            return TriggerResult.highPriorityTriggered(
                    NotificationType.WEATHER,
                    TriggerCondition.TEMPERATURE_LOW,
                    "한파 주의보",
                    String.format("현재 기온이 %.1f°C입니다. 외출 시 따뜻한 옷차림을 하시고 체온 유지에 주의하세요.", currentTemp),
                    context.getPublicApiData("locationInfo", String.class),
                    10 // 높은 우선순위
            );
        }
        
        log.debug("온도 기반 트리거 조건 미충족: userId={}, temp={}°C", 
                context.getUser().getId(), currentTemp);
        return TriggerResult.notTriggered();
    }
    
    /**
     * 공공 API 데이터에서 온도 정보를 추출합니다.
     * 
     * <p>서울시 실시간 도시 데이터의 날씨 정보에서 현재 온도를 파싱합니다.
     * 데이터 형식이 다양할 수 있으므로 String과 Number 타입을 모두 처리합니다.</p>
     * 
     * @param context 온도 정보가 포함된 트리거 컨텍스트
     * @return 현재 온도 (°C), 추출 실패 시 null
     */
    private Double extractTemperature(TriggerContext context) {
        // 서울시 실시간 도시 데이터에서 온도 추출
        Object weatherData = context.getPublicApiData().get("WEATHER_STTS");
        if (weatherData instanceof java.util.List) {
            @SuppressWarnings("unchecked")
            java.util.List<java.util.Map<String, Object>> weatherList = 
                    (java.util.List<java.util.Map<String, Object>>) weatherData;
            
            if (!weatherList.isEmpty()) {
                java.util.Map<String, Object> weather = weatherList.get(0);
                Object tempObj = weather.get("TEMP");
                if (tempObj instanceof String) {
                    try {
                        return Double.parseDouble((String) tempObj);
                    } catch (NumberFormatException e) {
                        log.warn("온도 데이터 파싱 실패: {}", tempObj, e);
                    }
                } else if (tempObj instanceof Number) {
                    return ((Number) tempObj).doubleValue();
                }
            }
        }
        
        return null;
    }
    
    /**
     * 이 전략이 지원하는 트리거 타입을 반환합니다.
     * 
     * @return 트리거 타입 문자열 ("TEMPERATURE")
     */
    @Override
    public String getSupportedTriggerType() {
        return "TEMPERATURE";
    }
    
    /**
     * 이 전략의 우선순위를 반환합니다.
     * 
     * <p>온도 관련 알림은 사용자 안전과 직결되므로 높은 우선순위를 가집니다.</p>
     * 
     * @return 우선순위 (10 - 높은 우선순위)
     */
    @Override
    public int getPriority() {
        return 10; // 높은 우선순위
    }
    
    /**
     * 이 전략에 대한 설명을 반환합니다.
     * 
     * @return 전략 설명 문자열
     */
    @Override
    public String getDescription() {
        return "기온이 설정된 임계값을 초과하거나 미만일 때 알림을 발송합니다.";
    }
}
