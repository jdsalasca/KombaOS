package com.kombaos.production.order.dto;

import com.kombaos.production.order.domain.ProductionOrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ProductionOrderResponse(
        String id,
        String productId,
        BigDecimal quantity,
        LocalDate dueDate,
        ProductionOrderStatus status,
        Instant createdAt
) {
}
