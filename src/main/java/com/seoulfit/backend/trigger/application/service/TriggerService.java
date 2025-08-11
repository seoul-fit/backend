package com.seoulfit.backend.trigger.application.service;

import com.seoulfit.backend.trigger.application.port.in.ManageTriggerUseCase;
import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationCommand;
import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationResult;
import com.seoulfit.backend.trigger.application.port.out.PublicDataPort;
import com.seoulfit.backend.trigger.domain.TriggerStrategy;
import com.seoulfit.backend.user.application.port.out.UserPort;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import java.util.ArrayList;
import java.util.List;
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
        List<TriggerEvaluationResult> results = new ArrayList<>();

        for (TriggerStrategy strategy : triggerStrategies) {
            try {
                // TODO: TriggerContext 생성 및 전략 실행 로직 구현
                // TriggerContext context = createTriggerContext(command);
                // TriggerResult result = strategy.evaluate(context);
                // results.add(convertToEvaluationResult(command.getUserId(), result));

                log.debug("트리거 전략 실행: strategy={}, userId={}",
                        strategy.getClass().getSimpleName(), command.userId());
            } catch (Exception e) {
                log.error("트리거 평가 중 오류 발생: strategy={}, userId={}, error={}",
                        strategy.getClass().getSimpleName(), command.userId(), e.getMessage());
            }
        }

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
}
