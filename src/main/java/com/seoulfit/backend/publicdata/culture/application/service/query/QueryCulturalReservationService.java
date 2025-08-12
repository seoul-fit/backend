package com.seoulfit.backend.publicdata.culture.application.service.query;

import com.seoulfit.backend.publicdata.culture.application.port.in.QueryCulturalReservationUseCase;
import com.seoulfit.backend.publicdata.culture.application.port.out.query.QueryCulturalReservationPort;
import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class QueryCulturalReservationService implements QueryCulturalReservationUseCase {
    private final QueryCulturalReservationPort queryCulturalReservationPort;

    @Override
    public List<CulturalReservation> getAllCulturalReservation() {
        return queryCulturalReservationPort.getAllCulturalReservation();
    }

    @Override
    public List<CulturalReservation> getCulturalSpaceByLatitudeAndLongitude(String latitude, String longitude) {
        List<CulturalReservation> culturalReservationLocation = queryCulturalReservationPort.getCulturalReservationLocation(latitude, longitude);
        log.info("문화 행사 예약 정보 현재 위치 정보 기반 데이터 Count : {}", culturalReservationLocation.size());
        return culturalReservationLocation;
    }

}
