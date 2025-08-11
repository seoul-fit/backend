package com.seoulfit.backend.publicdata.facilities.application.service;

import com.seoulfit.backend.location.domain.Library;
import com.seoulfit.backend.publicdata.facilities.application.port.in.CommandPublicLibraryUseCase;
import com.seoulfit.backend.publicdata.facilities.application.port.out.CommandPublicLibraryPort;
import com.seoulfit.backend.publicdata.facilities.application.port.out.LoadPublicLibraryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicLibraryService implements CommandPublicLibraryUseCase {
    private final CommandPublicLibraryPort commandPublicLibraryPort;
    private final LoadPublicLibraryPort loadPublicLibraryPort;

    @Transactional
    @Override
    public void savePublicLibraryList() {
        try{
            List<Library> publicLibraryList = loadPublicLibraryPort.loadPublicLibrary(1, 1000);

            commandPublicLibraryPort.truncate();
            commandPublicLibraryPort.save(publicLibraryList);

        } catch (Exception e) {
            log.error("Error fetching Public Library API : {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch Public Library data: " + e.getMessage(), e);
        }
    }
}
