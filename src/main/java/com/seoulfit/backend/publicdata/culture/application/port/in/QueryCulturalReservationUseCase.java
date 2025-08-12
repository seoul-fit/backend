package com.seoulfit.backend.publicdata.culture.application.port.in;

import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;

import java.util.List;

public interface QueryCulturalReservationUseCase {

    List<CulturalReservation> getAllCulturalReservation();

    List<CulturalReservation> getCulturalSpaceByLatitudeAndLongitude(String latitude, String longitude);
}
