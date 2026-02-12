package com.kombaos.inventory.threshold.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record MaterialStockThresholdResponse(
        String materialId,
        BigDecimal minStock,
        Instant updatedAt
) {
}
