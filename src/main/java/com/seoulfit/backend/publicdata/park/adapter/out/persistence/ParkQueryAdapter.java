package com.seoulfit.backend.publicdata.park.adapter.out.persistence;

import com.seoulfit.backend.publicdata.park.domain.Park;
import com.seoulfit.backend.publicdata.park.adapter.out.persistence.repository.ParkRepository;
import com.seoulfit.backend.publicdata.park.application.port.out.ParkQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 서울시 공원 정보 Repository 어댑터
 * 헥사고날 아키텍처의 출력 어댑터
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ParkQueryAdapter implements ParkQueryPort {
    private final ParkRepository parkRepository;

    @Override
    public List<Park> getAllPark() {
        return parkRepository.findAll();
    }

    @Override
    public List<Park> getParkLocation(double latitude, double longitude) {
        double radiusKm = 2.0;
        return parkRepository.findByLocationWithinRadius(
                latitude, longitude, radiusKm
        );
    }

}
