package com.kombaos.inventory.threshold.api;

import java.math.BigDecimal;

public record LowStockAlertResponse(
        String materialId,
        String name,
        String unit,
        BigDecimal stock,
        BigDecimal minStock
) {
}
