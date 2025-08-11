package com.seoulfit.backend.publicdata.facilities.adapter.in.web;

import com.seoulfit.backend.publicdata.facilities.application.port.in.query.QueryLibraryUseCase;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "서울시 공공도서관 API", description = "LibraryController.class")
@RequestMapping("/api/v1/library")
@RequiredArgsConstructor
@RestController
public class LibraryController {

    private final QueryLibraryUseCase queryLibraryUseCase;

    @GetMapping("/all")
    public ResponseEntity<?> getAllLibrary() {
        List<Library> allLibrary = queryLibraryUseCase.getAllLibrary();

        return ResponseEntity.ok(allLibrary);
    }
}
