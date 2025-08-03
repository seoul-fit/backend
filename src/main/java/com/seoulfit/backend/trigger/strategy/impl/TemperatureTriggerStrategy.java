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
 * 온도 기반 트리거 전략
 * 
 * 기온이 설정된 임계값을 초과하거나 미만일 때 알림을 발송하는 전략
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class TemperatureTriggerStrategy implements TriggerStrategy {
    
    @Value("${urbanping.trigger.temperature.high-threshold:35.0}")
    private double highTemperatureThreshold;
    
    @Value("${urbanping.trigger.temperature.low-threshold:0.0}")
    private double lowTemperatureThreshold;
    
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
     * @param context 트리거 컨텍스트
     * @return 현재 온도 (°C)
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
    
    @Override
    public String getSupportedTriggerType() {
        return "TEMPERATURE";
    }
    
    @Override
    public int getPriority() {
        return 10; // 높은 우선순위
    }
    
    @Override
    public String getDescription() {
        return "기온이 설정된 임계값을 초과하거나 미만일 때 알림을 발송합니다.";
    }
}
