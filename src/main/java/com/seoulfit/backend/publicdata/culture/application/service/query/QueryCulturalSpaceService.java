package com.seoulfit.backend.publicdata.culture.application.service.query;

import com.seoulfit.backend.publicdata.culture.application.port.in.QueryCulturalSpaceUseCase;
import com.seoulfit.backend.publicdata.culture.application.port.out.query.QueryCulturalSpacePort;
import com.seoulfit.backend.publicdata.culture.domain.CulturalSpace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class QueryCulturalSpaceService implements QueryCulturalSpaceUseCase {
    private final QueryCulturalSpacePort queryCulturalSpacePort;

    @Override
    public List<CulturalSpace> getAllCulturalSpace() {
        return queryCulturalSpacePort.getAllCulturalSpace();
    }

}
