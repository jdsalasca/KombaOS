package com.kombaos.sales.order.repository;

import com.kombaos.sales.order.domain.SalesOrder;
import com.kombaos.sales.order.domain.SalesOrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SalesOrderStore {
    List<SalesOrder> list();

    Optional<SalesOrder> getById(String id);

    SalesOrder create(String customerName, String customerEmail, String productId, BigDecimal quantity);

    SalesOrder updateStatus(String id, SalesOrderStatus status);
}
