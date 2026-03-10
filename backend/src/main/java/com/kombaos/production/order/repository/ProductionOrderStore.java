package com.kombaos.production.order.repository;

import com.kombaos.production.order.domain.ProductionOrder;
import com.kombaos.production.order.domain.ProductionOrderStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductionOrderStore {
    List<ProductionOrder> list();

    Optional<ProductionOrder> getById(String id);

    ProductionOrder create(String productId, BigDecimal quantity, LocalDate dueDate);

    ProductionOrder updateStatus(String id, ProductionOrderStatus status);
}
