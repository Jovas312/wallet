package com.wallet.repository;

import com.wallet.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    boolean existsByDocumentId(String documentId);

    User getByEmail(String email);

    Optional<User> findByEmail(@NotBlank String email);
}
