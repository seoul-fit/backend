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

import java.util.List;
import java.util.Map;

/**
 * 따릉이 기반 트리거 전략
 * 
 * 따릉이 대여소의 자전거 부족 또는 포화 상태일 때 알림을 발송하는 전략
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Component
public class BikeShareTriggerStrategy implements TriggerStrategy {
    
    @Value("${urbanping.trigger.bike.shortage-threshold:3}")
    private int shortageThreshold; // 자전거가 3대 이하일 때 부족 알림
    
    @Value("${urbanping.trigger.bike.full-threshold:90}")
    private int fullThreshold; // 거치율이 90% 이상일 때 포화 알림
    
    @Value("${urbanping.trigger.bike.location-radius:2000}")
    private double locationRadius; // 미터 단위 (기본값: 2km)
    
    @Override
    public TriggerResult evaluate(TriggerContext context) {
        log.debug("따릉이 기반 트리거 평가 시작: userId={}", context.getUser().getId());
        
        // 사용자가 따릉이에 관심이 있는지 확인
        if (!context.getUserInterests().contains(InterestCategory.BIKE)) {
            log.debug("사용자가 따릉이에 관심이 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        // 사용자 위치 정보 확인
        if (context.getUserLatitude() == null || context.getUserLongitude() == null) {
            log.debug("사용자 위치 정보 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        // 공공 API 데이터에서 따릉이 정보 추출
        List<BikeStationInfo> nearbyStations = extractNearbyBikeStations(context);
        if (nearbyStations.isEmpty()) {
            log.debug("주변 따릉이 대여소 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        log.debug("주변 따릉이 대여소 {}개 발견: userId={}", nearbyStations.size(), context.getUser().getId());
        
        // 자전거 부족 상태 체크 (주차된 자전거 수가 임계값 이하인 경우)
        for (BikeStationInfo station : nearbyStations) {
            if (station.parkingCount <= shortageThreshold && station.parkingCount > 0) {
                return TriggerResult.triggered(
                        NotificationType.BIKE_SHARING,
                        TriggerCondition.BIKE_SHORTAGE,
                        "따릉이 부족 알림",
                        String.format("%s 대여소에 자전거가 %d대만 남았습니다. (거치율: %d%%, 거리: %.0fm)", 
                                station.stationName, station.parkingCount, station.availabilityRate, station.distance),
                        String.format("위도: %.6f, 경도: %.6f", station.latitude, station.longitude)
                );
            }
        }
        
        // 대여소 포화 상태 체크 (거치율이 높아서 반납이 어려운 경우)
        for (BikeStationInfo station : nearbyStations) {
            if (station.availabilityRate >= fullThreshold) {
                int availableSlots = Math.max(0, station.rackCount - station.parkingCount);
                return TriggerResult.triggered(
                        NotificationType.BIKE_SHARING,
                        TriggerCondition.BIKE_FULL,
                        "따릉이 반납 주의",
                        String.format("%s 대여소가 거의 만차입니다. (반납가능: %d대, 거치율: %d%%, 거리: %.0fm)", 
                                station.stationName, availableSlots, station.availabilityRate, station.distance),
                        String.format("위도: %.6f, 경도: %.6f", station.latitude, station.longitude)
                );
            }
        }
        
        log.debug("따릉이 기반 트리거 조건 미충족: userId={}", context.getUser().getId());
        return TriggerResult.notTriggered();
    }
    
    /**
     * 공공 API 데이터에서 주변 따릉이 대여소 정보를 추출합니다.
     * 
     * @param context 트리거 컨텍스트
     * @return 주변 대여소 정보 목록
     */
    @SuppressWarnings("unchecked")
    private List<BikeStationInfo> extractNearbyBikeStations(TriggerContext context) {
        Object bikeData = context.getPublicApiData().get("BIKE_SHARE");
        if (!(bikeData instanceof List)) {
            log.debug("따릉이 데이터가 List 형태가 아님: type={}", bikeData != null ? bikeData.getClass().getSimpleName() : "null");
            return List.of();
        }
        
        List<Map<String, Object>> bikeStations = (List<Map<String, Object>>) bikeData;
        log.debug("전체 따릉이 대여소 수: {}", bikeStations.size());
        
        List<BikeStationInfo> nearbyStations = bikeStations.stream()
                .map(stationData -> mapToBikeStationInfo(stationData, context))
                .filter(station -> station != null)
                .filter(station -> station.distance <= locationRadius)
                .toList();
                
        log.debug("사용자 위치 기준 {}m 반경 내 따릉이 대여소: {}건 (전체 {}건 중)", 
                locationRadius, nearbyStations.size(), bikeStations.size());
                
        return nearbyStations;
    }
    
    /**
     * Map 데이터를 BikeStationInfo로 변환합니다.
     * 
     * @param stationData 대여소 데이터
     * @param context 트리거 컨텍스트 (사용자 위치 정보 포함)
     * @return BikeStationInfo 객체
     */
    private BikeStationInfo mapToBikeStationInfo(Map<String, Object> stationData, TriggerContext context) {
        try {
            String stationName = TriggerUtils.getStringValue(stationData, "stationName");
            Double latitude = TriggerUtils.getDoubleValue(stationData, "stationLatitude");
            Double longitude = TriggerUtils.getDoubleValue(stationData, "stationLongitude");
            Integer shared = TriggerUtils.getIntValue(stationData, "shared"); // 거치율(%)
            Integer parkingCount = TriggerUtils.getIntValue(stationData, "parkingBikeTotCnt"); // 주차된 자전거 수
            Integer rackCount = TriggerUtils.getIntValue(stationData, "rackTotCnt"); // 총 거치대 수
            
            if (latitude == null || longitude == null) {
                log.warn("따릉이 대여소 데이터에 위치 정보 없음: {}", stationData);
                return null;
            }
            
            // 사용자 위치로부터의 거리 계산
            double distance = TriggerUtils.calculateDistance(
                    context.getUserLatitude(), context.getUserLongitude(),
                    latitude, longitude
            );
            
            return BikeStationInfo.builder()
                    .stationName(stationName != null ? stationName : "")
                    .latitude(latitude)
                    .longitude(longitude)
                    .availabilityRate(shared != null ? shared : 0) // 거치율
                    .parkingCount(parkingCount != null ? parkingCount : 0) // 주차된 자전거 수
                    .rackCount(rackCount != null ? rackCount : 0) // 총 거치대 수
                    .distance(distance) // 사용자로부터의 거리
                    .build();
        } catch (Exception e) {
            log.warn("따릉이 대여소 데이터 파싱 실패: {}", stationData, e);
            return null;
        }
    }
    
    @Override
    public String getSupportedTriggerType() {
        return "BIKE_SHARING";
    }
    
    @Override
    public int getPriority() {
        return 30; // 중간 우선순위
    }
    
    @Override
    public String getDescription() {
        return "따릉이 대여소의 자전거 부족 또는 포화 상태일 때 알림을 발송합니다.";
    }
    
    /**
     * 따릉이 대여소 정보를 담는 내부 클래스
     */
    @lombok.Builder
    private static class BikeStationInfo {
        private final String stationName;
        private final double latitude;
        private final double longitude;
        private final int availabilityRate;
        private final int parkingCount;
        private final int rackCount;
        private final double distance; // 사용자로부터의 거리
    }
}
