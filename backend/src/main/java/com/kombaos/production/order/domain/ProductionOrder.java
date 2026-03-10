package com.kombaos.production.order.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ProductionOrder(
        String id,
        String productId,
        BigDecimal quantity,
        LocalDate dueDate,
        ProductionOrderStatus status,
        Instant createdAt
) {
}
