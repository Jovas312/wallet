package com.wallet.dto.response;

import com.wallet.entity.enums.Status;
import com.wallet.entity.enums.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponseDTO(
        UUID id,
        Type type,
        BigDecimal amount,
        String sourceUserEmail,
        String destinationUserEmail,
        Status status,
        LocalDateTime createdAt

) {
}
