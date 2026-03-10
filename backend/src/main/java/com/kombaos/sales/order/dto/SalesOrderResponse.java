package com.kombaos.sales.order.dto;

import com.kombaos.sales.order.domain.SalesOrderStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record SalesOrderResponse(
        String id,
        String customerName,
        String customerEmail,
        String productId,
        BigDecimal quantity,
        SalesOrderStatus status,
        Instant createdAt
) {
}
