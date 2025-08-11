package com.seoulfit.backend.publicdata.culture.infrastructure.mapper;

import com.seoulfit.backend.publicdata.culture.adapter.in.web.dto.res.SeoulCulturalSpaceApiResponse;
import com.seoulfit.backend.publicdata.culture.domain.CulturalSpace;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class CulturalSpaceMapper {

    public List<CulturalSpace> mapToEntity(List<SeoulCulturalSpaceApiResponse.CulturalSpaceData> dataList) {
        List<CulturalSpace> entityList = new ArrayList<>();

        for (SeoulCulturalSpaceApiResponse.CulturalSpaceData data: dataList) {
            CulturalSpace build = CulturalSpace.builder()
                    .num(data.getNum())
                    .subjCode(data.getSubjCode())
                    .facilityName(data.getFacName())
                    .address(data.getAddr())
                    .district(data.getDistrict())
                    .longitude(parseCoordinate(data.getXCoord()))
                    .latitude(parseCoordinate(data.getYCoord()))
                    .phone(data.getPhne())
                    .fax(data.getFax())
                    .homepage(data.getHomepage())
                    .openHour(data.getOpenHour())
                    .entranceFee(data.getEntrFee())
                    .closeDay(data.getCloseDay())
                    .openDay(data.getOpenDay())
                    .seatCount(data.getSeatCnt())
                    .mainImage(data.getMainImg())
                    .etcDesc(data.getEtcDesc())
                    .facilityDesc(data.getFacDesc())
                    .isFree(data.getEntrFree())
                    .subway(data.getSubway())
                    .busStop(data.getBusStop())
                    .yellowLine(data.getYellow())
                    .greenLine(data.getGreen())
                    .blueLine(data.getBlue())
                    .redLine(data.getRed())
                    .airportBus(data.getAirport())
                    .externalId(LocalDateTime.now().toString() + data.getNum())
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