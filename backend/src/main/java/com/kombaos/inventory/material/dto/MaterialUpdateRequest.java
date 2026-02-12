package com.kombaos.inventory.material.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

public record MaterialUpdateRequest(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 50) String unit,
        @Size(max = 200) String supplier,
        @Size(max = 200) String origin,
        Boolean certified,
        @Min(0) Long costCents,
        @Pattern(regexp = "[A-Z]{3}") String currency
) {
}
