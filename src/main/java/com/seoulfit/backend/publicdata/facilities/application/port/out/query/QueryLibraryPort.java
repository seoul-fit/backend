package com.seoulfit.backend.publicdata.facilities.application.port.out.query;

import com.seoulfit.backend.publicdata.facilities.domain.Library;

import java.util.List;

public interface QueryLibraryPort {
    List<Library> queryLibrary();
}
