package com.seoulfit.backend.facilities.application.port.out;

import com.seoulfit.backend.facilities.domain.PublicLibrary;

import java.util.List;

public interface CommandPublicLibraryPort {

    void save(List<PublicLibrary> publicLibraries);

    void truncate();
}
