package com.seoulfit.backend.publicdata.culture.application.port.out.query;

import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;

import java.util.List;

public interface QueryCulturalReservationPort {
    List<CulturalReservation> getAllCulturalReservation();

    List<CulturalReservation> getCulturalReservationLocation(String latitude, String longitude);

}
