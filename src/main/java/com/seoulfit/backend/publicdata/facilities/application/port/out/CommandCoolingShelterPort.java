package com.seoulfit.backend.publicdata.facilities.application.port.out;

import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;

import java.util.List;

public interface CommandCoolingShelterPort {

    void save(List<CoolingCenter> coolingShelters);

    void truncate();
}
