package com.kombaos.inventory.movement.dto;

import com.kombaos.inventory.movement.domain.InventoryMovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record InventoryMovementCreateRequest(
        @NotBlank String materialId,
        @NotNull InventoryMovementType type,
        @NotNull BigDecimal quantity,
        @Size(max = 200) String reason
) {
}
