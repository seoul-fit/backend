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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 위치 기반 따릉이 트리거 전략
 * 
 * 사용자 위치 주변의 따릉이 대여소 상황을 실시간으로 모니터링하여
 * 자전거 부족이나 포화 상태를 알림
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Component
public class LocationBasedBikeShareTriggerStrategy implements TriggerStrategy {

    @Value("${urbanping.trigger.bike-share.shortage-threshold:3}")
    private int shortageThreshold; // 자전거가 3대 이하일 때 부족 알림

    @Value("${urbanping.trigger.bike-share.full-threshold:2}")
    private int fullThreshold; // 빈 슬롯이 2개 이하일 때 포화 알림

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
            Map<String, String> metadata = createLocationBikeStationMetadata(shortageStation);
            return TriggerResult.builder()
                    .triggered(true)
                    .notificationType(NotificationType.BIKE_SHARING)
                    .triggerCondition(TriggerCondition.BIKE_SHORTAGE)
                    .title("따릉이 부족 알림")
                    .message(String.format("%s 대여소에 자전거가 %d대만 남았습니다. 다른 대여소를 이용해보세요.",
                            shortageStation.getName(), shortageStation.getAvailableBikes()))
                    .locationInfo(shortageStation.getAddress())
                    .priority(20)
                    .additionalData(Map.of("metadata", metadata))
                    .build();
        }

        // 포화 상황 체크 (반납할 곳이 없는 경우)
        BikeStation fullStation = findFullStation(nearbyStations);
        if (fullStation != null) {
            Map<String, String> metadata = createLocationBikeStationMetadata(fullStation);
            return TriggerResult.builder()
                    .triggered(true)
                    .notificationType(NotificationType.BIKE_SHARING)
                    .triggerCondition(TriggerCondition.BIKE_FULL)
                    .title("따릉이 반납 주의")
                    .message(String.format("%s 대여소가 거의 가득 찼습니다. 반납 공간이 %d개만 남았습니다.",
                            fullStation.getName(), fullStation.getAvailableSlots()))
                    .locationInfo(fullStation.getAddress())
                    .priority(30)
                    .additionalData(Map.of("metadata", metadata))
                    .build();
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

        List<BikeStation> nearbyStations = stations.stream()
                .map(this::mapToBikeStation)
                .filter(station -> station != null)
                .filter(station -> TriggerUtils.calculateDistance(userLat, userLng, 
                        station.getLatitude(), station.getLongitude()) <= searchRadius)
                .toList();
                
        log.debug("사용자 위치 기준 {}m 반경 내 따릉이 대여소: {}건 (전체 {}건 중)", 
                searchRadius, nearbyStations.size(), stations.size());
                
        return nearbyStations;
    }

    /**
     * Map 데이터를 BikeStation 객체로 변환
     */
    private BikeStation mapToBikeStation(Map<String, Object> data) {
        try {
            String id = TriggerUtils.getStringValue(data, "stationId");
            String name = TriggerUtils.getStringValue(data, "stationName");
            Double latitude = TriggerUtils.getDoubleValue(data, "stationLatitude");
            Double longitude = TriggerUtils.getDoubleValue(data, "stationLongitude");
            Integer parkingBikes = TriggerUtils.getIntValue(data, "parkingBikeTotCnt"); // 주차된 자전거 수
            Integer totalRacks = TriggerUtils.getIntValue(data, "rackTotCnt"); // 총 거치대 수
            
            if (latitude == null || longitude == null) {
                log.warn("따릉이 대여소 데이터에 위치 정보 없음: {}", data);
                return null;
            }
            
            int racks = totalRacks != null ? totalRacks : 0;
            int bikes = parkingBikes != null ? parkingBikes : 0;
            int freeSlots = Math.max(0, racks - bikes);
            
            return BikeStation.builder()
                    .id(id != null ? id : "")
                    .name(name != null ? name : "")
                    .address("") // 주소 정보는 API에서 제공하지 않음
                    .latitude(latitude)
                    .longitude(longitude)
                    .availableBikes(bikes) // 현재 주차된 자전거 수
                    .availableSlots(freeSlots) // 사용 가능한 거치대 수
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
                .filter(station -> station.getAvailableSlots() <= fullThreshold)
                .filter(station -> station.getAvailableSlots() > 0) // 완전히 가득 찬 것은 제외
                .findFirst()
                .orElse(null);
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
     * 위치 기반 따릉이 대여소의 메타데이터를 생성합니다.
     * 
     * @param station 대여소 정보
     * @return 메타데이터 맵
     */
    private Map<String, String> createLocationBikeStationMetadata(BikeStation station) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("bike_station_id", station.getId() != null ? station.getId() : "unknown");
        metadata.put("station_name", station.getName() != null ? station.getName() : "");
        metadata.put("available_bikes", String.valueOf(station.getAvailableBikes()));
        metadata.put("available_slots", String.valueOf(station.getAvailableSlots()));
        metadata.put("source", "PUBLIC_API_LOCATION");
        return metadata;
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
