package com.seoulfit.backend.publicdata.facilities.adapter.out.persistence;

import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository.CoolingCenterRepository;
import com.seoulfit.backend.publicdata.facilities.application.port.out.CommandCoolingShelterPort;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class SaveCoolingShelterAdapter implements CommandCoolingShelterPort {
    private final CoolingCenterRepository coolingCenterRepository;
    private final EntityManager entityManager;

    @Override
    public void save(List<CoolingCenter> coolingShelters) {
        log.info("무더위 쉼터 데이터 Insert");
        coolingCenterRepository.saveAll(coolingShelters);
    }

    @Override
    public void truncate() {
        entityManager.createNativeQuery("TRUNCATE TABLE cooling_centers").executeUpdate();
    }
}
