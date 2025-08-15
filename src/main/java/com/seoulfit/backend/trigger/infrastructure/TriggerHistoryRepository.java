package com.seoulfit.backend.trigger.infrastructure;

import com.seoulfit.backend.trigger.domain.TriggerCondition;
import com.seoulfit.backend.trigger.domain.TriggerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 트리거 히스토리 레포지토리
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Repository
public interface TriggerHistoryRepository extends JpaRepository<TriggerHistory, Long> {

    /**
     * 사용자별 트리거 히스토리 조회 (최신순)
     */
    @Query("SELECT th FROM TriggerHistory th WHERE th.userId = :userId " +
           "ORDER BY th.triggeredAt DESC LIMIT :limit OFFSET :offset")
    List<TriggerHistory> findByUserIdOrderByTriggeredAtDesc(@Param("userId") Long userId, 
                                                           @Param("offset") int offset, 
                                                           @Param("limit") int limit);

    /**
     * 최근 특정 시간 내 동일 트리거 발동 여부 확인
     */
    boolean existsByUserIdAndTriggerTypeAndTriggeredAtAfter(Long userId, String triggerType, LocalDateTime since);

    /**
     * 사용자별 특정 기간 트리거 히스토리 조회
     */
    List<TriggerHistory> findByUserIdAndTriggeredAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    /**
     * 트리거 타입별 발동 통계
     */
    @Query("SELECT th.triggerType, COUNT(th) FROM TriggerHistory th " +
           "WHERE th.triggeredAt >= :since GROUP BY th.triggerType")
    List<Object[]> countByTriggerTypeSince(@Param("since") LocalDateTime since);

    /**
     * 사용자별 트리거 발동 횟수
     */
    @Query("SELECT COUNT(th) FROM TriggerHistory th WHERE th.userId = :userId AND th.triggeredAt >= :since")
    long countByUserIdSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    /**
     * 문화행사별 사용자 알림 발송 여부 확인
     * 특정 사용자가 특정 문화행사에 대한 알림을 이미 받았는지 확인
     */
    @Query("SELECT COUNT(th) > 0 FROM TriggerHistory th " +
           "JOIN th.metadata m " +
           "WHERE th.userId = :userId " +
           "AND th.triggerCondition IN ('CULTURAL_EVENT', 'CULTURAL_EVENT_START') " +
           "AND KEY(m) = 'cultural_event_id' " +
           "AND VALUE(m) = :culturalEventId")
    boolean existsByUserIdAndCulturalEventId(@Param("userId") Long userId, 
                                           @Param("culturalEventId") String culturalEventId);

    /**
     * 문화행사별 사용자 알림 발송 여부 확인 (특정 기간 내)
     * 특정 기간 내에 특정 사용자가 특정 문화행사에 대한 알림을 받았는지 확인
     */
    @Query("SELECT COUNT(th) > 0 FROM TriggerHistory th " +
           "JOIN th.metadata m " +
           "WHERE th.userId = :userId " +
           "AND th.triggerCondition IN ('CULTURAL_EVENT', 'CULTURAL_EVENT_START') " +
           "AND KEY(m) = 'cultural_event_id' " +
           "AND VALUE(m) = :culturalEventId " +
           "AND th.triggeredAt >= :since")
    boolean existsByUserIdAndCulturalEventIdSince(@Param("userId") Long userId, 
                                                @Param("culturalEventId") String culturalEventId,
                                                @Param("since") LocalDateTime since);

    /**
     * 특정 조건에 대한 사용자 알림 발송 여부 확인 (시간 기반)
     * 지정된 시간 이후에 특정 사용자가 특정 트리거 조건에 대한 알림을 받았는지 확인
     */
    @Query("SELECT COUNT(th) > 0 FROM TriggerHistory th " +
           "WHERE th.userId = :userId " +
           "AND th.triggerCondition = :triggerCondition " +
           "AND th.triggeredAt >= :since")
    boolean existsByUserIdAndTriggerConditionSince(@Param("userId") Long userId,
                                                  @Param("triggerCondition") TriggerCondition triggerCondition,
                                                  @Param("since") LocalDateTime since);

    /**
     * 고유 식별자 기반 사용자 알림 발송 여부 확인 (범용)
     * 특정 사용자가 특정 고유 식별자에 대한 알림을 받았는지 확인
     */
    @Query("SELECT COUNT(th) > 0 FROM TriggerHistory th " +
           "JOIN th.metadata m " +
           "WHERE th.userId = :userId " +
           "AND th.triggerCondition = :triggerCondition " +
           "AND KEY(m) = :identifierKey " +
           "AND VALUE(m) = :identifierValue")
    boolean existsByUserIdAndUniqueIdentifier(@Param("userId") Long userId,
                                             @Param("triggerCondition") TriggerCondition triggerCondition,
                                             @Param("identifierKey") String identifierKey,
                                             @Param("identifierValue") String identifierValue);

    /**
     * 고유 식별자 기반 사용자 알림 발송 여부 확인 (시간 제한)
     * 특정 기간 내에 특정 사용자가 특정 고유 식별자에 대한 알림을 받았는지 확인
     */
    @Query("SELECT COUNT(th) > 0 FROM TriggerHistory th " +
           "JOIN th.metadata m " +
           "WHERE th.userId = :userId " +
           "AND th.triggerCondition = :triggerCondition " +
           "AND KEY(m) = :identifierKey " +
           "AND VALUE(m) = :identifierValue " +
           "AND th.triggeredAt >= :since")
    boolean existsByUserIdAndUniqueIdentifierSince(@Param("userId") Long userId,
                                                  @Param("triggerCondition") TriggerCondition triggerCondition,
                                                  @Param("identifierKey") String identifierKey,
                                                  @Param("identifierValue") String identifierValue,
                                                  @Param("since") LocalDateTime since);

    /**
     * 위치 기반 사용자 알림 발송 여부 확인 (시간 제한)
     * 특정 기간 내에 특정 사용자가 특정 위치에서 특정 트리거 조건에 대한 알림을 받았는지 확인
     */
    @Query("SELECT COUNT(th) > 0 FROM TriggerHistory th " +
           "WHERE th.userId = :userId " +
           "AND th.triggerCondition = :triggerCondition " +
           "AND th.latitude IS NOT NULL AND th.longitude IS NOT NULL " +
           "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(th.latitude)) " +
           "    * cos(radians(th.longitude) - radians(:longitude)) " +
           "    + sin(radians(:latitude)) * sin(radians(th.latitude)))) <= :radiusKm " +
           "AND th.triggeredAt >= :since")
    boolean existsByUserIdAndLocationSince(@Param("userId") Long userId,
                                         @Param("triggerCondition") TriggerCondition triggerCondition,
                                         @Param("latitude") Double latitude,
                                         @Param("longitude") Double longitude,
                                         @Param("radiusKm") Double radiusKm,
                                         @Param("since") LocalDateTime since);
}
