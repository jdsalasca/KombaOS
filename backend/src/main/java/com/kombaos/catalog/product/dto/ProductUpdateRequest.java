package com.kombaos.catalog.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductUpdateRequest(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 2000) String description,
        @Min(0) @Max(9_999_999_99L) long priceCents,
        @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency,
        boolean active
) {
}
