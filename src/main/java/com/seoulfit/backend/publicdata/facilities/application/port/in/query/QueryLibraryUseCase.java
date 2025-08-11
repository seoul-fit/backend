package com.seoulfit.backend.publicdata.facilities.application.port.in.query;

import com.seoulfit.backend.publicdata.facilities.domain.Library;

import java.util.List;

public interface QueryLibraryUseCase {
    List<Library> getAllLibrary();
}
