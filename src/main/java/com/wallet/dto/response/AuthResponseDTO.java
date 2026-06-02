package com.wallet.dto.response;

import com.wallet.entity.enums.Role;

public record AuthResponseDTO(
        String token,
        String email,
        Role role
) {
}
