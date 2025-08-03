package com.seoulfit.backend.tmp.infra;

import com.seoulfit.backend.tmp.domain.CulturalSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CulturalSpaceRepository extends JpaRepository<CulturalSpace, Long> {
}
