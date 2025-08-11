package com.seoulfit.backend.publicdata.facilities.application.service.query;

import com.seoulfit.backend.publicdata.facilities.application.port.in.query.QueryLibraryUseCase;
import com.seoulfit.backend.publicdata.facilities.application.port.out.query.QueryLibraryPort;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class QueryLibraryService implements QueryLibraryUseCase {
    private final QueryLibraryPort queryLibraryPort;

    @Override
    public List<Library> getAllLibrary() {
        List<Library> libraries = queryLibraryPort.queryLibrary();
        log.info("Library Count : {}", libraries);

        return libraries;
    }
}
