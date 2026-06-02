package com.wallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepositRequestDTO(
        @DecimalMin(value = "10.00")
        @Positive
        BigDecimal amount
) {
}
