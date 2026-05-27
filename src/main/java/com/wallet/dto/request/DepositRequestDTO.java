package com.wallet.dto.request;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record DepositRequestDTO(
        @DecimalMin(value = "10.00")
        BigDecimal amount
) {
}
