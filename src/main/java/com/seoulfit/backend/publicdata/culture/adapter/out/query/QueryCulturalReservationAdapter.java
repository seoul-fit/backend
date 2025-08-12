package com.seoulfit.backend.publicdata.culture.adapter.out.query;

import com.seoulfit.backend.publicdata.culture.adapter.out.repository.CulturalReservationRepository;
import com.seoulfit.backend.publicdata.culture.application.port.out.query.QueryCulturalReservationPort;
import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class QueryCulturalReservationAdapter implements QueryCulturalReservationPort {
    private final CulturalReservationRepository culturalReservationRepository;

    @Override
    public List<CulturalReservation> getAllCulturalReservation() {
        return culturalReservationRepository.findAll();
    }

    @Override
    public List<CulturalReservation> getCulturalReservationLocation(String latitude, String longitude) {
        double radiusKm = 2.0;
        double x = Double.parseDouble(latitude);
        double y = Double.parseDouble(longitude);

        return culturalReservationRepository.findWithInRadius(
                BigDecimal.valueOf(x),
                BigDecimal.valueOf(y),
                radiusKm
        );
    }
}
