package com.kombaos.inventory.threshold;

import java.math.BigDecimal;
import java.time.Instant;

public record MaterialStockThreshold(
        String materialId,
        BigDecimal minStock,
        Instant updatedAt
) {
}
