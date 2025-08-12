package com.seoulfit.backend.publicdata.park.application.service.batch;

import com.seoulfit.backend.publicdata.park.adapter.out.api.dto.SeoulParkApiResponse;
import com.seoulfit.backend.publicdata.park.application.port.in.ParkBatchUseCase;
import com.seoulfit.backend.publicdata.park.application.port.out.ParkCommandPort;
import com.seoulfit.backend.publicdata.park.application.port.out.SeoulParkApiClient;
import com.seoulfit.backend.publicdata.park.domain.Park;
import com.seoulfit.backend.publicdata.park.infrastructure.mapper.ParkMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 서울시 공원 정보 배치 처리 서비스
 * 헥사고날 아키텍처의 애플리케이션 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ParkBatchService implements ParkBatchUseCase {
    private final SeoulParkApiClient apiClient;
    private final ParkCommandPort parkCommandPort;


    @Override
    public void processDailyBatch() {
        log.info("서울시 공원 정보 일일 배치 처리 시작 - 날짜: {}", LocalDate.now());

        try {
            // 1. API에서 공원 정보 조회
            SeoulParkApiResponse apiResponse = apiClient.fetchAllParkInfo();

            if(!apiResponse.isSuccess())
                throw new RuntimeException("서울시 공원 API 호출 Error");

            List<SeoulParkApiResponse.ParkInfo> parkInfoList = apiResponse.getParkInfoList();
            log.info("공원 API에서 {} 개의 공원 정보 조회 완료", parkInfoList.size());

            List<Park> parks = ParkMapper.mapToEntity(parkInfoList);

            parkCommandPort.truncate();

            // 새 데이터 save
            List<Park> saveAllPark = parkCommandPort.saveAllPark(parks);
            log.info("공원 정보 저장 개수 Count : {}", saveAllPark.size());

        } catch (Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }

    }

}
