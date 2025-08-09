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
 * 위치 기반 따릉이 트리거 전략
 * 
 * 사용자 위치 주변의 따릉이 대여소 상황을 실시간으로 모니터링하여
 * 자전거 부족이나 포화 상태를 알림
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class LocationBasedBikeShareTriggerStrategy implements TriggerStrategy {

    @Value("${urbanping.trigger.bike-share.shortage-threshold:2}")
    private int shortageThreshold;

    @Value("${urbanping.trigger.bike-share.excess-threshold:15}")
    private int excessThreshold;

    @Value("${urbanping.trigger.bike-share.search-radius:500}")
    private int searchRadius; // 500m

    @Override
    public TriggerResult evaluate(TriggerContext context) {
        log.debug("위치 기반 따릉이 트리거 평가 시작: userId={}, location=[{}, {}]", 
                context.getUser().getId(), context.getUserLatitude(), context.getUserLongitude());

        // 사용자가 따릉이에 관심이 있는지 확인
        if (!context.getUserInterests().contains(InterestCategory.BIKE)) {
            log.debug("사용자가 따릉이에 관심이 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }

        // 위치 정보가 있는지 확인
        if (context.getUserLatitude() == null || context.getUserLongitude() == null) {
            log.debug("사용자 위치 정보 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }

        // 주변 따릉이 대여소 정보 추출
        List<BikeStation> nearbyStations = extractNearbyBikeStations(context);
        if (nearbyStations.isEmpty()) {
            log.debug("주변 따릉이 대여소 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }

        // 부족 상황 체크
        BikeStation shortageStation = findShortageStation(nearbyStations);
        if (shortageStation != null) {
            return TriggerResult.highPriorityTriggered(
                    NotificationType.TRAFFIC,
                    TriggerCondition.BIKE_SHORTAGE,
                    "따릉이 부족 알림",
                    String.format("%s 대여소에 자전거가 %d대만 남았습니다. 다른 대여소를 이용해보세요.",
                            shortageStation.getName(), shortageStation.getAvailableBikes()),
                    shortageStation.getAddress(),
                    20 // 높은 우선순위
            );
        }

        // 포화 상황 체크 (반납할 곳이 없는 경우)
        BikeStation fullStation = findFullStation(nearbyStations);
        if (fullStation != null) {
            return TriggerResult.triggered(
                    NotificationType.TRAFFIC,
                    TriggerCondition.BIKE_FULL,
                    "따릉이 반납 주의",
                    String.format("%s 대여소가 거의 가득 찼습니다. 반납 공간이 %d개만 남았습니다.",
                            fullStation.getName(), fullStation.getAvailableSlots()),
                    fullStation.getAddress()
            );
        }

        log.debug("위치 기반 따릉이 트리거 조건 미충족: userId={}", context.getUser().getId());
        return TriggerResult.notTriggered();
    }

    /**
     * 주변 따릉이 대여소 정보 추출
     */
    @SuppressWarnings("unchecked")
    private List<BikeStation> extractNearbyBikeStations(TriggerContext context) {
        Object bikeData = context.getPublicApiData().get("BIKE_SHARE");
        if (!(bikeData instanceof List)) {
            return List.of();
        }

        List<Map<String, Object>> stations = (List<Map<String, Object>>) bikeData;
        double userLat = context.getUserLatitude();
        double userLng = context.getUserLongitude();

        return stations.stream()
                .map(this::mapToBikeStation)
                .filter(station -> station != null)
                .filter(station -> calculateDistance(userLat, userLng, 
                        station.getLatitude(), station.getLongitude()) <= searchRadius)
                .toList();
    }

    /**
     * Map 데이터를 BikeStation 객체로 변환
     */
    private BikeStation mapToBikeStation(Map<String, Object> data) {
        try {
            return BikeStation.builder()
                    .id(String.valueOf(data.get("STATION_ID")))
                    .name(String.valueOf(data.get("STATION_NAME")))
                    .address(String.valueOf(data.get("ADDR")))
                    .latitude(Double.parseDouble(String.valueOf(data.get("LAT"))))
                    .longitude(Double.parseDouble(String.valueOf(data.get("LNG"))))
                    .availableBikes(Integer.parseInt(String.valueOf(data.get("BIKE_COUNT"))))
                    .availableSlots(Integer.parseInt(String.valueOf(data.get("SLOT_COUNT"))))
                    .build();
        } catch (Exception e) {
            log.warn("따릉이 대여소 데이터 파싱 실패: {}", data, e);
            return null;
        }
    }

    /**
     * 자전거 부족 대여소 찾기
     */
    private BikeStation findShortageStation(List<BikeStation> stations) {
        return stations.stream()
                .filter(station -> station.getAvailableBikes() <= shortageThreshold)
                .filter(station -> station.getAvailableBikes() > 0) // 완전히 없는 것은 제외
                .findFirst()
                .orElse(null);
    }

    /**
     * 포화 상태 대여소 찾기
     */
    private BikeStation findFullStation(List<BikeStation> stations) {
        return stations.stream()
                .filter(station -> station.getAvailableSlots() <= 2)
                .filter(station -> station.getAvailableSlots() > 0) // 완전히 가득 찬 것은 제외
                .findFirst()
                .orElse(null);
    }

    /**
     * 두 지점 간 거리 계산 (미터)
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371; // 지구 반지름 (km)
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c * 1000; // 미터로 변환
    }

    @Override
    public String getSupportedTriggerType() {
        return "LOCATION_BIKE_SHARE";
    }

    @Override
    public int getPriority() {
        return 20; // 높은 우선순위
    }

    @Override
    public String getDescription() {
        return "사용자 위치 주변의 따릉이 대여소 상황을 실시간으로 모니터링하여 자전거 부족이나 포화 상태를 알림합니다.";
    }

    /**
     * 따릉이 대여소 정보를 담는 내부 클래스
     */
    @lombok.Builder
    @lombok.Getter
    private static class BikeStation {
        private final String id;
        private final String name;
        private final String address;
        private final double latitude;
        private final double longitude;
        private final int availableBikes;
        private final int availableSlots;
    }
}
