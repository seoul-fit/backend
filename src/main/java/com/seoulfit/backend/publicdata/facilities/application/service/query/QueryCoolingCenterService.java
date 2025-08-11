package com.seoulfit.backend.publicdata.facilities.application.service.query;

import com.seoulfit.backend.publicdata.facilities.application.port.in.query.QueryCoolingCenterUseCase;
import com.seoulfit.backend.publicdata.facilities.application.port.out.query.QueryCoolingCenterPort;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class QueryCoolingCenterService implements QueryCoolingCenterUseCase {
    private final QueryCoolingCenterPort queryCoolingCenterPort;

    @Override
    public List<CoolingCenter> getAllCoolingCenter() {
        List<CoolingCenter> coolingCenters = queryCoolingCenterPort.queryCoolingCenter();
        log.info("Cooling Center Count : {}", coolingCenters.size());

        return coolingCenters;
    }
}
