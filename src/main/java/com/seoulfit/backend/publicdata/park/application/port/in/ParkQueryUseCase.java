package com.seoulfit.backend.publicdata.park.application.port.in;

import com.seoulfit.backend.publicdata.park.domain.Park;

import java.util.List;

/**
 * 서울시 공원 정보 조회 유스케이스
 * 헥사고날 아키텍처의 입력 포트
 */
public interface ParkQueryUseCase {
    List<Park> getAllPark();

    List<Park> getParkByLatitudeAndLongitude(String latitude, String longitude);
}
