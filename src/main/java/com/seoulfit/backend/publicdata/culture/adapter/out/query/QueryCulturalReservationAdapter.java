package com.seoulfit.backend.publicdata.culture.adapter.out.query;

import com.seoulfit.backend.publicdata.culture.adapter.out.repository.CulturalReservationRepository;
import com.seoulfit.backend.publicdata.culture.application.port.out.query.QueryCulturalReservationPort;
import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class QueryCulturalReservationAdapter implements QueryCulturalReservationPort {
    private final CulturalReservationRepository culturalReservationRepository;

    @Override
    public List<CulturalReservation> getAllCulturalReservation() {
        return culturalReservationRepository.findAll();
    }
}
