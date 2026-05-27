package com.wallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequestDTO(
        @NotBlank
        String destinationEmail,
        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,
        String description
) {
}
