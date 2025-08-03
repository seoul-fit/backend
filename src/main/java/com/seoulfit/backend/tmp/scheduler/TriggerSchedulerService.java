package com.seoulfit.backend.tmp.scheduler;

import com.seoulfit.backend.tmp.domain.port.UserInterestPort;
import com.seoulfit.backend.tmp.domain.port.UserPort;
import com.seoulfit.backend.event.NotificationEvent;
import com.seoulfit.backend.external.PublicDataApiClient;
import com.seoulfit.backend.trigger.TriggerManager;
import com.seoulfit.backend.trigger.dto.TriggerContext;
import com.seoulfit.backend.trigger.dto.TriggerResult;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 트리거 스케줄러 서비스
 * 
 * 주기적으로 공공 데이터를 조회하고 트리거 조건을 평가하여 알림을 발송하는 서비스
 * 오픈소스 확장성을 위해 설정 가능한 스케줄링 구조로 설계
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "urbanping.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class TriggerSchedulerService {
    
    private final TriggerManager triggerManager;
    private final PublicDataApiClient publicDataApiClient;
    private final UserPort userPort;
    private final UserInterestPort userInterestPort;
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * 실시간 트리거 평가 (5분마다 실행)
     * 
     * 주요 실시간 데이터를 기반으로 트리거 조건을 평가합니다.
     */
    @Scheduled(fixedRateString = "${urbanping.scheduler.realtime-interval:300000}")
    public void evaluateRealtimeTriggers() {
        log.info("실시간 트리거 평가 시작");
        
        try {
            // 모든 활성 사용자 조회
            List<User> activeUsers = userPort.findAllActiveUsers();
            log.debug("활성 사용자 수: {}", activeUsers.size());
            
            for (User user : activeUsers) {
                evaluateTriggersForUser(user);
            }
            
            log.info("실시간 트리거 평가 완료: 처리된 사용자 수={}", activeUsers.size());
            
        } catch (Exception e) {
            log.error("실시간 트리거 평가 중 오류 발생", e);
        }
    }
    
    /**
     * 문화행사 트리거 평가 (30분마다 실행)
     * 
     * 문화행사 정보를 기반으로 트리거 조건을 평가합니다.
     */
    @Scheduled(fixedRateString = "${urbanping.scheduler.cultural-interval:1800000}")
    public void evaluateCulturalEventTriggers() {
        log.info("문화행사 트리거 평가 시작");
        
        try {
            // 문화생활에 관심이 있는 사용자만 조회
            List<User> interestedUsers = userPort.findUsersByInterest(InterestCategory.CULTURE);
            log.debug("문화생활 관심 사용자 수: {}", interestedUsers.size());
            
            // 문화행사 데이터 조회
            publicDataApiClient.getCulturalEventData(1, 100)
                    .subscribe(
                            culturalData -> {
                                for (User user : interestedUsers) {
                                    evaluateTriggersForUser(user, culturalData);
                                }
                            },
                            error -> log.error("문화행사 데이터 조회 실패", error)
                    );
            
            log.info("문화행사 트리거 평가 완료");
            
        } catch (Exception e) {
            log.error("문화행사 트리거 평가 중 오류 발생", e);
        }
    }
    
    /**
     * 특정 사용자에 대한 트리거 평가
     * 
     * @param user 대상 사용자
     */
    private void evaluateTriggersForUser(User user) {
        try {
            // 사용자 관심사 조회
            List<InterestCategory> userInterests = userInterestPort.findInterestCategoriesByUser(user);
            
            if (userInterests.isEmpty()) {
                log.debug("사용자 관심사 없음: userId={}", user.getId());
                return;
            }
            
            // 실시간 도시 데이터 조회 (광화문·덕수궁 기본)
            publicDataApiClient.getCityData("광화문·덕수궁")
                    .subscribe(
                            cityData -> evaluateTriggersForUser(user, cityData),
                            error -> log.error("도시 데이터 조회 실패: userId={}", user.getId(), error)
                    );
            
        } catch (Exception e) {
            log.error("사용자 트리거 평가 중 오류 발생: userId={}", user.getId(), e);
        }
    }
    
    /**
     * 특정 사용자와 데이터에 대한 트리거 평가
     * 
     * @param user 대상 사용자
     * @param publicApiData 공공 API 데이터
     */
    private void evaluateTriggersForUser(User user, Map<String, Object> publicApiData) {
        try {
            // 사용자 관심사 조회
            List<InterestCategory> userInterests = userInterestPort.findInterestCategoriesByUser(user);
            
            // 트리거 컨텍스트 생성
            TriggerContext context = TriggerContext.builder()
                    .user(user)
                    .userInterests(userInterests)
                    .userLatitude(getUserLatitude(user)) // 사용자 위치 정보 (구현 필요)
                    .userLongitude(getUserLongitude(user)) // 사용자 위치 정보 (구현 필요)
                    .publicApiData(publicApiData)
                    .build();
            
            // 모든 트리거 전략 평가
            Optional<TriggerResult> triggerResult = triggerManager.evaluateAll(context);
            
            if (triggerResult.isPresent() && triggerResult.get().isTriggered()) {
                TriggerResult result = triggerResult.get();
                
                // 알림 이벤트 발행
                NotificationEvent event = NotificationEvent.builder()
                        .source(this)
                        .userId(user.getId())
                        .notificationType(result.getNotificationType())
                        .triggerCondition(result.getTriggerCondition())
                        .title(result.getTitle())
                        .message(result.getMessage())
                        .locationInfo(result.getLocationInfo())
                        .priority(result.getPriority())
                        .build();
                
                eventPublisher.publishEvent(event);
                
                log.info("트리거 발동 및 알림 이벤트 발행: userId={}, type={}, condition={}", 
                        user.getId(), result.getNotificationType(), result.getTriggerCondition());
            }
            
        } catch (Exception e) {
            log.error("사용자 트리거 평가 중 오류 발생: userId={}", user.getId(), e);
        }
    }
    
    /**
     * 사용자 위도 정보를 가져옵니다.
     * TODO: 실제 사용자 위치 정보 구현 필요
     * 
     * @param user 사용자
     * @return 위도
     */
    private Double getUserLatitude(User user) {
        // 임시로 광화문 위도 반환 (실제 구현 시 사용자 위치 정보 사용)
        return 37.5701416811;
    }
    
    /**
     * 사용자 경도 정보를 가져옵니다.
     * TODO: 실제 사용자 위치 정보 구현 필요
     * 
     * @param user 사용자
     * @return 경도
     */
    private Double getUserLongitude(User user) {
        // 임시로 광화문 경도 반환 (실제 구현 시 사용자 위치 정보 사용)
        return 126.9763534416;
    }
    
    /**
     * 스케줄러 상태 정보를 반환합니다.
     * 
     * @return 스케줄러 상태 정보
     */
    public Map<String, Object> getSchedulerStatus() {
        return Map.of(
                "enabledStrategiesCount", triggerManager.getEnabledStrategiesCount(),
                "strategies", triggerManager.getAllStrategiesInfo(),
                "lastExecutionTime", System.currentTimeMillis()
        );
    }
}
