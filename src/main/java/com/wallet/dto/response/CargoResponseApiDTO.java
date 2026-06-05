package com.wallet.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CargoResponseApiDTO(
        String id,
        BigDecimal amount,
        String authorization,
        String status,
        @JsonProperty("creation_date")
        OffsetDateTime creationDate
) {
}
