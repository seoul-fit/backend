package com.seoulfit.backend.infra.mapper;

import com.seoulfit.backend.domain.CulturalEvent;
import com.seoulfit.backend.presentation.culture.dtos.response.SeoulApiResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class CulturalEventMapper {

    public List<CulturalEvent> mapToEntity(List<SeoulApiResponse.CulturalEventData> dataList) {
        List<CulturalEvent> entityList = new ArrayList<>();
        for(SeoulApiResponse.CulturalEventData data : dataList) {
            CulturalEvent build = CulturalEvent.builder()
                    .codeName(data.getCodeName())
                    .district(data.getGuName())
                    .title(data.getTitle())
                    .eventDate(data.getDate())
                    .startDate(data.getStartDate() != null ? data.getStartDate().toLocalDate() : null)
                    .endDate(data.getEndDate() != null ? data.getEndDate().toLocalDate() : null)
                    .place(data.getPlace())
                    .orgName(data.getOrgName())
                    .useTarget(data.getUseTarget())
                    .useFee(data.getUseFee())
                    .player(data.getPlayer())
                    .program(data.getProgram())
                    .etcDesc(data.getEtcDesc())
                    .orgLink(data.getOrgLink())
                    .mainImg(data.getMainImg())
                    .registrationDate(data.getRegistrationDate())
                    .ticket(data.getTicket())
                    .themeCode(data.getThemeCode())
                    .latitude(parseCoordinate(data.getLatitude()))
                    .longitude(parseCoordinate(data.getLongitude()))
                    .isFree(data.getIsFree())
                    .homepageAddr(data.getHomepageAddr())
                    .externalId(LocalDateTime.now().toString() + data.getCodeName())
                    .build();

            entityList.add(build);
        }
        return entityList;
    }

    private BigDecimal parseCoordinate(String coordinate) {
        // null 체크
        if (coordinate == null) {
            return BigDecimal.TEN;
        }

        // 공백 제거 후 빈 문자열 체크
        String trimmed = coordinate.trim();
        if (trimmed.isEmpty()) {
            return BigDecimal.TEN;
        }

        // 유효하지 않은 값들 체크
        if (trimmed.equalsIgnoreCase("N/A") ||
                trimmed.equalsIgnoreCase("없음") ||
                trimmed.equalsIgnoreCase("null") ||
                trimmed.equals("-") ||
                trimmed.equals("0") ||
                trimmed.equals("0.0")) {
            return BigDecimal.TEN;
        }

        try {
            BigDecimal value = new BigDecimal(trimmed);

            // 서울시 좌표 범위 검증 (선택적)
            // 위도: 37.4 ~ 37.7, 경도: 126.7 ~ 127.2
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                return BigDecimal.TEN;
            }

            return value;
        } catch (NumberFormatException e) {
            // 숫자 변환 실패시 기본값 반환
            return BigDecimal.TEN;
        }
    }

}
