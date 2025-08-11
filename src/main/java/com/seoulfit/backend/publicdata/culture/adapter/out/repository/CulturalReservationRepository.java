package com.seoulfit.backend.publicdata.culture.adapter.out.repository;

import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CulturalReservationRepository extends JpaRepository<CulturalReservation, Long> {
}
