package com.kombaos.production.order.service;

import com.kombaos.production.order.domain.ProductionOrder;
import com.kombaos.production.order.domain.ProductionOrderStatus;
import com.kombaos.production.order.repository.ProductionOrderStore;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductionOrderService {

    private final ProductionOrderStore store;

    public ProductionOrderService(ProductionOrderStore store) {
        this.store = store;
    }

    public List<ProductionOrder> list() {
        return store.list();
    }

    public ProductionOrder getById(String id) {
        return store.getById(id).orElseThrow(() -> new java.util.NoSuchElementException("Production order not found: " + id));
    }

    public ProductionOrder create(String productId, BigDecimal quantity, LocalDate dueDate) {
        return store.create(productId, quantity, dueDate);
    }

    public ProductionOrder updateStatus(String id, ProductionOrderStatus status) {
        return store.updateStatus(id, status);
    }
}
