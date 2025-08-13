package com.seoulfit.backend.publicdata.park.application.service;

import com.seoulfit.backend.publicdata.park.application.port.in.ParkQueryUseCase;
import com.seoulfit.backend.publicdata.park.application.port.out.ParkQueryPort;
import com.seoulfit.backend.publicdata.park.domain.Park;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 서울시 공원 정보 조회 서비스
 * 헥사고날 아키텍처의 애플리케이션 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParkQueryService implements ParkQueryUseCase {
    private final ParkQueryPort parkQueryPort;

    @Override
    public List<Park> getAllPark() {
        List<Park> allPark = parkQueryPort.getAllPark();
        log.info("공원 전체 개수 : {} ", allPark.size());

        return  allPark;
    }

    @Override
    public List<Park> getParkByLatitudeAndLongitude(String latitude, String longitude) {
        List<Park> parkLocation = parkQueryPort.getParkLocation(
                Double.parseDouble(latitude),
                Double.parseDouble(longitude)
        );
        log.info("위치 기반 공원 조회 : {}", parkLocation.size());
        return parkLocation;
    }
}
