package com.seoulfit.backend.publicdata.culture.application.port.out.query;

import com.seoulfit.backend.publicdata.culture.domain.CulturalSpace;

import java.util.List;

public interface QueryCulturalSpacePort {
    List<CulturalSpace> getAllCulturalSpace();

}
