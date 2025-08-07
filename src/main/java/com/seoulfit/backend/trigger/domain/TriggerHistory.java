package com.seoulfit.backend.trigger.domain;

import com.seoulfit.backend.notification.domain.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 트리거 히스토리 엔티티
 * 
 * 트리거 발동 이력을 저장하여 중복 알림 방지 및 분석에 활용
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Entity
@Table(name = "trigger_history", indexes = {
    @Index(name = "idx_trigger_history_user_type_time", 
           columnList = "user_id, trigger_type, triggered_at"),
    @Index(name = "idx_trigger_history_user_time", 
           columnList = "user_id, triggered_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TriggerHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "trigger_type", nullable = false, length = 50)
    private String triggerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_condition", nullable = false)
    private TriggerCondition triggerCondition;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "location_info", length = 200)
    private String locationInfo;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "triggered_at", nullable = false)
    private LocalDateTime triggeredAt;

    @Column(name = "evaluation_source", length = 20)
    private String evaluationSource; // SCHEDULER, REALTIME

    @ElementCollection
    @CollectionTable(name = "trigger_history_metadata", 
                    joinColumns = @JoinColumn(name = "trigger_history_id"))
    @MapKeyColumn(name = "metadata_key")
    @Column(name = "metadata_value")
    private Map<String, String> metadata;

    @Builder
    public TriggerHistory(Long userId, String triggerType, NotificationType notificationType,
                         TriggerCondition triggerCondition, String title, String message,
                         String locationInfo, Double latitude, Double longitude,
                         Integer priority, LocalDateTime triggeredAt, String evaluationSource,
                         Map<String, String> metadata) {
        this.userId = userId;
        this.triggerType = triggerType;
        this.notificationType = notificationType;
        this.triggerCondition = triggerCondition;
        this.title = title;
        this.message = message;
        this.locationInfo = locationInfo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.priority = priority;
        this.triggeredAt = triggeredAt != null ? triggeredAt : LocalDateTime.now();
        this.evaluationSource = evaluationSource;
        this.metadata = metadata;
    }

    /**
     * 스케줄러 기반 트리거 히스토리 생성
     */
    public static TriggerHistory fromScheduler(Long userId, String triggerType, 
                                             NotificationType notificationType,
                                             TriggerCondition triggerCondition,
                                             String title, String message,
                                             String locationInfo, Integer priority) {
        return TriggerHistory.builder()
                .userId(userId)
                .triggerType(triggerType)
                .notificationType(notificationType)
                .triggerCondition(triggerCondition)
                .title(title)
                .message(message)
                .locationInfo(locationInfo)
                .priority(priority)
                .evaluationSource("SCHEDULER")
                .build();
    }

    /**
     * 실시간 위치 기반 트리거 히스토리 생성
     */
    public static TriggerHistory fromRealtime(Long userId, String triggerType,
                                            NotificationType notificationType,
                                            TriggerCondition triggerCondition,
                                            String title, String message,
                                            String locationInfo, Double latitude, Double longitude,
                                            Integer priority, Map<String, String> metadata) {
        return TriggerHistory.builder()
                .userId(userId)
                .triggerType(triggerType)
                .notificationType(notificationType)
                .triggerCondition(triggerCondition)
                .title(title)
                .message(message)
                .locationInfo(locationInfo)
                .latitude(latitude)
                .longitude(longitude)
                .priority(priority)
                .evaluationSource("REALTIME")
                .metadata(metadata)
                .build();
    }

    /**
     * 중복 알림 방지를 위한 최근 트리거 확인
     */
    public boolean isRecentTrigger(int minutes) {
        return triggeredAt.isAfter(LocalDateTime.now().minusMinutes(minutes));
    }
}
