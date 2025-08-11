package com.seoulfit.backend.publicdata.facilities.adapter.in.web;

import com.seoulfit.backend.publicdata.facilities.application.port.in.query.QueryCoolingCenterUseCase;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 서울시 무더위 쉼터 웹 어댑터
 * 헥사고날 아키텍처의 Adapter In
 */
@Tag(name = "서울시 무더위 쉼터 관련 API", description = "CoolingShelterController.class")
@RequestMapping("/api/v1/cooling-shelter")
@RequiredArgsConstructor
@RestController
public class CoolingShelterController {
    private final QueryCoolingCenterUseCase queryCoolingCenterUseCase;

    @GetMapping("/all")
    public ResponseEntity<?> getAllLibrary() {
        List<CoolingCenter> allCoolingCenter = queryCoolingCenterUseCase.getAllCoolingCenter();

        return ResponseEntity.ok(allCoolingCenter);
    }
}
