package com.seoulfit.backend.facilities.application.port.out;

import com.seoulfit.backend.facilities.domain.PublicLibrary;

import java.util.List;

public interface LoadPublicLibraryPort {

    List<PublicLibrary> loadPublicLibrary(int startIndex, int endIndex);
}
