package com.seoulfit.backend.trigger.application.service;

import com.seoulfit.backend.event.NotificationEvent;
import com.seoulfit.backend.publicdata.PublicDataApiClient;
import com.seoulfit.backend.trigger.TriggerManager;
import com.seoulfit.backend.trigger.adapter.in.web.dto.TriggerEvaluationResponse;
import com.seoulfit.backend.trigger.application.port.in.EvaluateTriggerUseCase;
import com.seoulfit.backend.trigger.application.port.in.dto.LocationTriggerCommand;
import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationResult;
import com.seoulfit.backend.trigger.domain.TriggerHistory;
import com.seoulfit.backend.trigger.dto.TriggerContext;
import com.seoulfit.backend.trigger.dto.TriggerResult;
import com.seoulfit.backend.trigger.infrastructure.TriggerHistoryRepository;
import com.seoulfit.backend.user.adapter.out.persistence.UserInterestPort;
import com.seoulfit.backend.user.adapter.out.persistence.UserPort;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 트리거 평가 서비스
 * <p>
 * 실시간 트리거 평가 및 관리를 담당하는 서비스
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TriggerEvaluationService implements EvaluateTriggerUseCase {

    private final TriggerManager triggerManager;
    private final PublicDataApiClient publicDataApiClient;
    private final UserPort userPort;
    private final UserInterestPort userInterestPort;
    private final TriggerHistoryRepository triggerHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public TriggerEvaluationResult evaluateLocationBasedTriggers(LocationTriggerCommand command) {
        log.info("위치 기반 트리거 평가 시작: userId={}, location=[{}, {}]",
                command.getUserId(), command.getLatitude(), command.getLongitude());

        // 사용자 조회
        User user = userPort.findById(Long.valueOf(command.getUserId()))
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + command.getUserId()));

        // 사용자 관심사 조회
        List<InterestCategory> userInterests = userInterestPort.findInterestsByUserId(user.getId());

        // 공공 데이터 조회
        Map<String, Object> publicApiData = fetchPublicApiData(command.getLatitude(), command.getLongitude());

        // 트리거 컨텍스트 생성
        TriggerContext context = TriggerContext.builder()
                .user(user)
                .userInterests(userInterests)
                .userLatitude(command.getLatitude())
                .userLongitude(command.getLongitude())
                .publicApiData(publicApiData)
                .currentTime(LocalDateTime.now())
                .build();

        // 트리거 평가 실행
        List<TriggerEvaluationResult.TriggeredInfo> triggeredList = new ArrayList<>();
        int totalEvaluated = 0;

        if (command.getTriggerTypes() == null || command.getTriggerTypes().isEmpty()) {
            // 모든 트리거 평가
            Optional<TriggerResult> result = triggerManager.evaluateAll(context);
            totalEvaluated = (int) triggerManager.getEnabledStrategiesCount();

            if (result.isPresent() && result.get().isTriggered()) {
                TriggerResult triggerResult = result.get();

                // 중복 알림 방지 체크
                if (!command.getForceEvaluation() && isRecentlyTriggered(user.getId(), "ALL", 30)) {
                    log.debug("최근 30분 내 동일 트리거 발동으로 스킵: userId={}", user.getId());
                } else {
                    triggeredList.add(createTriggeredInfo(triggerResult));
                    saveAndPublishTrigger(user, triggerResult, command);
                }
            }
        } else {
            // 특정 타입 트리거들 평가
            for (String triggerType : command.getTriggerTypes()) {
                Optional<TriggerResult> result = triggerManager.evaluateByType(context, triggerType);
                totalEvaluated++;

                if (result.isPresent() && result.get().isTriggered()) {
                    TriggerResult triggerResult = result.get();

                    // 중복 알림 방지 체크
                    if (!command.getForceEvaluation() && isRecentlyTriggered(user.getId(), triggerType, 30)) {
                        log.debug("최근 30분 내 동일 트리거 발동으로 스킵: userId={}, type={}", user.getId(), triggerType);
                        continue;
                    }

                    triggeredList.add(createTriggeredInfo(triggerResult));
                    saveAndPublishTrigger(user, triggerResult, command);
                }
            }
        }

        // 결과 생성
        TriggerEvaluationResult.LocationInfo locationInfo = TriggerEvaluationResult.LocationInfo.builder()
                .latitude(command.getLatitude())
                .longitude(command.getLongitude())
                .build();

        if (triggeredList.isEmpty()) {
            return TriggerEvaluationResult.notTriggered(totalEvaluated, locationInfo);
        } else {
            return TriggerEvaluationResult.triggered(triggeredList, totalEvaluated, locationInfo);
        }
    }

    @Override
    public List<TriggerEvaluationResponse> getTriggerHistory(String userId, int page, int size) {
        User user = userPort.findByEmail(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        List<TriggerHistory> histories = triggerHistoryRepository.findByUserIdOrderByTriggeredAtDesc(
                user.getId(), page * size, size);

        return histories.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * 공공 API 데이터 조회
     */
    private Map<String, Object> fetchPublicApiData(Double latitude, Double longitude) {
        try {
            // 실시간 공공 데이터 조회 (예: 날씨, 대기질, 따릉이 등)
            return publicDataApiClient.fetchRealtimeData(latitude, longitude);
        } catch (Exception e) {
            log.warn("공공 API 데이터 조회 실패: lat={}, lng={}, error={}", latitude, longitude, e.getMessage());
            return Map.of();
        }
    }

    /**
     * 최근 트리거 발동 여부 확인
     */
    private boolean isRecentlyTriggered(Long userId, String triggerType, int minutes) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes);
        return triggerHistoryRepository.existsByUserIdAndTriggerTypeAndTriggeredAtAfter(
                userId, triggerType, since);
    }

    /**
     * TriggerResult를 TriggeredInfo로 변환
     */
    private TriggerEvaluationResult.TriggeredInfo createTriggeredInfo(TriggerResult result) {
        return TriggerEvaluationResult.TriggeredInfo.builder()
                .triggerType(result.getTriggerCondition().name())
                .title(result.getTitle())
                .message(result.getMessage())
                .priority(result.getPriority())
                .locationInfo(result.getLocationInfo())
                .triggeredTime(LocalDateTime.now())
                .build();
    }

    /**
     * 트리거 히스토리 저장 및 알림 이벤트 발행
     */
    @Transactional
    private void saveAndPublishTrigger(User user, TriggerResult result, LocationTriggerCommand command) {
        // 트리거 히스토리 저장
        TriggerHistory history = TriggerHistory.fromRealtime(
                user.getId(),
                result.getTriggerCondition().name(),
                result.getNotificationType(),
                result.getTriggerCondition(),
                result.getTitle(),
                result.getMessage(),
                result.getLocationInfo(),
                command.getLatitude(),
                command.getLongitude(),
                result.getPriority(),
                Map.of("source", "REALTIME_LOCATION")
        );
        triggerHistoryRepository.save(history);

        // 알림 이벤트 발행
        NotificationEvent event = NotificationEvent.builder()
                .userId(user.getId())
                .type(result.getNotificationType())
                .triggerCondition(result.getTriggerCondition())
                .title(result.getTitle())
                .message(result.getMessage())
                .locationInfo(result.getLocationInfo())
                .priority(result.getPriority())
                .build();
        eventPublisher.publishEvent(event);

        log.info("트리거 발동 및 알림 발송: userId={}, type={}, title={}",
                user.getId(), result.getTriggerCondition(), result.getTitle());
    }

    /**
     * TriggerHistory를 TriggerEvaluationResponse로 변환
     */
    private TriggerEvaluationResponse convertToResponse(TriggerHistory history) {
        TriggerEvaluationResponse.TriggeredInfo triggeredInfo =
                TriggerEvaluationResponse.TriggeredInfo.builder()
                        .triggerType(history.getTriggerType())
                        .title(history.getTitle())
                        .message(history.getMessage())
                        .priority(history.getPriority())
                        .locationInfo(history.getLocationInfo())
                        .triggeredTime(history.getTriggeredAt())
                        .build();

        TriggerEvaluationResponse.LocationInfo locationInfo = null;
        if (history.getLatitude() != null && history.getLongitude() != null) {
            locationInfo = TriggerEvaluationResponse.LocationInfo.builder()
                    .latitude(history.getLatitude())
                    .longitude(history.getLongitude())
                    .build();
        }

        return TriggerEvaluationResponse.builder()
                .triggered(true)
                .triggeredCount(1)
                .totalEvaluated(1)
                .triggeredList(List.of(triggeredInfo))
                .evaluationTime(history.getTriggeredAt())
                .locationInfo(locationInfo)
                .build();
    }
}
