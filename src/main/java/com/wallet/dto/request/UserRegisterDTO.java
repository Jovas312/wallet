package com.wallet.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterDTO(
        @NotBlank(message = "firstName required")
        String firstName,
        @NotBlank(message = "lastName required")
        String lastName,
        @Email(message = "Email invalid") @NotBlank
        String email,
        @Size(min = 8, message = "Min 8 chars") @NotBlank
        String password,
        @NotBlank(message = "DocumentId required")
        String documentId
) {
}
