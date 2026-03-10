package com.kombaos.production.order.dto;

import com.kombaos.production.order.domain.ProductionOrderStatus;
import jakarta.validation.constraints.NotNull;

public record ProductionOrderUpdateStatusRequest(
        @NotNull ProductionOrderStatus status
) {
}
