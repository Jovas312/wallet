package com.wallet.dto.response;

import com.wallet.entity.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Role role,
        String documentId,
        LocalDateTime createdAt
) {
}
