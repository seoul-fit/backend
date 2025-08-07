package com.seoulfit.backend.sports.application.port.out;

import com.seoulfit.backend.sports.application.port.out.dto.SportsFacilityProgramApiResponse;

/**
 * 서울시 공공체육시설 프로그램 정보 API 클라이언트 포트
 * 외부 API 호출을 위한 출력 포트
 */
public interface SportsFacilityProgramApiClient {

    /**
     * 서울시 공공체육시설 프로그램 정보 API 호출
     * @param startIndex 시작 인덱스
     * @param endIndex 종료 인덱스
     * @param subjectName 종목명 (선택사항)
     * @return API 응답 데이터
     */
    SportsFacilityProgramApiResponse fetchProgramInfo(int startIndex, int endIndex, String subjectName);

    /**
     * 전체 체육시설 프로그램 정보 조회
     * @return API 응답 데이터
     */
    SportsFacilityProgramApiResponse fetchAllProgramInfo();

    /**
     * 특정 종목의 체육시설 프로그램 정보 조회
     * @param subjectName 종목명
     * @return API 응답 데이터
     */
    SportsFacilityProgramApiResponse fetchProgramInfoBySubject(String subjectName);
}
