package com.seoulfit.backend.publicdata.culture.adapter.out.command;

import com.seoulfit.backend.publicdata.culture.adapter.out.repository.CulturalReservationRepository;
import com.seoulfit.backend.publicdata.culture.application.port.out.command.SaveCulturalReservationPort;
import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CulturalReservationAdapter implements SaveCulturalReservationPort  {
    private final EntityManager entityManager;
    private final CulturalReservationRepository culturalReservationRepository;

    @Override
    public int saveCulturalReservations(List<CulturalReservation> reservations) {
        List<CulturalReservation> culturalReservations = culturalReservationRepository.saveAll(reservations);

        return culturalReservations.size();
    }

    @Override
    public void clearCulturalReservations() {
        entityManager.createNativeQuery("TRUNCATE TABLE cultural_reservation").executeUpdate();
    }
}
