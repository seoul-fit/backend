package com.seoulfit.backend.publicdata.culture.adapter.out.query;

import com.seoulfit.backend.publicdata.culture.adapter.out.repository.CulturalSpaceRepository;
import com.seoulfit.backend.publicdata.culture.application.port.out.query.QueryCulturalSpacePort;
import com.seoulfit.backend.publicdata.culture.domain.CulturalSpace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class QueryCulturalSpaceAdapter implements QueryCulturalSpacePort {
    private final CulturalSpaceRepository culturalSpaceRepository;

    @Override
    public List<CulturalSpace> getAllCulturalSpace() {
        return culturalSpaceRepository.findAll();
    }

    @Override
    public List<CulturalSpace> getCulturalSpaceLocation(double latitude, double longitude) {
        double radiusKm = 5.0; // 검색 반경 5km
        return culturalSpaceRepository.findWithInRadius(
                BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude), radiusKm
        );
    }
}
