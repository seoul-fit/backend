package com.seoulfit.backend.publicdata.facilities.application.port.out;

import com.seoulfit.backend.location.domain.Library;

import java.util.List;

public interface CommandPublicLibraryPort {

    void save(List<Library> publicLibraries);

    void truncate();
}
