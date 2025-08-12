package com.seoulfit.backend.publicdata.facilities.adapter.out.persistence;

import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository.CoolingCenterRepository;
import com.seoulfit.backend.publicdata.facilities.application.port.out.query.QueryCoolingCenterPort;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class QueryCoolingCenterAdapter implements QueryCoolingCenterPort {
    private final CoolingCenterRepository coolingCenterRepository;

    @Override
    public List<CoolingCenter> queryCoolingCenter() {
        return coolingCenterRepository.findAll();
    }

    @Override
    public List<CoolingCenter> queryCoolingCenterLocation(double latitude, double longitude) {
        double radiusKm = 2.0;
        return coolingCenterRepository.findByLocationWithinRadius(latitude, longitude, radiusKm);
    }
}
