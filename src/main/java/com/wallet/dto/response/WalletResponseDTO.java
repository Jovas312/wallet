package com.wallet.dto.response;

import com.wallet.entity.User;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletResponseDTO(
        UUID id,
        UUID userId,
        String userEmail,
        BigDecimal balance
) {
}
