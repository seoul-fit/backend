package com.seoulfit.backend.facilities.application.port.out;

import com.seoulfit.backend.facilities.domain.CoolingShelter;

import java.util.List;

public interface CommandCoolingShelterPort {

    void save(List<CoolingShelter> coolingShelters);

    void truncate();
}
