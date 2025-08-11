package com.seoulfit.backend.publicdata.facilities.application.port.out;

import com.seoulfit.backend.publicdata.facilities.domain.CoolingShelter;

import java.util.List;

public interface CommandCoolingShelterPort {

    void save(List<CoolingShelter> coolingShelters);

    void truncate();
}
