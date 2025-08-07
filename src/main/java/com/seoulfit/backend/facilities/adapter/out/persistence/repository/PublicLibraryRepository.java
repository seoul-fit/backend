package com.seoulfit.backend.facilities.adapter.out.persistence.repository;

import com.seoulfit.backend.facilities.domain.PublicLibrary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicLibraryRepository extends JpaRepository<PublicLibrary,Long> {
}
