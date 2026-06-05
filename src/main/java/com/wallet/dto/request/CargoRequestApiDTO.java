package com.wallet.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CargoRequestApiDTO(
        String method,
        BigDecimal amount,
        String description,
        @JsonProperty("source_id")
        String sourceId,
        @JsonProperty("device_session_id")
        String deviceSessionId,
        CustomerRequestDTO customer
) {
}
