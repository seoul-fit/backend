package com.seoulfit.backend.trigger.strategy.impl;

import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.publicdata.culture.adapter.out.custom.CulturalEventRepository;
import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import com.seoulfit.backend.trigger.dto.TriggerContext;
import com.seoulfit.backend.trigger.dto.TriggerResult;
import com.seoulfit.backend.trigger.infrastructure.TriggerHistoryRepository;
import com.seoulfit.backend.trigger.strategy.TriggerStrategy;
import com.seoulfit.backend.user.domain.InterestCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 문화행사 기반 트리거 전략
 * 
 * 사용자 위치 주변의 문화행사 정보를 데이터베이스에서 조회하여
 * 현재 진행 중이거나 곧 시작될 문화행사에 대한 알림을 발송하는 전략
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CulturalEventTriggerStrategy implements TriggerStrategy {
    
    private final CulturalEventRepository culturalEventRepository;
    private final TriggerHistoryRepository triggerHistoryRepository;
    
    @Value("${urbanping.trigger.cultural-event.search-radius:2.0}")
    private double searchRadius; // 검색 반경 (기본값: 2km)
    
    @Value("${urbanping.trigger.cultural-event.upcoming-days:3}")
    private int upcomingDays; // 앞으로 며칠 내의 행사를 알림할지 (기본값: 3일)
    
    @Override
    public TriggerResult evaluate(TriggerContext context) {
        log.debug("문화행사 기반 트리거 평가 시작: userId={}, location=[{}, {}]", 
                context.getUser().getId(), context.getUserLatitude(), context.getUserLongitude());
        
        // 사용자가 문화시설에 관심이 있는지 확인
        if (!context.getUserInterests().contains(InterestCategory.CULTURE)) {
            log.debug("사용자가 문화시설에 관심이 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        // 위치 정보가 있는지 확인
        if (context.getUserLatitude() == null || context.getUserLongitude() == null) {
            log.debug("사용자 위치 정보 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        // 주변 문화행사 정보 조회
        List<CulturalEvent> nearbyEvents = findNearbyEvents(
                context.getUserLatitude(), 
                context.getUserLongitude()
        );
        
        if (nearbyEvents.isEmpty()) {
            log.debug("주변 문화행사 없음: userId={}", context.getUser().getId());
            return TriggerResult.notTriggered();
        }
        
        log.debug("주변 문화행사 {}개 발견: userId={}", nearbyEvents.size(), context.getUser().getId());
        
        LocalDate today = LocalDate.now();
        LocalDate upcomingLimit = today.plusDays(upcomingDays);
        
        // 현재 진행 중인 문화행사 체크 (중복 알림 방지 포함)
        CulturalEvent ongoingEvent = findOngoingEventWithoutDuplicate(nearbyEvents, today, context.getUser().getId());
        if (ongoingEvent != null) {
            Map<String, String> metadata = createEventMetadata(ongoingEvent);
            return TriggerResult.builder()
                    .triggered(true)
                    .notificationType(NotificationType.CULTURE)
                    .triggerCondition(TriggerCondition.CULTURAL_EVENT)
                    .title("진행 중인 문화행사")
                    .message(String.format("주변에서 '%s' 문화행사가 진행 중입니다. %s에서 만나보세요!", 
                            ongoingEvent.getTitle(), ongoingEvent.getPlace()))
                    .locationInfo(String.format("위도: %.6f, 경도: %.6f, 구: %s", 
                            ongoingEvent.getLatitude(), ongoingEvent.getLongitude(), ongoingEvent.getDistrict()))
                    .priority(30)
                    .additionalData(Map.of("metadata", metadata))
                    .build();
        }
        
        // 곧 시작될 문화행사 체크 (중복 알림 방지 포함)
        CulturalEvent upcomingEvent = findUpcomingEventWithoutDuplicate(nearbyEvents, today, upcomingLimit, context.getUser().getId());
        if (upcomingEvent != null) {
            long daysUntilStart = java.time.temporal.ChronoUnit.DAYS.between(today, upcomingEvent.getStartDate());
            Map<String, String> metadata = createEventMetadata(upcomingEvent);
            return TriggerResult.builder()
                    .triggered(true)
                    .notificationType(NotificationType.CULTURE)
                    .triggerCondition(TriggerCondition.CULTURAL_EVENT_START)
                    .title("곧 시작될 문화행사")
                    .message(String.format("'%s' 문화행사가 %d일 후 시작됩니다. %s에서 진행되니 놓치지 마세요!%s", 
                            upcomingEvent.getTitle(), 
                            daysUntilStart,
                            upcomingEvent.getPlace(),
                            upcomingEvent.isFreeEvent() ? " (무료)" : ""))
                    .locationInfo(String.format("위도: %.6f, 경도: %.6f, 구: %s", 
                            upcomingEvent.getLatitude(), upcomingEvent.getLongitude(), upcomingEvent.getDistrict()))
                    .priority(40)
                    .additionalData(Map.of("metadata", metadata))
                    .build();
        }
        
        log.debug("문화행사 기반 트리거 조건 미충족: userId={}", context.getUser().getId());
        return TriggerResult.notTriggered();
    }
    
    /**
     * 사용자 위치 주변의 문화행사를 데이터베이스에서 조회합니다.
     * 
     * @param latitude 사용자 위도
     * @param longitude 사용자 경도
     * @return 주변 문화행사 목록
     */
    private List<CulturalEvent> findNearbyEvents(Double latitude, Double longitude) {
        try {
            BigDecimal lat = BigDecimal.valueOf(latitude);
            BigDecimal lng = BigDecimal.valueOf(longitude);
            
            List<CulturalEvent> events = culturalEventRepository.findWithInRadius(lat, lng, searchRadius);
            log.debug("사용자 위치 기준 {}km 반경 내 문화행사: {}건", searchRadius, events.size());
            
            return events;
        } catch (Exception e) {
            log.error("주변 문화행사 조회 중 오류 발생: lat={}, lng={}", latitude, longitude, e);
            return List.of();
        }
    }
    
    /**
     * 현재 진행 중인 문화행사를 중복 알림 없이 찾습니다.
     * 
     * @param events 문화행사 목록
     * @param currentDate 현재 날짜
     * @param userId 사용자 ID
     * @return 진행 중인 문화행사 (중복이 아닌 경우만, 없으면 null)
     */
    private CulturalEvent findOngoingEventWithoutDuplicate(List<CulturalEvent> events, LocalDate currentDate, Long userId) {
        return events.stream()
                .filter(event -> event.isOngoing(currentDate))
                .filter(event -> event.getLatitude() != null && event.getLongitude() != null)
                .filter(event -> !isAlreadyNotified(userId, String.valueOf(event.getId())))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 곧 시작될 문화행사를 중복 알림 없이 찾습니다.
     * 
     * @param events 문화행사 목록
     * @param currentDate 현재 날짜
     * @param upcomingLimit 예정 행사 검색 한계 날짜
     * @param userId 사용자 ID
     * @return 예정된 문화행사 (중복이 아닌 경우만, 없으면 null)
     */
    private CulturalEvent findUpcomingEventWithoutDuplicate(List<CulturalEvent> events, LocalDate currentDate, 
                                                           LocalDate upcomingLimit, Long userId) {
        return events.stream()
                .filter(event -> event.isUpcoming(currentDate))
                .filter(event -> event.getStartDate() != null && !event.getStartDate().isAfter(upcomingLimit))
                .filter(event -> event.getLatitude() != null && event.getLongitude() != null)
                .filter(event -> !isAlreadyNotified(userId, String.valueOf(event.getId())))
                .sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate())) // 가장 빠른 행사부터
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 사용자가 특정 문화행사에 대한 알림을 이미 받았는지 확인합니다.
     * 
     * @param userId 사용자 ID
     * @param culturalEventId 문화행사 ID
     * @return 이미 알림을 받았으면 true, 아니면 false
     */
    private boolean isAlreadyNotified(Long userId, String culturalEventId) {
        try {
            boolean alreadyNotified = triggerHistoryRepository.existsByUserIdAndCulturalEventId(userId, culturalEventId);
            if (alreadyNotified) {
                log.debug("문화행사 중복 알림 방지: userId={}, eventId={}", userId, culturalEventId);
            }
            return alreadyNotified;
        } catch (Exception e) {
            log.warn("문화행사 중복 확인 중 오류 발생: userId={}, eventId={}", userId, culturalEventId, e);
            return false; // 오류 시 알림 허용 (안전한 방향)
        }
    }
    
    /**
     * 문화행사의 메타데이터를 생성합니다.
     * 
     * @param event 문화행사
     * @return 메타데이터 맵
     */
    private Map<String, String> createEventMetadata(CulturalEvent event) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("cultural_event_id", String.valueOf(event.getId()));
        metadata.put("event_title", event.getTitle());
        metadata.put("event_district", event.getDistrict());
        metadata.put("source", "DATABASE");
        return metadata;
    }
    
    @Override
    public String getSupportedTriggerType() {
        return "CULTURAL_EVENT";
    }
    
    @Override
    public int getPriority() {
        return 40; // 중간 우선순위
    }
    
    @Override
    public String getDescription() {
        return "사용자 위치 주변의 문화행사 정보를 데이터베이스에서 조회하여 진행 중이거나 곧 시작될 문화행사를 알림합니다.";
    }
}