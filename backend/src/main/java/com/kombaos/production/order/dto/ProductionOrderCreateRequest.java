package com.kombaos.production.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductionOrderCreateRequest(
        @NotBlank String productId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal quantity,
        LocalDate dueDate
) {
}
