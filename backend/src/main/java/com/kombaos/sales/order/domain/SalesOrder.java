package com.kombaos.sales.order.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record SalesOrder(
        String id,
        String customerName,
        String customerEmail,
        String productId,
        BigDecimal quantity,
        SalesOrderStatus status,
        Instant createdAt
) {
}
