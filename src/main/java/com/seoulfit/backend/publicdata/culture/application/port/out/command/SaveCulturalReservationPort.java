package com.seoulfit.backend.publicdata.culture.application.port.out.command;

import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;

import java.util.List;

public interface SaveCulturalReservationPort {
    int saveCulturalReservations(List<CulturalReservation> reservations);

    void clearCulturalReservations();

}
