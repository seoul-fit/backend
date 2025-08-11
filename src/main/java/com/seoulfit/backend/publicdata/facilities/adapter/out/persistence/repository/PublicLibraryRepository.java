package com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository;

import com.seoulfit.backend.publicdata.facilities.domain.PublicLibrary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicLibraryRepository extends JpaRepository<PublicLibrary,Long> {
}
