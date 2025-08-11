package com.seoulfit.backend.publicdata.facilities.application.port.out;

import com.seoulfit.backend.publicdata.facilities.domain.PublicLibrary;

import java.util.List;

public interface LoadPublicLibraryPort {

    List<PublicLibrary> loadPublicLibrary(int startIndex, int endIndex);
}
