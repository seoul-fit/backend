package com.seoulfit.backend.publicdata.park.application.port.out;

import com.seoulfit.backend.publicdata.park.domain.Park;

import java.util.List;

public interface ParkCommandPort {

    List<Park> saveAllPark(List<Park> parks);

    void truncate();
}
