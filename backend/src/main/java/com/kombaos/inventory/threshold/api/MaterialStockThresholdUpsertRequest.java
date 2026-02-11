package com.kombaos.inventory.threshold.api;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record MaterialStockThresholdUpsertRequest(
        @NotNull BigDecimal minStock
) {
}
