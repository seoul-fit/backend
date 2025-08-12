package com.seoulfit.backend.publicdata.park.adapter.out.persistence;

import com.seoulfit.backend.location.infrastructure.ParkRepository;
import com.seoulfit.backend.publicdata.park.application.port.out.ParkCommandPort;
import com.seoulfit.backend.publicdata.park.domain.Park;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ParkCommandAdapter implements ParkCommandPort {
    private final ParkRepository parkRepository;
    private final EntityManager entityManager;

    @Override
    public List<Park> saveAllPark(List<Park> parks) {
        return parkRepository.saveAll(parks);
    }

    @Override
    public void truncate() {
        entityManager.createNativeQuery("TRUNCATE TABLE parks").executeUpdate();
    }
}
