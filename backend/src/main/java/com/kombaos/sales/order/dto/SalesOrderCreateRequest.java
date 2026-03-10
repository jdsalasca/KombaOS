package com.kombaos.sales.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SalesOrderCreateRequest(
        @NotBlank String customerName,
        @NotBlank @Email String customerEmail,
        @NotBlank String productId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal quantity
) {
}
