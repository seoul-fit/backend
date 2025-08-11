package com.seoulfit.backend.publicdata.culture.infrastructure.mapper;

import com.seoulfit.backend.publicdata.culture.adapter.in.web.dto.res.SeoulReservationApiResponse;
import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;

import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ReservationDataMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // API에서 제공하는 날짜 형식에 맞게 조정 필요

    public static List<CulturalReservation> mapToEntity(List<SeoulReservationApiResponse.ReservationData> dataList) {
        List<CulturalReservation> entityList = new ArrayList<>();

        for (SeoulReservationApiResponse.ReservationData data : dataList) {
            // 유효성 검증 (필요 시)
            if (!data.isValid() || !data.hasValidLocation() || !data.isActive()) {
                continue; // 유효하지 않은 데이터는 스킵
            }

            CulturalReservation entity = CulturalReservation.builder()
                    .svcId(data.getSvcId())
                    .gubun(data.getGubun())
                    .maxClassNm(data.getMaxClassNm())
                    .minClassNm(data.getMinClassNm())
                    .svcStatNm(data.getSvcStatNm())
                    .svcNm(data.getSvcNm())
                    .payAtNm(data.getPayAtNm())
                    .placeNm(data.getPlaceNm())
                    .useTgtInfo(data.getUseTgtInfo())
                    .svcUrl(data.getSvcUrl())
                    .x(data.getX())
                    .y(data.getY())
                    .svcOpnBgnDt(parseDateTime(data.getSvcOpnBgnDt()))
                    .svcOpnEndDt(parseDateTime(data.getSvcOpnEndDt()))
                    .rcptBgnDt(parseDateTime(data.getRcptBgnDt()))
                    .rcptEndDt(parseDateTime(data.getRcptEndDt()))
                    .areaNm(data.getDistrict()) // getDistrict()로 구 이름 추출
                    .imgUrl(data.getImgUrl())
                    .dtlCont(data.getDtlCont())
                    .telNo(data.getTelNo())
                    .vMin(data.getVMin())
                    .vMax(data.getVMax())
                    .revStdDayNm(data.getRevStdDayNm())
                    .revStdDay(data.getRevStdDay())
                    .build();

            entityList.add(entity);
        }

        return entityList;
    }

    private static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            // 로깅 추가 가능 (예: log.error("Failed to parse date: {}", dateTimeStr, e))
            return null; // 파싱 실패 시 null 반환 (필요 시 다른 정책 적용)
        }
    }
}