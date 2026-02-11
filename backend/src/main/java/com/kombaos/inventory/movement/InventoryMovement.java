package com.kombaos.inventory.movement;

import java.math.BigDecimal;
import java.time.Instant;

public record InventoryMovement(
        String id,
        String materialId,
        InventoryMovementType type,
        BigDecimal quantity,
        String reason,
        Instant createdAt
) {
}
