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
    
    @Value("${urbanping.trigger.bike.shortage-threshold:10}")
    private int shortageThreshold;
    
    @Value("${urbanping.trigger.bike.full-threshold:95}")
    private int fullThreshold;
    
    @Value("${urbanping.trigger.bike.location-radius:2000}")
    private double locationRadius; // 미터 단위
    
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
        
        // 자전거 부족 상태 체크
        for (BikeStationInfo station : nearbyStations) {
            if (station.availabilityRate <= shortageThreshold) {
                return TriggerResult.triggered(
                        NotificationType.BIKE_SHARING,
                        TriggerCondition.BIKE_SHORTAGE,
                        "따릉이 부족 알림",
                        String.format("%s 대여소의 자전거가 부족합니다. (이용가능: %d%%, 거리: %.0fm)", 
                                station.stationName, station.availabilityRate, station.distance),
                        String.format("위도: %.6f, 경도: %.6f", station.latitude, station.longitude)
                );
            }
        }
        
        // 대여소 포화 상태 체크
        for (BikeStationInfo station : nearbyStations) {
            if (station.availabilityRate >= fullThreshold) {
                return TriggerResult.triggered(
                        NotificationType.BIKE_SHARING,
                        TriggerCondition.BIKE_FULL,
                        "따릉이 대여소 포화 알림",
                        String.format("%s 대여소가 거의 만차입니다. (이용가능: %d%%, 거리: %.0fm)", 
                                station.stationName, station.availabilityRate, station.distance),
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
        Object bikeData = context.getPublicApiData().get("SBIKE_STTS");
        if (!(bikeData instanceof List)) {
            return List.of();
        }
        
        List<Map<String, Object>> bikeStations = (List<Map<String, Object>>) bikeData;
        
        return bikeStations.stream()
                .map(this::mapToBikeStationInfo)
                .filter(station -> station != null)
                .filter(station -> isWithinRadius(context, station))
                .toList();
    }
    
    /**
     * Map 데이터를 BikeStationInfo로 변환합니다.
     * 
     * @param stationData 대여소 데이터
     * @return BikeStationInfo 객체
     */
    private BikeStationInfo mapToBikeStationInfo(Map<String, Object> stationData) {
        try {
            String stationName = getStringValue(stationData, "SBIKE_SPOT_NM");
            double latitude = getDoubleValue(stationData, "SBIKE_Y");
            double longitude = getDoubleValue(stationData, "SBIKE_X");
            int availabilityRate = getIntValue(stationData, "SBIKE_SHARED");
            int parkingCount = getIntValue(stationData, "SBIKE_PARKING_CNT");
            int rackCount = getIntValue(stationData, "SBIKE_RACK_CNT");
            
            return BikeStationInfo.builder()
                    .stationName(stationName)
                    .latitude(latitude)
                    .longitude(longitude)
                    .availabilityRate(availabilityRate)
                    .parkingCount(parkingCount)
                    .rackCount(rackCount)
                    .build();
        } catch (Exception e) {
            log.warn("따릉이 대여소 데이터 파싱 실패: {}", stationData, e);
            return null;
        }
    }
    
    /**
     * 대여소가 사용자 위치 반경 내에 있는지 확인합니다.
     * 
     * @param context 트리거 컨텍스트
     * @param station 대여소 정보
     * @return 반경 내 여부
     */
    private boolean isWithinRadius(TriggerContext context, BikeStationInfo station) {
        double distance = calculateDistance(
                context.getUserLatitude(), context.getUserLongitude(),
                station.latitude, station.longitude
        );
        
        station.distance = distance;
        return distance <= locationRadius;
    }
    
    /**
     * 두 지점 간의 거리를 계산합니다 (Haversine formula).
     * 
     * @param lat1 위도1
     * @param lon1 경도1
     * @param lat2 위도2
     * @param lon2 경도2
     * @return 거리 (미터)
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름 (km)
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c * 1000; // 미터로 변환
    }
    
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
    
    private double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
    
    private int getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
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
        private double distance; // 사용자로부터의 거리
    }
}
