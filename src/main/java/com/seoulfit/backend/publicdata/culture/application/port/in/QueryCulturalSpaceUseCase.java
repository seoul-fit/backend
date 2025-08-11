package com.seoulfit.backend.publicdata.culture.application.port.in;

import com.seoulfit.backend.publicdata.culture.domain.CulturalSpace;

import java.util.List;

public interface QueryCulturalSpaceUseCase {

    List<CulturalSpace> getAllCulturalSpace();

    List<CulturalSpace> getCulturalSpaceByLatitudeAndLongitude(String latitude, String longitude);

}
