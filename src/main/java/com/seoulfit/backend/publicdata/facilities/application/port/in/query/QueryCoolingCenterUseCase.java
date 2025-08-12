package com.seoulfit.backend.publicdata.facilities.application.port.in.query;

import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;

import java.util.List;

public interface QueryCoolingCenterUseCase {
    List<CoolingCenter> getAllCoolingCenter();

    List<CoolingCenter> getCoolingCenterByLatitudeAndLongitude(String latitude, String longitude);
}
