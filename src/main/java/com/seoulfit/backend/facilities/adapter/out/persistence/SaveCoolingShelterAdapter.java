package com.seoulfit.backend.facilities.adapter.out.persistence;

import com.seoulfit.backend.facilities.adapter.out.persistence.repository.CoolingShelterRepository;
import com.seoulfit.backend.facilities.application.port.out.CommandCoolingShelterPort;
import com.seoulfit.backend.facilities.domain.CoolingShelter;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class SaveCoolingShelterAdapter implements CommandCoolingShelterPort {
    private final CoolingShelterRepository coolingShelterRepository;
    private final EntityManager entityManager;

    @Override
    public void save(List<CoolingShelter> coolingShelters) {
        coolingShelterRepository.saveAll(coolingShelters);
    }

    @Override
    public void truncate() {
        entityManager.createNativeQuery("TRUNCATE TABLE cooling_shelters").executeUpdate();
    }
}
