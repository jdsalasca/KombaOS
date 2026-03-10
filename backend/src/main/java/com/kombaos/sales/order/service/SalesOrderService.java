package com.kombaos.sales.order.service;

import com.kombaos.catalog.product.service.ProductService;
import com.kombaos.sales.order.domain.SalesOrder;
import com.kombaos.sales.order.domain.SalesOrderStatus;
import com.kombaos.sales.order.repository.SalesOrderStore;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SalesOrderService {

    private final SalesOrderStore store;
    private final ProductService productService;

    public SalesOrderService(SalesOrderStore store, ProductService productService) {
        this.store = store;
        this.productService = productService;
    }

    public List<SalesOrder> list() {
        return store.list();
    }

    public SalesOrder getById(String id) {
        return store.getById(id).orElseThrow(() -> new java.util.NoSuchElementException("Sales order not found: " + id));
    }

    public SalesOrder create(String customerName, String customerEmail, String productId, BigDecimal quantity) {
        productService.getById(productId);
        return store.create(customerName, customerEmail, productId, quantity);
    }

    public SalesOrder updateStatus(String id, SalesOrderStatus status) {
        return store.updateStatus(id, status);
    }
}
