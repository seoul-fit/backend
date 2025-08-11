package com.seoulfit.backend.trigger.dto;

import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 트리거 평가 컨텍스트
 * 
 * 트리거 전략에서 사용할 모든 정보를 담는 컨텍스트 객체
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
@Builder
public class TriggerContext {
    
    /**
     * 대상 사용자
     */
    private final User user;
    
    /**
     * 사용자 관심사 목록
     */
    private final List<InterestCategory> userInterests;
    
    /**
     * 사용자 위치 정보 (위도)
     */
    private final Double userLatitude;
    
    /**
     * 사용자 위치 정보 (경도)
     */
    private final Double userLongitude;
    
    /**
     * 공공 데이터 API 응답 데이터
     */
    private final Map<String, Object> publicApiData;
    
    /**
     * 이전 트리거 실행 시간
     */
    private final LocalDateTime lastTriggerTime;
    
    /**
     * 현재 시간
     */
    private final LocalDateTime currentTime;
    
    /**
     * 추가 메타데이터
     */
    private final Map<String, Object> metadata;
    
    /**
     * 트리거 컨텍스트 생성
     * 
     * @param user 대상 사용자
     * @param userInterests 사용자 관심사
     * @param userLatitude 사용자 위도
     * @param userLongitude 사용자 경도
     * @param publicApiData 공공 API 데이터
     * @return 트리거 컨텍스트
     */
    public static TriggerContext of(User user, List<InterestCategory> userInterests, 
                                   Double userLatitude, Double userLongitude, 
                                   Map<String, Object> publicApiData) {
        return TriggerContext.builder()
                .user(user)
                .userInterests(userInterests)
                .userLatitude(userLatitude)
                .userLongitude(userLongitude)
                .publicApiData(publicApiData)
                .currentTime(LocalDateTime.now())
                .build();
    }
    
    /**
     * 공공 API 데이터에서 특정 키의 값을 가져옵니다.
     * 
     * @param key 데이터 키
     * @param clazz 반환 타입 클래스
     * @param <T> 반환 타입
     * @return 데이터 값
     */
    @SuppressWarnings("unchecked")
    public <T> T getPublicApiData(String key, Class<T> clazz) {
        Object value = publicApiData.get(key);
        if (value != null && clazz.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * 메타데이터에서 특정 키의 값을 가져옵니다.
     * 
     * @param key 메타데이터 키
     * @param clazz 반환 타입 클래스
     * @param <T> 반환 타입
     * @return 메타데이터 값
     */
    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key, Class<T> clazz) {
        if (metadata == null) {
            return null;
        }
        Object value = metadata.get(key);
        if (value != null && clazz.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
}
