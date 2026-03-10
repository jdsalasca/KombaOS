package com.kombaos.sales.order.dto;

import com.kombaos.sales.order.domain.SalesOrderStatus;
import jakarta.validation.constraints.NotNull;

public record SalesOrderUpdateStatusRequest(
        @NotNull SalesOrderStatus status
) {
}
