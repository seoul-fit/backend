package com.seoulfit.backend.publicdata.culture.application.service.query;

import com.seoulfit.backend.publicdata.culture.application.port.in.QueryCulturalReservationUseCase;
import com.seoulfit.backend.publicdata.culture.application.port.out.query.QueryCulturalReservationPort;
import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class QueryCulturalReservationService implements QueryCulturalReservationUseCase {
    private final QueryCulturalReservationPort queryCulturalReservationPort;

    @Override
    public List<CulturalReservation> getAllCulturalReservation() {
        return queryCulturalReservationPort.getAllCulturalReservation();
    }

}
