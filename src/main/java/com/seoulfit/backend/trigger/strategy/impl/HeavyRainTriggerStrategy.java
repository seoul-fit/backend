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
 * 폭우 기반 트리거 전략
 * 
 * 강수량이 설정된 임계값을 초과할 때 알림을 발송하는 전략
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class HeavyRainTriggerStrategy implements TriggerStrategy {
    
    @Value("${urbanping.trigger.rain.heavy-threshold:15.0}")
    private double heavyRainThreshold; // mm/h
    
    @Value("${urbanping.trigger.rain.warning-threshold:30.0}")
    private double warningRainThreshold; // mm/h
    
    @Override
    public TriggerResult evaluate(TriggerContext context) {
        log.debug("폭우 기반 트리거 평가 시작: userId={}", context.getUser().getId());
        
        // 사용자가 날씨에 관심이 있는지 확인
        if (!context.getUserInterests().contains(InterestCategory.WEATHER)) {
            log.debug("사용자가 날씨에 관심이 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        // 공공 API 데이터에서 강수량 정보 추출
        RainfallInfo rainfallInfo = extractRainfallInfo(context);
        if (rainfallInfo == null) {
            log.warn("강수량 정보를 찾을 수 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        log.debug("현재 강수량: 시간당 {}mm, 일 누적 {}mm", 
                rainfallInfo.hourlyRainfall, rainfallInfo.dailyRainfall);
        
        // 경보 수준 폭우 체크
        if (rainfallInfo.hourlyRainfall >= warningRainThreshold) {
            return TriggerResult.highPriorityTriggered(
                    NotificationType.WEATHER,
                    TriggerCondition.HEAVY_RAIN,
                    "호우 경보",
                    String.format("현재 시간당 강수량이 %.1fmm입니다. 외출을 자제하고 안전한 곳으로 대피하세요. " +
                            "침수 위험이 있는 지하차도나 저지대는 피해주세요.", rainfallInfo.hourlyRainfall),
                    context.getPublicApiData("locationInfo", String.class),
                    5 // 매우 높은 우선순위
            );
        }
        
        // 주의보 수준 폭우 체크
        if (rainfallInfo.hourlyRainfall >= heavyRainThreshold) {
            return TriggerResult.triggered(
                    NotificationType.WEATHER,
                    TriggerCondition.HEAVY_RAIN,
                    "호우 주의보",
                    String.format("현재 시간당 강수량이 %.1fmm입니다. 우산을 준비하시고 " +
                            "물이 고이기 쉬운 곳은 주의하세요.", rainfallInfo.hourlyRainfall),
                    context.getPublicApiData("locationInfo", String.class)
            );
        }
        
        log.debug("폭우 기반 트리거 조건 미충족: userId={}, rainfall={}mm/h", 
                context.getUser().getId(), rainfallInfo.hourlyRainfall);
        return TriggerResult.notTriggered();
    }
    
    /**
     * 공공 API 데이터에서 강수량 정보를 추출합니다.
     * 
     * @param context 트리거 컨텍스트
     * @return 강수량 정보
     */
    private RainfallInfo extractRainfallInfo(TriggerContext context) {
        // 서울시 실시간 도시 데이터에서 강수량 추출
        Object weatherData = context.getPublicApiData().get("WEATHER_STTS");
        if (weatherData instanceof java.util.List) {
            @SuppressWarnings("unchecked")
            java.util.List<java.util.Map<String, Object>> weatherList = 
                    (java.util.List<java.util.Map<String, Object>>) weatherData;
            
            if (!weatherList.isEmpty()) {
                java.util.Map<String, Object> weather = weatherList.get(0);
                
                double hourlyRainfall = getDoubleValue(weather, "RAIN_HOUR", "RAINFALL_1H");
                double dailyRainfall = getDoubleValue(weather, "RAIN_DAY", "RAINFALL_24H");
                
                return RainfallInfo.builder()
                        .hourlyRainfall(hourlyRainfall)
                        .dailyRainfall(dailyRainfall)
                        .build();
            }
        }
        
        // 별도 강수량 API 데이터 확인
        Object rainData = context.getPublicApiData().get("rainInfo");
        if (rainData instanceof java.util.List) {
            @SuppressWarnings("unchecked")
            java.util.List<java.util.Map<String, Object>> rainList = 
                    (java.util.List<java.util.Map<String, Object>>) rainData;
            
            if (!rainList.isEmpty()) {
                java.util.Map<String, Object> rain = rainList.get(0);
                
                double hourlyRainfall = getDoubleValue(rain, "RAINFALL1H", "RF1H");
                double dailyRainfall = getDoubleValue(rain, "RAINFALL24H", "RF24H");
                
                return RainfallInfo.builder()
                        .hourlyRainfall(hourlyRainfall)
                        .dailyRainfall(dailyRainfall)
                        .build();
            }
        }
        
        return null;
    }
    
    /**
     * Map에서 Double 값을 안전하게 추출합니다. 여러 키를 시도합니다.
     * 
     * @param map 데이터 맵
     * @param keys 시도할 키들
     * @return Double 값
     */
    private double getDoubleValue(java.util.Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value instanceof String) {
                try {
                    return Double.parseDouble((String) value);
                } catch (NumberFormatException e) {
                    // 다음 키 시도
                }
            } else if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        }
        return 0.0;
    }
    
    @Override
    public String getSupportedTriggerType() {
        return "HEAVY_RAIN";
    }
    
    @Override
    public int getPriority() {
        return 5; // 매우 높은 우선순위
    }
    
    @Override
    public String getDescription() {
        return "강수량이 설정된 임계값을 초과할 때 알림을 발송합니다.";
    }
    
    /**
     * 강수량 정보를 담는 내부 클래스
     */
    @lombok.Builder
    private static class RainfallInfo {
        private final double hourlyRainfall;  // 시간당 강수량 (mm/h)
        private final double dailyRainfall;   // 일 누적 강수량 (mm)
    }
}