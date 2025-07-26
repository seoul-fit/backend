package com.seoulfit.backend.infra;

import com.seoulfit.backend.domain.User;
import com.seoulfit.backend.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, UserStatus status);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    @Query("SELECT u FROM User u JOIN FETCH u.interests WHERE u.id = :userId AND u.status = 'ACTIVE'")
    Optional<User> findByIdWithInterests(@Param("userId") Long userId);
}
