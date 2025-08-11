package com.seoulfit.backend.publicdata.culture.adapter.out.query;

import com.seoulfit.backend.publicdata.culture.adapter.out.custom.CulturalEventRepository;
import com.seoulfit.backend.publicdata.culture.application.port.out.query.QueryCulturalEventPort;
import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class QueryCulturalEventAdapter implements QueryCulturalEventPort {
    private final CulturalEventRepository culturalEventRepository;

    @Override
    public List<CulturalEvent> getAllCulturalEvent() {
        return culturalEventRepository.findAll();
    }

    @Override
    public List<CulturalEvent> getCulturalEventLocation(double latitude, double longitude) {
        double radius = 2.0;

        return culturalEventRepository.findWithInRadius(BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude), radius);
    }

}
