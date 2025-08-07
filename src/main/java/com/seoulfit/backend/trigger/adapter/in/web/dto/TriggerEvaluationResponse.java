package com.seoulfit.backend.trigger.adapter.in.web.dto;

import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 트리거 평가 응답 DTO
 * 
 * 트리거 평가 결과를 클라이언트에 반환하기 위한 응답 객체
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Schema(description = "트리거 평가 결과")
@Getter
@Builder
public class TriggerEvaluationResponse {

    @Schema(description = "트리거 발동 여부", example = "true")
    private final boolean triggered;

    @Schema(description = "발동된 트리거 개수", example = "2")
    private final int triggeredCount;

    @Schema(description = "평가된 전체 트리거 개수", example = "5")
    private final int totalEvaluated;

    @Schema(description = "발동된 트리거 목록")
    private final List<TriggeredInfo> triggeredList;

    @Schema(description = "평가 시간")
    private final LocalDateTime evaluationTime;

    @Schema(description = "사용자 위치 정보")
    private final LocationInfo locationInfo;

    @Schema(description = "추가 메타데이터")
    private final Map<String, Object> metadata;

    /**
     * TriggerEvaluationResult로부터 응답 DTO 생성
     */
    public static TriggerEvaluationResponse from(TriggerEvaluationResult result) {
        return TriggerEvaluationResponse.builder()
                .triggered(result.isTriggered())
                .triggeredCount(result.getTriggeredCount())
                .totalEvaluated(result.getTotalEvaluated())
                .triggeredList(result.getTriggeredList().stream()
                        .map(TriggeredInfo::from)
                        .toList())
                .evaluationTime(result.getEvaluationTime())
                .locationInfo(LocationInfo.from(result.getLocationInfo()))
                .metadata(result.getMetadata())
                .build();
    }

    /**
     * 발동된 트리거 정보
     */
    @Schema(description = "발동된 트리거 정보")
    @Getter
    @Builder
    public static class TriggeredInfo {
        
        @Schema(description = "트리거 타입", example = "TEMPERATURE")
        private final String triggerType;

        @Schema(description = "알림 제목", example = "폭염 주의보")
        private final String title;

        @Schema(description = "알림 메시지", example = "현재 기온이 35.2°C입니다.")
        private final String message;

        @Schema(description = "우선순위", example = "10")
        private final int priority;

        @Schema(description = "위치 정보", example = "서울시 강남구")
        private final String locationInfo;

        @Schema(description = "발동 시간")
        private final LocalDateTime triggeredTime;

        public static TriggeredInfo from(TriggerEvaluationResult.TriggeredInfo info) {
            return TriggeredInfo.builder()
                    .triggerType(info.getTriggerType())
                    .title(info.getTitle())
                    .message(info.getMessage())
                    .priority(info.getPriority())
                    .locationInfo(info.getLocationInfo())
                    .triggeredTime(info.getTriggeredTime())
                    .build();
        }
    }

    /**
     * 위치 정보
     */
    @Schema(description = "위치 정보")
    @Getter
    @Builder
    public static class LocationInfo {
        
        @Schema(description = "위도", example = "37.5665")
        private final Double latitude;

        @Schema(description = "경도", example = "126.9780")
        private final Double longitude;

        @Schema(description = "주소", example = "서울시 중구 명동")
        private final String address;

        @Schema(description = "구역명", example = "명동")
        private final String district;

        public static LocationInfo from(TriggerEvaluationResult.LocationInfo info) {
            if (info == null) return null;
            
            return LocationInfo.builder()
                    .latitude(info.getLatitude())
                    .longitude(info.getLongitude())
                    .address(info.getAddress())
                    .district(info.getDistrict())
                    .build();
        }
    }
}
