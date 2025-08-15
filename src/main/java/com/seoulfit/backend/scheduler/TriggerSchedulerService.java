package com.seoulfit.backend.scheduler;

import com.seoulfit.backend.user.adapter.out.persistence.UserInterestPort;
import com.seoulfit.backend.user.adapter.out.persistence.UserPort;
import com.seoulfit.backend.notification.domain.NotificationEvent;
import com.seoulfit.backend.publicdata.PublicDataApiClient;
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
 * 트리거 스케줄러 서비스 클래스입니다.
 * 
 * <p>주기적으로 공공 데이터를 조회하고 트리거 조건을 평가하여 사용자에게 알림을 발송하는 
 * 핵심 스케줄링 서비스입니다. 오픈소스 확장성을 고려하여 설정 가능한 스케줄링 구조로 설계되었습니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>실시간 트리거 평가 (5분 간격)</li>
 *   <li>문화행사 트리거 평가 (30분 간격)</li>
 *   <li>사용자별 맞춤 트리거 조건 평가</li>
 *   <li>알림 이벤트 발행 및 처리</li>
 *   <li>스케줄러 상태 모니터링</li>
 * </ul>
 * 
 * <p>이 서비스는 Spring의 @Scheduled 어노테이션을 사용하여 주기적으로 실행되며,
 * 설정을 통해 활성화/비활성화할 수 있습니다. 각 사용자의 관심사와 위치 정보를 고려하여
 * 개인화된 알림을 제공합니다.</p>
 * 
 * @author Seoul Fit
 * @version 1.0.0
 * @since 2025-01-01
 * @see TriggerManager
 * @see PublicDataApiClient
 * @see NotificationEvent
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "urbanping.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class TriggerSchedulerService {
    
    /**
     * 트리거 관리자입니다.
     * 등록된 모든 트리거 전략을 관리하고 실행합니다.
     */
    private final TriggerManager triggerManager;
    
    /**
     * 공공 데이터 API 클라이언트입니다.
     * 서울시 공공 데이터를 조회하는 데 사용됩니다.
     */
    private final PublicDataApiClient publicDataApiClient;
    
    /**
     * 사용자 데이터 접근 포트입니다.
     */
    private final UserPort userPort;
    
    /**
     * 사용자 관심사 데이터 접근 포트입니다.
     */
    private final UserInterestPort userInterestPort;
    
    /**
     * 애플리케이션 이벤트 발행자입니다.
     * 알림 이벤트를 발행하는 데 사용됩니다.
     */
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * 실시간 트리거를 평가합니다.
     * 
     * <p>5분마다 실행되며, 모든 활성 사용자에 대해 실시간 도시 데이터를 기반으로
     * 트리거 조건을 평가합니다. 실행 간격은 설정을 통해 변경할 수 있습니다.</p>
     * 
     * <p>평가 대상:</p>
     * <ul>
     *   <li>날씨 관련 트리거 (폭염, 한파, 대기질)</li>
     *   <li>따릉이 현황 트리거</li>
     *   <li>인구 혼잡도 트리거</li>
     * </ul>
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
     * 문화행사 트리거를 평가합니다.
     * 
     * <p>30분마다 실행되며, 문화생활에 관심이 있는 사용자들을 대상으로
     * 문화행사 정보를 기반으로 트리거 조건을 평가합니다.</p>
     * 
     * <p>평가 대상:</p>
     * <ul>
     *   <li>새로운 문화행사 등록</li>
     *   <li>관심 있는 문화행사 시작 임박</li>
     *   <li>위치 기반 문화행사 추천</li>
     * </ul>
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
     * 특정 사용자에 대한 트리거를 평가합니다.
     * 
     * <p>사용자의 관심사를 조회하고, 실시간 도시 데이터를 기반으로
     * 해당 사용자에게 적용 가능한 트리거 조건들을 평가합니다.</p>
     * 
     * @param user 트리거를 평가할 대상 사용자
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
     * 특정 사용자와 공공 데이터에 대한 트리거를 평가합니다.
     * 
     * <p>사용자의 관심사와 위치 정보, 그리고 공공 API 데이터를 종합하여
     * 트리거 컨텍스트를 생성하고 모든 트리거 전략을 평가합니다.
     * 트리거가 발동되면 알림 이벤트를 발행합니다.</p>
     * 
     * @param user 트리거를 평가할 대상 사용자
     * @param publicApiData 평가에 사용할 공공 API 데이터
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
                        .type(result.getNotificationType())
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
     * 사용자의 위도 정보를 가져옵니다.
     * 
     * <p>현재는 임시로 광화문 위도를 반환하며, 
     * 실제 구현 시에는 사용자의 실제 위치 정보를 사용해야 합니다.</p>
     * 
     * @param user 위치 정보를 조회할 사용자
     * @return 사용자의 위도 (현재는 광화문 위도)
     * @todo 실제 사용자 위치 정보 구현 필요
     */
    private Double getUserLatitude(User user) {
        // 임시로 광화문 위도 반환 (실제 구현 시 사용자 위치 정보 사용)
        return 37.5701416811;
    }
    
    /**
     * 사용자의 경도 정보를 가져옵니다.
     * 
     * <p>현재는 임시로 광화문 경도를 반환하며, 
     * 실제 구현 시에는 사용자의 실제 위치 정보를 사용해야 합니다.</p>
     * 
     * @param user 위치 정보를 조회할 사용자
     * @return 사용자의 경도 (현재는 광화문 경도)
     * @todo 실제 사용자 위치 정보 구현 필요
     */
    private Double getUserLongitude(User user) {
        // 임시로 광화문 경도 반환 (실제 구현 시 사용자 위치 정보 사용)
        return 126.9763534416;
    }
    
    /**
     * 스케줄러의 현재 상태 정보를 반환합니다.
     * 
     * <p>활성화된 트리거 전략 수, 전략 목록, 마지막 실행 시간 등의
     * 모니터링 정보를 제공합니다. 관리자 대시보드나 헬스체크 용도로 사용됩니다.</p>
     * 
     * @return 스케줄러 상태 정보를 포함한 Map
     */
    public Map<String, Object> getSchedulerStatus() {
        return Map.of(
                "enabledStrategiesCount", triggerManager.getEnabledStrategiesCount(),
                "strategies", triggerManager.getAllStrategiesInfo(),
                "lastExecutionTime", System.currentTimeMillis()
        );
    }
}
