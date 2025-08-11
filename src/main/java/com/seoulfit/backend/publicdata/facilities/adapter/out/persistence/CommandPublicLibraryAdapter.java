package com.seoulfit.backend.publicdata.facilities.adapter.out.persistence;

import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository.LibraryRepository;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import com.seoulfit.backend.publicdata.facilities.application.port.out.CommandPublicLibraryPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CommandPublicLibraryAdapter implements CommandPublicLibraryPort {
    private final EntityManager entityManager;
    private final LibraryRepository libraryRepository;


    @Override
    public void save(List<Library> publicLibraries) {
        libraryRepository.saveAll(publicLibraries);
    }

    @Override
    public void truncate() {
        entityManager.createNativeQuery("TRUNCATE libraries").executeUpdate();
    }
}
