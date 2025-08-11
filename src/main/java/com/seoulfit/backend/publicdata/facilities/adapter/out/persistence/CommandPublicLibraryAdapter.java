package com.seoulfit.backend.publicdata.facilities.adapter.out.persistence;

import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository.PublicLibraryRepository;
import com.seoulfit.backend.publicdata.facilities.application.port.out.CommandPublicLibraryPort;
import com.seoulfit.backend.publicdata.facilities.domain.PublicLibrary;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CommandPublicLibraryAdapter implements CommandPublicLibraryPort {
    private final EntityManager entityManager;
    private final PublicLibraryRepository publicLibraryRepository;


    @Override
    public void save(List<PublicLibrary> publicLibraries) {
        publicLibraryRepository.saveAll(publicLibraries);
    }

    @Override
    public void truncate() {
        entityManager.createNativeQuery("TRUNCATE public_library").executeUpdate();
    }
}
