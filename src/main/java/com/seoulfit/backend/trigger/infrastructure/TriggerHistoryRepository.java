package com.seoulfit.backend.trigger.infrastructure;

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
}
