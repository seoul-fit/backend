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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 문화행사 기반 트리거 전략
 * 
 * 사용자 주변의 새로운 문화행사나 관심있는 문화행사가 시작될 때 알림을 발송하는 전략
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class CulturalEventTriggerStrategy implements TriggerStrategy {
    
    @Value("${urbanping.trigger.cultural.location-radius:5000}")
    private double locationRadius; // 미터 단위
    
    @Value("${urbanping.trigger.cultural.days-ahead:7}")
    private int daysAhead; // 며칠 전까지의 행사를 알림할지
    
    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyyMMdd")
    );
    
    @Override
    public TriggerResult evaluate(TriggerContext context) {
        log.debug("문화행사 기반 트리거 평가 시작: userId={}", context.getUser().getId());
        
        // 사용자가 문화생활에 관심이 있는지 확인
        if (!context.getUserInterests().contains(InterestCategory.CULTURE)) {
            log.debug("사용자가 문화생활에 관심이 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        // 사용자 위치 정보 확인
        if (context.getUserLatitude() == null || context.getUserLongitude() == null) {
            log.debug("사용자 위치 정보 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        // 공공 API 데이터에서 문화행사 정보 추출
        List<CulturalEventInfo> nearbyEvents = extractNearbyCulturalEvents(context);
        if (nearbyEvents.isEmpty()) {
            log.debug("주변 문화행사 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        // 오늘 시작하는 행사 체크
        for (CulturalEventInfo event : nearbyEvents) {
            if (isEventStartingToday(event)) {
                return TriggerResult.triggered(
                        NotificationType.CULTURE,
                        TriggerCondition.CULTURAL_EVENT_START,
                        "오늘 시작하는 문화행사",
                        buildEventStartMessage(event),
                        String.format("위도: %.6f, 경도: %.6f", event.latitude, event.longitude)
                );
            }
        }
        
        // 곧 시작할 행사 체크
        for (CulturalEventInfo event : nearbyEvents) {
            if (isEventStartingSoon(event)) {
                return TriggerResult.triggered(
                        NotificationType.CULTURE,
                        TriggerCondition.CULTURAL_EVENT,
                        "곧 시작하는 문화행사",
                        buildEventSoonMessage(event),
                        String.format("위도: %.6f, 경도: %.6f", event.latitude, event.longitude)
                );
            }
        }
        
        log.debug("문화행사 기반 트리거 조건 미충족: userId={}", context.getUser().getId());
        return TriggerResult.notTriggered();
    }
    
    /**
     * 공공 API 데이터에서 주변 문화행사 정보를 추출합니다.
     * 
     * @param context 트리거 컨텍스트
     * @return 주변 문화행사 정보 목록
     */
    @SuppressWarnings("unchecked")
    private List<CulturalEventInfo> extractNearbyCulturalEvents(TriggerContext context) {
        Object culturalData = context.getPublicApiData().get("culturalEventInfo");
        if (culturalData instanceof java.util.Map) {
            java.util.Map<String, Object> culturalMap = (java.util.Map<String, Object>) culturalData;
            Object rowData = culturalMap.get("row");
            
            if (rowData instanceof List) {
                List<java.util.Map<String, Object>> events = 
                        (List<java.util.Map<String, Object>>) rowData;
                
                return events.stream()
                        .map(this::mapToCulturalEventInfo)
                        .filter(event -> event != null)
                        .filter(event -> isWithinRadius(context, event))
                        .toList();
            }
        }
        
        return List.of();
    }
    
    /**
     * Map 데이터를 CulturalEventInfo로 변환합니다.
     * 
     * @param eventData 문화행사 데이터
     * @return CulturalEventInfo 객체
     */
    private CulturalEventInfo mapToCulturalEventInfo(java.util.Map<String, Object> eventData) {
        try {
            String title = getStringValue(eventData, "TITLE");
            String place = getStringValue(eventData, "PLACE");
            String startDate = getStringValue(eventData, "STRTDATE");
            String endDate = getStringValue(eventData, "END_DATE");
            String program = getStringValue(eventData, "PROGRAM");
            String isFree = getStringValue(eventData, "IS_FREE");
            double latitude = getDoubleValue(eventData, "LOT");
            double longitude = getDoubleValue(eventData, "LAT");
            
            return CulturalEventInfo.builder()
                    .title(title)
                    .place(place)
                    .startDate(startDate)
                    .endDate(endDate)
                    .program(program)
                    .isFree(isFree)
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
        } catch (Exception e) {
            log.warn("문화행사 데이터 파싱 실패: {}", eventData, e);
            return null;
        }
    }
    
    /**
     * 문화행사가 사용자 위치 반경 내에 있는지 확인합니다.
     * 
     * @param context 트리거 컨텍스트
     * @param event 문화행사 정보
     * @return 반경 내 여부
     */
    private boolean isWithinRadius(TriggerContext context, CulturalEventInfo event) {
        double distance = calculateDistance(
                context.getUserLatitude(), context.getUserLongitude(),
                event.latitude, event.longitude
        );
        
        event.distance = distance;
        return distance <= locationRadius;
    }
    
    /**
     * 행사가 오늘 시작하는지 확인합니다.
     * 
     * @param event 문화행사 정보
     * @return 오늘 시작 여부
     */
    private boolean isEventStartingToday(CulturalEventInfo event) {
        LocalDate startDate = parseDate(event.startDate);
        return startDate != null && startDate.equals(LocalDate.now());
    }
    
    /**
     * 행사가 곧 시작하는지 확인합니다 (설정된 일수 내).
     * 
     * @param event 문화행사 정보
     * @return 곧 시작 여부
     */
    private boolean isEventStartingSoon(CulturalEventInfo event) {
        LocalDate startDate = parseDate(event.startDate);
        if (startDate == null) return false;
        
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(daysAhead);
        
        return startDate.isAfter(today) && startDate.isBefore(targetDate.plusDays(1));
    }
    
    /**
     * 문자열을 LocalDate로 파싱합니다.
     * 
     * @param dateStr 날짜 문자열
     * @return LocalDate 객체
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr.trim(), formatter);
            } catch (DateTimeParseException e) {
                // 다음 포맷터 시도
            }
        }
        
        log.warn("날짜 파싱 실패: {}", dateStr);
        return null;
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
    
    /**
     * 오늘 시작하는 행사 메시지를 생성합니다.
     * 
     * @param event 문화행사 정보
     * @return 알림 메시지
     */
    private String buildEventStartMessage(CulturalEventInfo event) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("'%s' 행사가 오늘 시작됩니다! ", event.title));
        message.append(String.format("장소: %s ", event.place));
        
        if ("무료".equals(event.isFree) || "Y".equalsIgnoreCase(event.isFree)) {
            message.append("(무료) ");
        }
        
        message.append(String.format("거리: %.0fm", event.distance));
        
        return message.toString();
    }
    
    /**
     * 곧 시작할 행사 메시지를 생성합니다.
     * 
     * @param event 문화행사 정보
     * @return 알림 메시지
     */
    private String buildEventSoonMessage(CulturalEventInfo event) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("'%s' 행사가 %s에 시작됩니다. ", event.title, event.startDate));
        message.append(String.format("장소: %s ", event.place));
        
        if (event.program != null && !event.program.isEmpty()) {
            message.append(String.format("내용: %s ", event.program));
        }
        
        if ("무료".equals(event.isFree) || "Y".equalsIgnoreCase(event.isFree)) {
            message.append("(무료) ");
        }
        
        message.append(String.format("거리: %.0fm", event.distance));
        
        return message.toString();
    }
    
    private String getStringValue(java.util.Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
    
    private double getDoubleValue(java.util.Map<String, Object> map, String key) {
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
    
    @Override
    public String getSupportedTriggerType() {
        return "CULTURAL_EVENT";
    }
    
    @Override
    public int getPriority() {
        return 50; // 보통 우선순위
    }
    
    @Override
    public String getDescription() {
        return "사용자 주변의 새로운 문화행사나 관심있는 문화행사가 시작될 때 알림을 발송합니다.";
    }
    
    /**
     * 문화행사 정보를 담는 내부 클래스
     */
    @lombok.Builder
    private static class CulturalEventInfo {
        private final String title;
        private final String place;
        private final String startDate;
        private final String endDate;
        private final String program;
        private final String isFree;
        private final double latitude;
        private final double longitude;
        private double distance; // 사용자로부터의 거리
    }
}