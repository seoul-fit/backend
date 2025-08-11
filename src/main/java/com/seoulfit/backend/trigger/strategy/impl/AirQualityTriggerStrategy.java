package com.seoulfit.backend.trigger.strategy.impl;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import com.seoulfit.backend.trigger.dto.TriggerContext;
import com.seoulfit.backend.trigger.dto.TriggerResult;
import com.seoulfit.backend.trigger.strategy.TriggerStrategy;
import com.seoulfit.backend.trigger.utils.TriggerUtils;
import com.seoulfit.backend.user.domain.InterestCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 대기질 기반 트리거 전략
 * 
 * 미세먼지, 초미세먼지 등 대기질이 나쁨 이상일 때 알림을 발송하는 전략
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Component
public class AirQualityTriggerStrategy implements TriggerStrategy {
    
    private static final List<String> BAD_AIR_QUALITY_LEVELS = Arrays.asList("나쁨", "매우나쁨");
    
    @Override
    public TriggerResult evaluate(TriggerContext context) {
        log.debug("대기질 기반 트리거 평가 시작: userId={}", context.getUser().getId());
        
        // 사용자가 날씨에 관심이 있는지 확인
        if (!context.getUserInterests().contains(InterestCategory.WEATHER)) {
            log.debug("사용자가 날씨에 관심이 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        // 공공 API 데이터에서 대기질 정보 추출
        AirQualityInfo airQuality = extractAirQuality(context);
        if (airQuality == null) {
            log.warn("대기질 정보를 찾을 수 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        log.debug("현재 대기질: PM10={}, PM2.5={}, 통합지수={}", 
                airQuality.pm10Index, airQuality.pm25Index, airQuality.airIndex);
        
        // 대기질이 나쁨 이상인지 확인
        if (isBadAirQuality(airQuality)) {
            String message = buildAirQualityMessage(airQuality);
            
            return TriggerResult.triggered(
                    NotificationType.WEATHER,
                    TriggerCondition.AIR_QUALITY_BAD,
                    "대기질 주의보",
                    message,
                    context.getPublicApiData("locationInfo", String.class)
            );
        }
        
        log.debug("대기질 기반 트리거 조건 미충족: userId={}", context.getUser().getId());
        return TriggerResult.notTriggered();
    }
    
    /**
     * 공공 API 데이터에서 대기질 정보를 추출합니다.
     * 
     * @param context 트리거 컨텍스트
     * @return 대기질 정보
     */
    private AirQualityInfo extractAirQuality(TriggerContext context) {
        // 서울시 실시간 도시 데이터에서 대기질 추출
        Object weatherData = context.getPublicApiData().get("WEATHER_STTS");
        if (weatherData instanceof java.util.List) {
            @SuppressWarnings("unchecked")
            java.util.List<java.util.Map<String, Object>> weatherList = 
                    (java.util.List<java.util.Map<String, Object>>) weatherData;
            
            if (!weatherList.isEmpty()) {
                java.util.Map<String, Object> weather = weatherList.get(0);
                
                return AirQualityInfo.builder()
                        .pm10Index(TriggerUtils.getStringValue(weather, "PM10_INDEX"))
                        .pm25Index(TriggerUtils.getStringValue(weather, "PM25_INDEX"))
                        .airIndex(TriggerUtils.getStringValue(weather, "AIR_IDX"))
                        .pm10Value(TriggerUtils.getStringValue(weather, "PM10"))
                        .pm25Value(TriggerUtils.getStringValue(weather, "PM25"))
                        .build();
            }
        }
        
        return null;
    }
    
    
    /**
     * 대기질이 나쁜지 확인합니다.
     * 
     * @param airQuality 대기질 정보
     * @return 나쁜 대기질 여부
     */
    private boolean isBadAirQuality(AirQualityInfo airQuality) {
        return BAD_AIR_QUALITY_LEVELS.contains(airQuality.pm10Index) ||
               BAD_AIR_QUALITY_LEVELS.contains(airQuality.pm25Index) ||
               BAD_AIR_QUALITY_LEVELS.contains(airQuality.airIndex);
    }
    
    /**
     * 대기질 알림 메시지를 생성합니다.
     * 
     * @param airQuality 대기질 정보
     * @return 알림 메시지
     */
    private String buildAirQualityMessage(AirQualityInfo airQuality) {
        StringBuilder message = new StringBuilder("현재 대기질이 좋지 않습니다. ");
        
        if (BAD_AIR_QUALITY_LEVELS.contains(airQuality.pm10Index)) {
            message.append(String.format("미세먼지: %s(%s㎍/㎥) ", 
                    airQuality.pm10Index, airQuality.pm10Value));
        }
        
        if (BAD_AIR_QUALITY_LEVELS.contains(airQuality.pm25Index)) {
            message.append(String.format("초미세먼지: %s(%s㎍/㎥) ", 
                    airQuality.pm25Index, airQuality.pm25Value));
        }
        
        message.append("외출 시 마스크 착용을 권장합니다.");
        
        return message.toString();
    }
    
    @Override
    public String getSupportedTriggerType() {
        return "AIR_QUALITY";
    }
    
    @Override
    public int getPriority() {
        return 20; // 중간 우선순위
    }
    
    @Override
    public String getDescription() {
        return "미세먼지, 초미세먼지 등 대기질이 나쁨 이상일 때 알림을 발송합니다.";
    }
    
    /**
     * 대기질 정보를 담는 내부 클래스
     */
    @lombok.Builder
    private static class AirQualityInfo {
        private final String pm10Index;
        private final String pm25Index;
        private final String airIndex;
        private final String pm10Value;
        private final String pm25Value;
    }
}
