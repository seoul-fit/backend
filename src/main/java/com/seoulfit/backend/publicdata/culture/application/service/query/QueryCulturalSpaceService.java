package com.seoulfit.backend.publicdata.culture.application.service.query;

import com.seoulfit.backend.publicdata.culture.application.port.in.QueryCulturalSpaceUseCase;
import com.seoulfit.backend.publicdata.culture.application.port.out.query.QueryCulturalSpacePort;
import com.seoulfit.backend.publicdata.culture.domain.CulturalSpace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class QueryCulturalSpaceService implements QueryCulturalSpaceUseCase {
    private final QueryCulturalSpacePort queryCulturalSpacePort;

    @Override
    public List<CulturalSpace> getAllCulturalSpace() {
        return queryCulturalSpacePort.getAllCulturalSpace();
    }

    @Override
    public List<CulturalSpace> getCulturalSpaceByLatitudeAndLongitude(String latitude, String longitude) {
        List<CulturalSpace> culturalSpaceLocation = queryCulturalSpacePort.getCulturalSpaceLocation(
                Double.parseDouble(latitude), Double.parseDouble(longitude)
        );
        log.info("Cultural Space Location : {}", culturalSpaceLocation.size());
        return culturalSpaceLocation;
    }

}
