package com.wallet.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthDTO(
        @NotBlank
        String email,
        @NotBlank
        String password
) {
}
