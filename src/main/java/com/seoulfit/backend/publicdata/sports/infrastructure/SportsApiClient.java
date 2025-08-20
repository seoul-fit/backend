package com.seoulfit.backend.publicdata.sports.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoulfit.backend.publicdata.sports.infrastructure.dto.SportsApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 서울시 체육시설 정보 API 클라이언트
 * 
 * 서울시 공공데이터 포탈의 체육시설 정보 API를 호출하여 데이터를 조회합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SportsApiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${seoul.api.key:666e634468776c7339314668766844}")
    private String apiKey;

    @Value("${seoul.api.base-url:http://openapi.seoul.go.kr:8088}")
    private String baseUrl;

    private static final String SPORTS_ENDPOINT = "ListPublicReservationSport";
    private static final int DEFAULT_START_INDEX = 1;
    private static final int DEFAULT_END_INDEX = 1000;

    /**
     * 서울시 체육시설 정보 조회
     * 
     * @return 체육시설 정보 DTO 목록
     */
    public List<SportsApiDto> getSportsData() {
        return getSportsData(DEFAULT_START_INDEX, DEFAULT_END_INDEX);
    }

    /**
     * 서울시 체육시설 정보 조회 (페이징)
     * 
     * @param startIndex 시작 인덱스
     * @param endIndex 종료 인덱스
     * @return 체육시설 정보 DTO 목록
     */
    public List<SportsApiDto> getSportsData(int startIndex, int endIndex) {
        // 서울시 공공데이터 API URL 형식: http://openapi.seoul.go.kr:8088/{API_KEY}/json/{SERVICE_NAME}/{START_INDEX}/{END_INDEX}/
        String url = String.format("%s/%s/json/%s/%d/%d/", 
                baseUrl, apiKey, SPORTS_ENDPOINT, startIndex, endIndex);

        log.info("체육시설 정보 API 호출: {}", url);

        try {
            // API 호출
            SportsApiResponseDto response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(SportsApiResponseDto.class);

            return convertToSportsApiDto(response);

        } catch (Exception e) {
            log.error("체육시설 정보 API 호출 실패: {}", e.getMessage(), e);
            
            // 실패 시 원시 응답 확인을 위한 추가 로깅
            try {
                String rawResponse = restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(String.class);
                log.error("원시 API 응답: {}", rawResponse);
            } catch (Exception ex) {
                log.error("원시 응답 조회도 실패: {}", ex.getMessage());
            }
            
            return new ArrayList<>();
        }
    }

    /**
     * API 응답을 SportsApiDto 목록으로 변환
     * 
     * @param response API 응답 DTO
     * @return 변환된 체육시설 정보 DTO 목록
     */
    private List<SportsApiDto> convertToSportsApiDto(SportsApiResponseDto response) {
        List<SportsApiDto> sportsList = new ArrayList<>();

        if (response == null || response.getListPublicReservationSport() == null) {
            log.warn("체육시설 API 응답이 null입니다.");
            return sportsList;
        }

        SportsApiResponseDto.ListPublicReservationSport listData = response.getListPublicReservationSport();
        
        // API 응답 상태 확인
        if (listData.getResult() != null) {
            String code = listData.getResult().getCode();
            String message = listData.getResult().getMessage();
            
            if (!"INFO-000".equals(code)) {
                log.warn("체육시설 API 응답 오류: {} - {}", code, message);
                return sportsList;
            }
        }

        // 데이터 변환
        if (listData.getRow() != null && !listData.getRow().isEmpty()) {
            for (SportsApiResponseDto.SportsRow row : listData.getRow()) {
                try {
                    SportsApiDto dto = convertRowToDto(row);
                    sportsList.add(dto);
                } catch (Exception e) {
                    log.warn("체육시설 데이터 변환 오류: {} - {}", row.getSvcName(), e.getMessage());
                }
            }
        }

        log.info("체육시설 정보 변환 완료: {} 건", sportsList.size());
        return sportsList;
    }

    /**
     * SportsRow를 SportsApiDto로 변환
     */
    private SportsApiDto convertRowToDto(SportsApiResponseDto.SportsRow row) {
        return SportsApiDto.builder()
                .facilityName(row.getSvcName())
                .facilityType(row.getMinClassName())
                .address(row.getPlaceName())
                .phoneNumber(row.getTelNo())
                .operatingHours(formatOperatingHours(row.getVMin(), row.getVMax()))
                .holiday(row.getRevStdDayName())
                .feeInfo(row.getPayAtName())
                .homepageUrl(row.getSvcUrl())
                .district(row.getAreaName())
                .facilityScale(row.getUseTargetInfo())
                .parkingInfo(null) // API에서 주차 정보 제공하지 않음
                .detailContent(row.getDetailContent())
                .imageUrl(row.getImgUrl())
                .xCoordinate(row.getX())
                .yCoordinate(row.getY())
                .serviceId(row.getSvcId())
                .build();
    }

    /**
     * 운영시간 포맷팅
     */
    private String formatOperatingHours(String vMin, String vMax) {
        if (vMin == null && vMax == null) {
            return null;
        }
        if (vMin == null) {
            return "~ " + vMax;
        }
        if (vMax == null) {
            return vMin + " ~";
        }
        return vMin + " ~ " + vMax;
    }

    /**
     * 체육시설 API 응답 DTO (확장된 버전)
     */
    public static class SportsApiDto {
        private String facilityName;
        private String facilityType;
        private String address;
        private String phoneNumber;
        private String operatingHours;
        private String holiday;
        private String feeInfo;
        private String homepageUrl;
        private String district;
        private String facilityScale;
        private String parkingInfo;
        private String detailContent;
        private String imageUrl;
        private String xCoordinate;
        private String yCoordinate;
        private String serviceId;

        // Getters
        public String getFacilityName() { return facilityName; }
        public String getFacilityType() { return facilityType; }
        public String getAddress() { return address; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getOperatingHours() { return operatingHours; }
        public String getHoliday() { return holiday; }
        public String getFeeInfo() { return feeInfo; }
        public String getHomepageUrl() { return homepageUrl; }
        public String getDistrict() { return district; }
        public String getFacilityScale() { return facilityScale; }
        public String getParkingInfo() { return parkingInfo; }
        public String getDetailContent() { return detailContent; }
        public String getImageUrl() { return imageUrl; }
        public String getXCoordinate() { return xCoordinate; }
        public String getYCoordinate() { return yCoordinate; }
        public String getServiceId() { return serviceId; }

        // Builder 패턴
        public static SportsApiDtoBuilder builder() {
            return new SportsApiDtoBuilder();
        }

        public static class SportsApiDtoBuilder {
            private String facilityName;
            private String facilityType;
            private String address;
            private String phoneNumber;
            private String operatingHours;
            private String holiday;
            private String feeInfo;
            private String homepageUrl;
            private String district;
            private String facilityScale;
            private String parkingInfo;
            private String detailContent;
            private String imageUrl;
            private String xCoordinate;
            private String yCoordinate;
            private String serviceId;

            public SportsApiDtoBuilder facilityName(String facilityName) {
                this.facilityName = facilityName;
                return this;
            }

            public SportsApiDtoBuilder facilityType(String facilityType) {
                this.facilityType = facilityType;
                return this;
            }

            public SportsApiDtoBuilder address(String address) {
                this.address = address;
                return this;
            }

            public SportsApiDtoBuilder phoneNumber(String phoneNumber) {
                this.phoneNumber = phoneNumber;
                return this;
            }

            public SportsApiDtoBuilder operatingHours(String operatingHours) {
                this.operatingHours = operatingHours;
                return this;
            }

            public SportsApiDtoBuilder holiday(String holiday) {
                this.holiday = holiday;
                return this;
            }

            public SportsApiDtoBuilder feeInfo(String feeInfo) {
                this.feeInfo = feeInfo;
                return this;
            }

            public SportsApiDtoBuilder homepageUrl(String homepageUrl) {
                this.homepageUrl = homepageUrl;
                return this;
            }

            public SportsApiDtoBuilder district(String district) {
                this.district = district;
                return this;
            }

            public SportsApiDtoBuilder facilityScale(String facilityScale) {
                this.facilityScale = facilityScale;
                return this;
            }

            public SportsApiDtoBuilder parkingInfo(String parkingInfo) {
                this.parkingInfo = parkingInfo;
                return this;
            }

            public SportsApiDtoBuilder detailContent(String detailContent) {
                this.detailContent = detailContent;
                return this;
            }

            public SportsApiDtoBuilder imageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
                return this;
            }

            public SportsApiDtoBuilder xCoordinate(String xCoordinate) {
                this.xCoordinate = xCoordinate;
                return this;
            }

            public SportsApiDtoBuilder yCoordinate(String yCoordinate) {
                this.yCoordinate = yCoordinate;
                return this;
            }

            public SportsApiDtoBuilder serviceId(String serviceId) {
                this.serviceId = serviceId;
                return this;
            }

            public SportsApiDto build() {
                SportsApiDto dto = new SportsApiDto();
                dto.facilityName = this.facilityName;
                dto.facilityType = this.facilityType;
                dto.address = this.address;
                dto.phoneNumber = this.phoneNumber;
                dto.operatingHours = this.operatingHours;
                dto.holiday = this.holiday;
                dto.feeInfo = this.feeInfo;
                dto.homepageUrl = this.homepageUrl;
                dto.district = this.district;
                dto.facilityScale = this.facilityScale;
                dto.parkingInfo = this.parkingInfo;
                dto.detailContent = this.detailContent;
                dto.imageUrl = this.imageUrl;
                dto.xCoordinate = this.xCoordinate;
                dto.yCoordinate = this.yCoordinate;
                dto.serviceId = this.serviceId;
                return dto;
            }
        }
    }
}
