package com.seoulfit.backend.trigger.strategy.impl;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import com.seoulfit.backend.trigger.dto.TriggerContext;
import com.seoulfit.backend.trigger.dto.TriggerResult;
import com.seoulfit.backend.trigger.strategy.TriggerStrategy;
import com.seoulfit.backend.trigger.utils.TriggerUtils;
import com.seoulfit.backend.user.domain.InterestCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 혼잡도 기반 트리거 전략
 * 
 * 특정 지역의 인구 혼잡도가 높을 때 알림을 발송하는 전략
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Component
public class CongestionTriggerStrategy implements TriggerStrategy {
    
    private static final List<String> CROWDED_LEVELS = Arrays.asList("혼잡", "매우혼잡");
    
    @Value("${urbanping.trigger.congestion.location-radius:1000}")
    private double locationRadius; // 미터 단위
    
    @Override
    public TriggerResult evaluate(TriggerContext context) {
        log.debug("혼잡도 기반 트리거 평가 시작: userId={}", context.getUser().getId());
        
        // 사용자가 교통이나 혼잡도에 관심이 있는지 확인
        if (!context.getUserInterests().contains(InterestCategory.CONGESTION)) {
            log.debug("사용자가 교통/혼잡도에 관심이 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        // 사용자 위치 정보 확인
        if (context.getUserLatitude() == null || context.getUserLongitude() == null) {
            log.debug("사용자 위치 정보 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        // 공공 API 데이터에서 혼잡도 정보 추출
        List<CongestionInfo> nearbyCongestions = extractNearbyCongestionInfo(context);
        if (nearbyCongestions.isEmpty()) {
            log.debug("주변 혼잡도 정보 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        // 혼잡한 지역 체크
        for (CongestionInfo congestion : nearbyCongestions) {
            if (CROWDED_LEVELS.contains(congestion.congestionLevel)) {
                String message = buildCongestionMessage(congestion);
                
                return TriggerResult.triggered(
                        NotificationType.CONGESTION,
                        TriggerCondition.CONGESTION,
                        "주변 지역 혼잡 알림",
                        message,
                        String.format("위도: %.6f, 경도: %.6f", congestion.latitude, congestion.longitude)
                );
            }
        }
        
        log.debug("혼잡도 기반 트리거 조건 미충족: userId={}", context.getUser().getId());
        return TriggerResult.notTriggered();
    }
    
    /**
     * 공공 API 데이터에서 주변 혼잡도 정보를 추출합니다.
     * 
     * @param context 트리거 컨텍스트
     * @return 주변 혼잡도 정보 목록
     */
    @SuppressWarnings("unchecked")
    private List<CongestionInfo> extractNearbyCongestionInfo(TriggerContext context) {
        // 서울시 실시간 도시 데이터에서 혼잡도 정보 추출
        Object livePopData = context.getPublicApiData().get("LIVE_PPLTN_STTS");
        if (livePopData instanceof List) {
            List<java.util.Map<String, Object>> populationData = 
                    (List<java.util.Map<String, Object>>) livePopData;
            
            return populationData.stream()
                    .map(this::mapToCongestionInfo)
                    .filter(congestion -> congestion != null)
                    .filter(congestion -> isWithinRadius(context, congestion))
                    .toList();
        }
        
        return List.of();
    }
    
    /**
     * Map 데이터를 CongestionInfo로 변환합니다.
     * 
     * @param congestionData 혼잡도 데이터
     * @return CongestionInfo 객체
     */
    private CongestionInfo mapToCongestionInfo(java.util.Map<String, Object> congestionData) {
        try {
            String areaName = TriggerUtils.getStringValue(congestionData, "AREA_NM");
            String congestionLevel = TriggerUtils.getStringValue(congestionData, "AREA_CONGEST_LVL");
            Double latitude = TriggerUtils.getDoubleValue(congestionData, "AREA_Y");
            Double longitude = TriggerUtils.getDoubleValue(congestionData, "AREA_X");
            String popMin = TriggerUtils.getStringValue(congestionData, "AREA_PPLTN_MIN");
            String popMax = TriggerUtils.getStringValue(congestionData, "AREA_PPLTN_MAX");
            String message = TriggerUtils.getStringValue(congestionData, "AREA_CONGEST_MSG");
            
            if (latitude == null || longitude == null) {
                log.warn("혼잡도 데이터에 위치 정보 없음: {}", congestionData);
                return null;
            }
            
            return CongestionInfo.builder()
                    .areaName(areaName)
                    .congestionLevel(congestionLevel)
                    .latitude(latitude)
                    .longitude(longitude)
                    .populationMin(popMin)
                    .populationMax(popMax)
                    .congestionMessage(message)
                    .build();
        } catch (Exception e) {
            log.warn("혼잡도 데이터 파싱 실패: {}", congestionData, e);
            return null;
        }
    }
    
    /**
     * 혼잡도 정보가 사용자 위치 반경 내에 있는지 확인합니다.
     * 
     * @param context 트리거 컨텍스트
     * @param congestion 혼잡도 정보
     * @return 반경 내 여부
     */
    private boolean isWithinRadius(TriggerContext context, CongestionInfo congestion) {
        double distance = TriggerUtils.calculateDistance(
                context.getUserLatitude(), context.getUserLongitude(),
                congestion.latitude, congestion.longitude
        );
        
        congestion.distance = distance;
        return distance <= locationRadius;
    }
    
    
    /**
     * 혼잡도 알림 메시지를 생성합니다.
     * 
     * @param congestion 혼잡도 정보
     * @return 알림 메시지
     */
    private String buildCongestionMessage(CongestionInfo congestion) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("%s 지역이 현재 %s 상태입니다. ", 
                congestion.areaName, congestion.congestionLevel));
        
        if (congestion.congestionMessage != null && !congestion.congestionMessage.isEmpty()) {
            message.append(congestion.congestionMessage).append(" ");
        }
        
        if (congestion.populationMin != null && congestion.populationMax != null) {
            message.append(String.format("(예상 인구: %s~%s명) ", 
                    congestion.populationMin, congestion.populationMax));
        }
        
        message.append(String.format("거리: %.0fm", congestion.distance));
        
        return message.toString();
    }
    
    
    @Override
    public String getSupportedTriggerType() {
        return "CONGESTION";
    }
    
    @Override
    public int getPriority() {
        return 40; // 중간-낮은 우선순위
    }
    
    @Override
    public String getDescription() {
        return "특정 지역의 인구 혼잡도가 높을 때 알림을 발송합니다.";
    }
    
    /**
     * 혼잡도 정보를 담는 내부 클래스
     */
    @lombok.Builder
    private static class CongestionInfo {
        private final String areaName;
        private final String congestionLevel;
        private final double latitude;
        private final double longitude;
        private final String populationMin;
        private final String populationMax;
        private final String congestionMessage;
        private double distance; // 사용자로부터의 거리
    }
}