package com.seoulfit.backend.publicdata.facilities.application.port.out;

import com.seoulfit.backend.publicdata.facilities.domain.PublicLibrary;

import java.util.List;

public interface CommandPublicLibraryPort {

    void save(List<PublicLibrary> publicLibraries);

    void truncate();
}
