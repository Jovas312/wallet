package com.wallet.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String documentId,
        LocalDateTime createdAt
) {
}
