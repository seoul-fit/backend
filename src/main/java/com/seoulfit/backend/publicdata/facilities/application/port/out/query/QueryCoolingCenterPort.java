package com.seoulfit.backend.publicdata.facilities.application.port.out.query;

import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;

import java.util.List;

public interface QueryCoolingCenterPort {
    List<CoolingCenter> queryCoolingCenter();

    List<CoolingCenter> queryCoolingCenterLocation(double latitude, double longitude);
}
