package com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository;

import com.seoulfit.backend.location.domain.Library;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicLibraryRepository extends JpaRepository<Library,Long> {
}
