package com.kombaos.inventory.movement.dto;

import java.math.BigDecimal;

public record MaterialStockResponse(
        String materialId,
        BigDecimal stock
) {
}
