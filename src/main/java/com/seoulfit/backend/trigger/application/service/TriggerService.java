package com.seoulfit.backend.trigger.application.service;

import com.seoulfit.backend.trigger.application.port.in.ManageTriggerUseCase;
import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationCommand;
import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationResult;
import com.seoulfit.backend.trigger.application.port.out.PublicDataPort;
import com.seoulfit.backend.trigger.strategy.TriggerStrategy;
import com.seoulfit.backend.trigger.dto.TriggerContext;
import com.seoulfit.backend.trigger.dto.TriggerResult;
import com.seoulfit.backend.user.application.port.out.UserPort;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 트리거 서비스
 * <p>
 * 헥사고날 아키텍처의 애플리케이션 서비스 트리거 평가 및 알림 발송과 관련된 비즈니스 로직을 처리
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TriggerService implements ManageTriggerUseCase {

    private final List<TriggerStrategy> triggerStrategies;
    private final UserPort userPort;
    private final PublicDataPort publicDataPort;

    @Override
    public List<TriggerEvaluationResult> evaluateAllTriggers() {
        List<TriggerEvaluationResult> results = new ArrayList<>();
        List<User> activeUsers = userPort.findAllActiveUsers();

        for (User user : activeUsers) {
            TriggerEvaluationCommand command = TriggerEvaluationCommand.of(
                    user.getId(),
                    user.getInterestCategories(),
                    user.getLocationLatitude(),
                    user.getLocationLongitude(),
                    user.getLocationAddress()
            );
            results.addAll(evaluateTriggersForUser(command));
        }

        log.info("전체 트리거 평가 완료: 사용자 수={}, 결과 수={}", activeUsers.size(), results.size());
        return results;
    }

    @Override
    public List<TriggerEvaluationResult> evaluateTriggersForUser(TriggerEvaluationCommand command) {
        List<TriggerEvaluationResult.TriggeredInfo> triggeredInfos = new ArrayList<>();

        // 1. 사용자 정보 검증
        User user = userPort.findById(command.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + command.getUserId()));

        // 2. 공공데이터 조회 (안전한 방식으로)
        Map<String, Object> publicData = fetchPublicDataSafely(command);

        // 3. TriggerContext 생성
        TriggerContext context = createTriggerContext(command, user, publicData);

        // 4. 각 전략 실행 (우선순위 순으로 정렬)
        List<TriggerStrategy> sortedStrategies = triggerStrategies.stream()
            .filter(TriggerStrategy::isEnabled)
            .sorted((s1, s2) -> Integer.compare(s1.getPriority(), s2.getPriority()))
            .toList();

        for (TriggerStrategy strategy : sortedStrategies) {
            try {
                log.debug("트리거 전략 실행: strategy={}, userId={}, priority={}", 
                    strategy.getClass().getSimpleName(), command.getUserId(), strategy.getPriority());
                
                TriggerResult result = strategy.evaluate(context);
                
                if (result != null && result.isTriggered()) {
                    TriggerEvaluationResult.TriggeredInfo triggeredInfo = convertToTriggeredInfo(result, strategy);
                    triggeredInfos.add(triggeredInfo);
                    
                    log.info("트리거 발동: strategy={}, userId={}, condition={}", 
                        strategy.getClass().getSimpleName(), command.getUserId(), result.getTriggerCondition());
                }
                
            } catch (Exception e) {
                log.error("트리거 평가 중 오류 발생: strategy={}, userId={}, error={}", 
                    strategy.getClass().getSimpleName(), command.getUserId(), e.getMessage(), e);
            }
        }

        // 5. 결과 생성
        TriggerEvaluationResult.LocationInfo locationInfo = TriggerEvaluationResult.LocationInfo.builder()
                .latitude(command.getLatitude())
                .longitude(command.getLongitude())
                .address(command.getUserAddress())
                .build();

        List<TriggerEvaluationResult> results = new ArrayList<>();
        if (!triggeredInfos.isEmpty()) {
            results.add(TriggerEvaluationResult.triggered(triggeredInfos, sortedStrategies.size(), locationInfo));
        } else {
            results.add(TriggerEvaluationResult.notTriggered(sortedStrategies.size(), locationInfo));
        }

        log.info("사용자 트리거 평가 완료: userId={}, 평가된 전략 수={}, 발동된 트리거 수={}", 
            command.getUserId(), sortedStrategies.size(), triggeredInfos.size());
        
        return results;
    }

    @Override
    public List<TriggerEvaluationResult> evaluateRealtimeTriggers() {
        List<TriggerEvaluationResult> results = new ArrayList<>();
        List<User> interestedUsers = userPort.findUsersByInterest(InterestCategory.WEATHER);

        for (User user : interestedUsers) {
            TriggerEvaluationCommand command = TriggerEvaluationCommand.of(
                    user.getId(),
                    user.getInterestCategories(),
                    user.getLocationLatitude(),
                    user.getLocationLongitude(),
                    user.getLocationAddress()
            );

            // 실시간 트리거만 실행 (온도, 대기질, 따릉이 등)
            results.addAll(evaluateTriggersForUser(command));
        }

        log.info("실시간 트리거 평가 완료: 대상 사용자 수={}", interestedUsers.size());
        return results;
    }

    @Override
    public List<TriggerEvaluationResult> evaluateCulturalEventTriggers() {
        List<TriggerEvaluationResult> results = new ArrayList<>();
        List<User> interestedUsers = userPort.findUsersByInterest(InterestCategory.CULTURE);

        for (User user : interestedUsers) {
            TriggerEvaluationCommand command = TriggerEvaluationCommand.of(
                    user.getId(),
                    user.getInterestCategories(),
                    user.getLocationLatitude(),
                    user.getLocationLongitude(),
                    user.getLocationAddress()
            );

            // 문화행사 트리거만 실행
            results.addAll(evaluateTriggersForUser(command));
        }

        log.info("문화행사 트리거 평가 완료: 대상 사용자 수={}", interestedUsers.size());
        return results;
    }
    
    /**
     * 공공데이터를 안전하게 조회합니다.
     * 
     * @param command 트리거 평가 명령
     * @return 공공데이터 맵
     */
    private Map<String, Object> fetchPublicDataSafely(TriggerEvaluationCommand command) {
        try {
            // 여러 공공데이터 소스를 통합
            Map<String, Object> cityData = publicDataPort.getCityData(
                command.getUserAddress() != null ? command.getUserAddress() : "광화문");
            Map<String, Object> bikeData = publicDataPort.getBikeShareData();
            Map<String, Object> airData = publicDataPort.getAirQualityData();
            Map<String, Object> rainData = publicDataPort.getRainfallData();
            // 문화행사 데이터는 데이터베이스에서 직접 조회하므로 제거
            
            // 데이터 통합
            java.util.Map<String, Object> combinedData = new java.util.HashMap<>();
            if (cityData != null) combinedData.putAll(cityData);
            if (bikeData != null) combinedData.put("BIKE_SHARE", bikeData); // 수정된 키명 사용
            if (airData != null) combinedData.put("WEATHER_STTS", airData);
            if (rainData != null) combinedData.put("rainInfo", rainData);
            // 문화행사 데이터는 CulturalEventTriggerStrategy에서 데이터베이스에서 직접 조회
            
            return combinedData;
        } catch (Exception e) {
            log.warn("공공데이터 조회 실패: userId={}, error={}", command.getUserId(), e.getMessage());
            return Map.of(); // 빈 맵 반환
        }
    }
    
    /**
     * TriggerContext를 생성합니다.
     * 
     * @param command 트리거 평가 명령
     * @param user 사용자 정보
     * @param publicData 공공데이터
     * @return 트리거 컨텍스트
     */
    private TriggerContext createTriggerContext(TriggerEvaluationCommand command, User user, Map<String, Object> publicData) {
        return TriggerContext.builder()
            .user(user)
            .userInterests(command.getUserInterests())
            .userLatitude(command.getLatitude())
            .userLongitude(command.getLongitude())
            .publicApiData(publicData)
            .currentTime(LocalDateTime.now())
            .build();
    }
    
    /**
     * TriggerResult를 TriggeredInfo로 변환합니다.
     * 
     * @param result 트리거 결과
     * @param strategy 트리거 전략
     * @return 발동된 트리거 정보
     */
    private TriggerEvaluationResult.TriggeredInfo convertToTriggeredInfo(TriggerResult result, TriggerStrategy strategy) {
        return TriggerEvaluationResult.TriggeredInfo.builder()
            .triggerType(strategy.getSupportedTriggerType())
            .title(result.getTitle())
            .message(result.getMessage())
            .priority(result.getPriority())
            .locationInfo(result.getLocationInfo())
            .triggeredTime(LocalDateTime.now())
            .build();
    }
}
