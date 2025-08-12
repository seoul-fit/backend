package com.seoulfit.backend.publicdata.park.application.port.out;

import com.seoulfit.backend.publicdata.park.domain.Park;

import java.util.List;

/**
 * 서울시 공원 정보 저장소 포트
 * 헥사고날 아키텍처의 출력 포트로, 도메인 계층에서 인프라스트럭처 계층으로의 의존성을 역전시킴
 */
public interface ParkQueryPort {
    List<Park> getAllPark();

    List<Park> getParkLocation(double latitude, double longitude);

}
