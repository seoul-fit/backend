package com.seoulfit.backend.park.application.port.out;

import com.seoulfit.backend.park.application.port.out.dto.SeoulParkApiResponse;

/**
 * 서울시 공원 정보 API 클라이언트 포트
 * 외부 API 호출을 위한 출력 포트
 */
public interface SeoulParkApiClient {

    /**
     * 서울시 공원 정보 API 호출
     * @param startIndex 시작 인덱스
     * @param endIndex 종료 인덱스
     * @return API 응답 데이터
     */
    SeoulParkApiResponse fetchParkInfo(int startIndex, int endIndex);

    /**
     * 전체 공원 정보 조회
     * @return API 응답 데이터
     */
    SeoulParkApiResponse fetchAllParkInfo();
}
