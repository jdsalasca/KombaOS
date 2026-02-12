package com.kombaos.inventory.movement.dto;

import com.kombaos.inventory.movement.domain.InventoryMovementType;
import java.math.BigDecimal;
import java.time.Instant;

public record InventoryMovementResponse(
        String id,
        String materialId,
        InventoryMovementType type,
        BigDecimal quantity,
        String reason,
        Instant createdAt
) {
}
