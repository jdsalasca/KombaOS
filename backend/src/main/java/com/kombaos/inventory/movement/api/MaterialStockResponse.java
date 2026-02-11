package com.kombaos.inventory.movement.api;

import java.math.BigDecimal;

public record MaterialStockResponse(
        String materialId,
        BigDecimal stock
) {
}
