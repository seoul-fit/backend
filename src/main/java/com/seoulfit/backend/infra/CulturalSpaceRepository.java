package com.seoulfit.backend.infra;

import com.seoulfit.backend.domain.CulturalSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CulturalSpaceRepository extends JpaRepository<CulturalSpace, Long> {
}
