package com.seoulfit.backend.trigger.application.service;

import com.seoulfit.backend.trigger.domain.NotificationDuplicationPolicy;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import com.seoulfit.backend.trigger.dto.TriggerResult;
import com.seoulfit.backend.trigger.infrastructure.TriggerHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 알림 중복 방지 서비스
 * 
 * 다양한 트리거 조건에 대한 중복 알림 방지 로직을 관리
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDuplicationService {
    
    private final TriggerHistoryRepository triggerHistoryRepository;
    
    /**
     * 알림 중복 여부를 확인합니다.
     * 
     * @param userId 사용자 ID
     * @param triggerResult 트리거 결과
     * @param userLatitude 사용자 위도
     * @param userLongitude 사용자 경도
     * @return 중복이면 true, 새로운 알림이면 false
     */
    public boolean isDuplicateNotification(Long userId, TriggerResult triggerResult, 
                                         Double userLatitude, Double userLongitude) {
        TriggerCondition condition = triggerResult.getTriggerCondition();
        NotificationDuplicationPolicy policy = NotificationDuplicationPolicy.fromTriggerCondition(condition);
        
        // 중복 방지가 필요하지 않은 경우
        if (!policy.isPreventionRequired()) {
            log.debug("중복 방지 불필요: userId={}, condition={}", userId, condition);
            return false;
        }
        
        try {
            return checkDuplicationByPolicy(userId, triggerResult, userLatitude, userLongitude, policy);
        } catch (Exception e) {
            log.warn("중복 확인 중 오류 발생: userId={}, condition={}, error={}", 
                    userId, condition, e.getMessage());
            // 오류 시 중복이 아닌 것으로 간주 (사용자 경험 우선)
            return false;
        }
    }
    
    /**
     * 정책에 따라 중복 여부를 확인합니다.
     */
    private boolean checkDuplicationByPolicy(Long userId, TriggerResult triggerResult, 
                                           Double userLatitude, Double userLongitude,
                                           NotificationDuplicationPolicy policy) {
        TriggerCondition condition = triggerResult.getTriggerCondition();
        LocalDateTime since = LocalDateTime.now().minus(policy.getPreventionDuration());
        
        if (policy.isUniqueIdentifierBased()) {
            return checkUniqueIdentifierDuplication(userId, triggerResult, policy, since);
            
        } else if (policy.isLocationBased()) {
            return checkLocationBasedDuplication(userId, condition, userLatitude, userLongitude, policy, since);
            
        } else if (policy.isConditionBased()) {
            return checkConditionBasedDuplication(userId, condition, since);
            
        } else {
            return false;
        }
    }
    
    /**
     * 고유 식별자 기반 중복 확인
     */
    private boolean checkUniqueIdentifierDuplication(Long userId, TriggerResult triggerResult,
                                                    NotificationDuplicationPolicy policy, LocalDateTime since) {
        String identifierKey = policy.getUniqueIdentifierKey();
        if (identifierKey == null) {
            log.warn("고유 식별자 키가 없음: policy={}", policy.name());
            return false;
        }
        
        String identifierValue = extractIdentifierValue(triggerResult, identifierKey);
        if (identifierValue == null) {
            log.debug("고유 식별자 값을 찾을 수 없음: userId={}, key={}", userId, identifierKey);
            return false;
        }
        
        TriggerCondition condition = triggerResult.getTriggerCondition();
        
        // 영구 중복 방지 (Duration이 1년 이상인 경우)
        if (policy.getPreventionDuration().toDays() >= 365) {
            boolean isDuplicate = triggerHistoryRepository.existsByUserIdAndUniqueIdentifier(
                    userId, condition, identifierKey, identifierValue);
            if (isDuplicate) {
                log.debug("영구 중복 알림 방지: userId={}, condition={}, {}={}", 
                        userId, condition, identifierKey, identifierValue);
            }
            return isDuplicate;
        } else {
            // 시간 제한 중복 방지
            boolean isDuplicate = triggerHistoryRepository.existsByUserIdAndUniqueIdentifierSince(
                    userId, condition, identifierKey, identifierValue, since);
            if (isDuplicate) {
                log.debug("시간 제한 중복 알림 방지: userId={}, condition={}, {}={}, duration={}", 
                        userId, condition, identifierKey, identifierValue, policy.getPreventionDuration());
            }
            return isDuplicate;
        }
    }
    
    /**
     * 위치 기반 중복 확인
     */
    private boolean checkLocationBasedDuplication(Long userId, TriggerCondition condition,
                                                 Double userLatitude, Double userLongitude,
                                                 NotificationDuplicationPolicy policy, LocalDateTime since) {
        if (userLatitude == null || userLongitude == null) {
            return false;
        }
        
        // 위치 기반 중복 확인 (반경 500m 내)
        double radiusKm = 0.5; // 500m
        boolean isDuplicate = triggerHistoryRepository.existsByUserIdAndLocationSince(
                userId, condition, userLatitude, userLongitude, radiusKm, since);
        
        if (isDuplicate) {
            log.debug("위치 기반 중복 알림 방지: userId={}, condition={}, location=[{}, {}], duration={}", 
                    userId, condition, userLatitude, userLongitude, policy.getPreventionDuration());
        }
        
        return isDuplicate;
    }
    
    /**
     * 조건 기반 중복 확인
     */
    private boolean checkConditionBasedDuplication(Long userId, TriggerCondition condition, LocalDateTime since) {
        boolean isDuplicate = triggerHistoryRepository.existsByUserIdAndTriggerConditionSince(
                userId, condition, since);
        
        if (isDuplicate) {
            log.debug("조건 기반 중복 알림 방지: userId={}, condition={}, since={}", 
                    userId, condition, since);
        }
        
        return isDuplicate;
    }
    
    /**
     * TriggerResult의 메타데이터에서 고유 식별자 값을 추출합니다.
     */
    private String extractIdentifierValue(TriggerResult triggerResult, String identifierKey) {
        if (triggerResult.getAdditionalData() == null) {
            return null;
        }
        
        Object metadataObj = triggerResult.getAdditionalData().get("metadata");
        if (!(metadataObj instanceof Map)) {
            return null;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, String> metadata = (Map<String, String>) metadataObj;
        return metadata.get(identifierKey);
    }
    
    /**
     * 정책 정보를 로그로 출력합니다.
     */
    public void logPolicyInfo(TriggerCondition condition) {
        NotificationDuplicationPolicy policy = NotificationDuplicationPolicy.fromTriggerCondition(condition);
        log.debug("알림 중복 방지 정책: condition={}, policy={}, duration={}, type={}", 
                condition, policy.name(), policy.getPreventionDuration(), policy.getCheckType());
    }
}