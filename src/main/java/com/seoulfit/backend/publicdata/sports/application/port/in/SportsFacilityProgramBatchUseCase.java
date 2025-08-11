package com.seoulfit.backend.publicdata.sports.application.port.in;

/**
 * 서울시 공공체육시설 프로그램 정보 배치 처리 유스케이스
 * 헥사고날 아키텍처의 입력 포트
 */
public interface SportsFacilityProgramBatchUseCase {

    /**
     * 서울시 공공체육시설 프로그램 정보 일일 배치 처리
     * - API에서 최신 프로그램 정보 조회
     * - 데이터베이스에 저장 (날짜별)
     * - 3일 이전 데이터 삭제
     * 
     * @param dataDate 데이터 날짜 (YYYYMMDD 형식)
     * @return 배치 처리 결과
     */
    SportsFacilityProgramBatchResult processDailyBatch(String dataDate);

    /**
     * 특정 종목의 프로그램 정보 배치 처리
     * @param dataDate 데이터 날짜 (YYYYMMDD 형식)
     * @param subjectName 종목명
     * @return 배치 처리 결과
     */
    SportsFacilityProgramBatchResult processBatchBySubject(String dataDate, String subjectName);

    /**
     * 이전 데이터 정리
     * 지정된 보관 기간을 초과한 데이터를 삭제
     * 
     * @param retentionDays 보관 기간 (일)
     * @return 삭제된 레코드 수
     */
    int cleanupOldData(int retentionDays);

    /**
     * 배치 처리 결과 DTO
     */
    record SportsFacilityProgramBatchResult(
        String dataDate,
        int totalFetched,
        int totalSaved,
        int totalUpdated,
        int totalDeleted,
        boolean success,
        String errorMessage
    ) {
        public static SportsFacilityProgramBatchResult success(String dataDate, int totalFetched, 
                                                              int totalSaved, int totalUpdated, int totalDeleted) {
            return new SportsFacilityProgramBatchResult(dataDate, totalFetched, totalSaved, totalUpdated, totalDeleted, true, null);
        }

        public static SportsFacilityProgramBatchResult failure(String dataDate, String errorMessage) {
            return new SportsFacilityProgramBatchResult(dataDate, 0, 0, 0, 0, false, errorMessage);
        }
    }
}
