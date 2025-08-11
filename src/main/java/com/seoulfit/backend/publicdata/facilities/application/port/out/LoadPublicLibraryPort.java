package com.seoulfit.backend.publicdata.facilities.application.port.out;

import com.seoulfit.backend.publicdata.facilities.domain.Library;

import java.util.List;

public interface LoadPublicLibraryPort {

    List<Library> loadPublicLibrary(int startIndex, int endIndex);
}
