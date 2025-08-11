package com.seoulfit.backend.publicdata.facilities.adapter.out.persistence;

import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository.LibraryRepository;
import com.seoulfit.backend.publicdata.facilities.application.port.out.query.QueryLibraryPort;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class QueryLibraryAdapter implements QueryLibraryPort  {
    private final LibraryRepository libraryRepository;

    @Override
    public List<Library> queryLibrary() {
        return libraryRepository.findAll();
    }

}
