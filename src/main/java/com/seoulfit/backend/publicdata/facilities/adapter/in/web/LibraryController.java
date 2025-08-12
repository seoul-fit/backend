package com.seoulfit.backend.publicdata.facilities.adapter.in.web;

import com.seoulfit.backend.publicdata.facilities.application.port.in.query.QueryLibraryUseCase;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "서울시 공공도서관 API", description = "LibraryController.class")
@RequestMapping("/api/v1/library")
@RequiredArgsConstructor
@RestController
public class LibraryController {

    private final QueryLibraryUseCase queryLibraryUseCase;

    @Operation(summary = "공공 도서관 조회 API.", description = "공공 도서관 정보 전체 조회")
    @GetMapping("/all")
    public ResponseEntity<?> getAllLibrary() {
        List<Library> allLibrary = queryLibraryUseCase.getAllLibrary();

        return ResponseEntity.ok(allLibrary);
    }

    @Operation(summary = "위도, 경도 기반 2km 내에 공공 도서관 정보를 가져온다.", description = "공공 도서관 조회(반경2km)")
    @GetMapping("/nearby")
    public ResponseEntity<?> getLibraryNearby(
            @RequestParam String latitude,
            @RequestParam String longitude
    ) {
        return ResponseEntity.ok(queryLibraryUseCase.getLibraryByLatitudeAndLongitude(latitude, longitude));
    }
}
