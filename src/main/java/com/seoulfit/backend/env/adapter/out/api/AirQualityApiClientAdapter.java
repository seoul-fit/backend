package com.seoulfit.backend.env.adapter.out.api;

import com.seoulfit.backend.env.application.port.out.AirQualityApiClient;
import com.seoulfit.backend.env.application.port.out.dto.AirQualityApiResponse;
import com.seoulfit.backend.shared.utils.RestClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 대기질 정보 외부 API 클라이언트 어댑터
 * 헥사고날 아키텍처의 Adapter Out
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AirQualityApiClientAdapter implements AirQualityApiClient {

    @Value("${seoul-api.base-url}")
    private String baseUrl;

    @Value("${seoul-api.v1.environment.service-name[0]}")
    private String serviceName;

    @Value("${seoul-api.v1.environment.api-key}")
    private String apiKey;

    private final RestClientUtils restClientUtils;

    private static final int DEFAULT_START_INDEX = 1;
    private static final int DEFAULT_END_INDEX = 1000;
    private static final int MAX_BATCH_SIZE = 1000;

    @Override
    public AirQualityApiResponse fetchRealTimeAirQuality() {
        log.debug("실시간 대기질 정보 API 호출");
        return fetchAirQuality(DEFAULT_START_INDEX, DEFAULT_END_INDEX);
    }

    @Override
    public AirQualityApiResponse fetchAirQuality(int startIndex, int endIndex) {
        log.debug("대기질 정보 API 호출 - startIndex: {}, endIndex: {}", startIndex, endIndex);

        try {
            String url = buildApiUrl(startIndex, endIndex);
            log.debug("API URL: {}", url);

            SeoulAirQualityApiResponse response = (SeoulAirQualityApiResponse) restClientUtils.callGetApi(url, SeoulAirQualityApiResponse.class);

            if (response == null || response.getRealtimeCityAir() == null) {
                log.warn("API 응답이 null이거나 데이터가 없습니다.");
                return AirQualityApiResponse.failure("API 응답 데이터가 없습니다.");
            }

            SeoulAirQualityApiResponse.RealtimeCityAir realtimeCityAir = response.getRealtimeCityAir();
            
            if (realtimeCityAir.getResult() == null || 
                !"INFO-000".equals(realtimeCityAir.getResult().getCode())) {
                String errorMessage = realtimeCityAir.getResult() != null ? 
                    realtimeCityAir.getResult().getMessage() : "알 수 없는 오류";
                log.error("API 호출 실패: {}", errorMessage);
                return AirQualityApiResponse.failure("API 호출 실패: " + errorMessage);
            }

            List<AirQualityApiResponse.AirQualityData> airQualityDataList = 
                realtimeCityAir.getRow().stream()
                    .map(this::convertToAirQualityData)
                    .toList();

            int totalCount = realtimeCityAir.getListTotalCount();
            
            log.info("대기질 정보 API 호출 성공 - 조회된 데이터 수: {}, 전체 데이터 수: {}", 
                airQualityDataList.size(), totalCount);

            return AirQualityApiResponse.success(airQualityDataList, totalCount);

        } catch (Exception e) {
            String errorMessage = "대기질 정보 API 호출 중 예외 발생: " + e.getMessage();
            log.error(errorMessage, e);
            return AirQualityApiResponse.failure(errorMessage);
        }
    }

    @Override
    public AirQualityApiResponse fetchAllAirQuality() {
        log.debug("전체 대기질 정보 API 호출");

        try {
            // 먼저 전체 데이터 수를 확인
            AirQualityApiResponse firstResponse = fetchAirQuality(1, 1);
            if (!firstResponse.isSuccess()) {
                return firstResponse;
            }

            int totalCount = firstResponse.getTotalCount();
            log.debug("전체 대기질 데이터 수: {}", totalCount);

            // 전체 데이터를 배치로 조회
            List<AirQualityApiResponse.AirQualityData> allData = new java.util.ArrayList<>();
            
            for (int startIndex = 1; startIndex <= totalCount; startIndex += MAX_BATCH_SIZE) {
                int endIndex = Math.min(startIndex + MAX_BATCH_SIZE - 1, totalCount);
                
                AirQualityApiResponse batchResponse = fetchAirQuality(startIndex, endIndex);
                if (!batchResponse.isSuccess()) {
                    log.warn("배치 조회 실패 - startIndex: {}, endIndex: {}", startIndex, endIndex);
                    continue;
                }
                
                if (batchResponse.getData() != null) {
                    allData.addAll(batchResponse.getData());
                }
            }

            log.info("전체 대기질 정보 API 호출 완료 - 조회된 데이터 수: {}", allData.size());
            return AirQualityApiResponse.success(allData, allData.size());

        } catch (Exception e) {
            String errorMessage = "전체 대기질 정보 API 호출 중 예외 발생: " + e.getMessage();
            log.error(errorMessage, e);
            return AirQualityApiResponse.failure(errorMessage);
        }
    }

    @Override
    public AirQualityApiResponse fetchAirQualityByStation(String stationName) {
        log.debug("특정 측정소 대기질 정보 API 호출 - 측정소: {}", stationName);

        // 전체 데이터를 조회한 후 필터링
        AirQualityApiResponse allResponse = fetchRealTimeAirQuality();
        if (!allResponse.isSuccess()) {
            return allResponse;
        }

        List<AirQualityApiResponse.AirQualityData> filteredData = 
            allResponse.getData().stream()
                .filter(data -> stationName.equals(data.getMsrSteNm()))
                .toList();

        return AirQualityApiResponse.success(filteredData, filteredData.size());
    }

    /**
     * API URL 생성
     */
    private String buildApiUrl(int startIndex, int endIndex) {
        return String.format("%s/%s/%s/%d/%d/",
            baseUrl, apiKey, serviceName, startIndex, endIndex);
    }

    /**
     * Seoul API 응답을 공통 DTO로 변환
     */
    private AirQualityApiResponse.AirQualityData convertToAirQualityData(
            SeoulAirQualityApiResponse.AirQualityRow row) {
        return AirQualityApiResponse.AirQualityData.builder()
            .msrDt(row.getMsrdt())
            .msrRgnNm(row.getMsrrgnnm())
            .msrSteNm(row.getMsrstennm())
            .pm10Value(row.getPm10())
            .pm25Value(row.getPm25())
            .o3Value(row.getO3())
            .no2Value(row.getNo2())
            .coValue(row.getCo())
            .so2Value(row.getSo2())
            .khaiValue(row.getKhai())
            .khaiGrade(row.getKhaigrade())
            .pm1024hAvg(row.getPm10_24h())
            .pm2524hAvg(row.getPm25_24h())
            .build();
    }

    /**
     * Seoul API 응답 DTO (내부 사용)
     */
    private static class SeoulAirQualityApiResponse {
        private RealtimeCityAir RealtimeCityAir;

        public RealtimeCityAir getRealtimeCityAir() { return RealtimeCityAir; }
        public void setRealtimeCityAir(RealtimeCityAir realtimeCityAir) { this.RealtimeCityAir = realtimeCityAir; }

        public static class RealtimeCityAir {
            private int list_total_count;
            private Result RESULT;
            private List<AirQualityRow> row;

            public int getListTotalCount() { return list_total_count; }
            public void setList_total_count(int list_total_count) { this.list_total_count = list_total_count; }

            public Result getResult() { return RESULT; }
            public void setRESULT(Result RESULT) { this.RESULT = RESULT; }

            public List<AirQualityRow> getRow() { return row; }
            public void setRow(List<AirQualityRow> row) { this.row = row; }
        }

        public static class Result {
            private String CODE;
            private String MESSAGE;

            public String getCode() { return CODE; }
            public void setCODE(String CODE) { this.CODE = CODE; }

            public String getMessage() { return MESSAGE; }
            public void setMESSAGE(String MESSAGE) { this.MESSAGE = MESSAGE; }
        }

        public static class AirQualityRow {
            private String MSRDT;
            private String MSRRGNNM;
            private String MSRSTENNM;
            private String PM10;
            private String PM25;
            private String O3;
            private String NO2;
            private String CO;
            private String SO2;
            private String KHAI;
            private String KHAIGRADE;
            private String PM10_24H;
            private String PM25_24H;

            // Getters and Setters
            public String getMsrdt() { return MSRDT; }
            public void setMSRDT(String MSRDT) { this.MSRDT = MSRDT; }

            public String getMsrrgnnm() { return MSRRGNNM; }
            public void setMSRRGNNM(String MSRRGNNM) { this.MSRRGNNM = MSRRGNNM; }

            public String getMsrstennm() { return MSRSTENNM; }
            public void setMSRSTENNM(String MSRSTENNM) { this.MSRSTENNM = MSRSTENNM; }

            public String getPm10() { return PM10; }
            public void setPM10(String PM10) { this.PM10 = PM10; }

            public String getPm25() { return PM25; }
            public void setPM25(String PM25) { this.PM25 = PM25; }

            public String getO3() { return O3; }
            public void setO3(String O3) { this.O3 = O3; }

            public String getNo2() { return NO2; }
            public void setNO2(String NO2) { this.NO2 = NO2; }

            public String getCo() { return CO; }
            public void setCO(String CO) { this.CO = CO; }

            public String getSo2() { return SO2; }
            public void setSO2(String SO2) { this.SO2 = SO2; }

            public String getKhai() { return KHAI; }
            public void setKHAI(String KHAI) { this.KHAI = KHAI; }

            public String getKhaigrade() { return KHAIGRADE; }
            public void setKHAIGRADE(String KHAIGRADE) { this.KHAIGRADE = KHAIGRADE; }

            public String getPm10_24h() { return PM10_24H; }
            public void setPM10_24H(String PM10_24H) { this.PM10_24H = PM10_24H; }

            public String getPm25_24h() { return PM25_24H; }
            public void setPM25_24H(String PM25_24H) { this.PM25_24H = PM25_24H; }
        }
    }
}
