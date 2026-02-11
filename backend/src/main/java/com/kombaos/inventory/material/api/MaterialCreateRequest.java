package com.kombaos.inventory.material.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MaterialCreateRequest(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 50) String unit
) {
}
