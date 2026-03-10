package com.kombaos.traceability.controller;

import com.kombaos.catalog.product.domain.Product;
import com.kombaos.catalog.product.service.ProductService;
import com.kombaos.inventory.material.domain.Material;
import com.kombaos.inventory.material.service.MaterialService;
import com.kombaos.production.order.domain.ProductionOrder;
import com.kombaos.production.order.service.ProductionOrderService;
import java.time.Instant;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TraceabilityController {

    private final ProductionOrderService productionOrderService;
    private final ProductService productService;
    private final MaterialService materialService;

    public TraceabilityController(ProductionOrderService productionOrderService,
                                  ProductService productService,
                                  MaterialService materialService) {
        this.productionOrderService = productionOrderService;
        this.productService = productService;
        this.materialService = materialService;
    }

    @GetMapping("/api/traceability/production-orders/{orderId}")
    public ProductionOrderTraceabilityResponse productionOrderTraceability(@PathVariable String orderId) {
        ProductionOrder order = productionOrderService.getById(orderId);
        Product product = productService.getById(order.productId());
        List<MaterialTraceabilityItem> materials = materialService.list().stream()
                .map(m -> new MaterialTraceabilityItem(m.id(), m.name(), m.supplier(), m.origin(), m.certified()))
                .toList();

        return new ProductionOrderTraceabilityResponse(
                new ProductionOrderTraceabilityItem(order.id(), order.productId(), order.quantity(), order.dueDate(), order.status(), order.createdAt()),
                new ProductTraceabilityItem(product.id(), product.name(), product.description(), product.active()),
                materials,
                Instant.now()
        );
    }

    public record ProductionOrderTraceabilityResponse(
            ProductionOrderTraceabilityItem order,
            ProductTraceabilityItem product,
            List<MaterialTraceabilityItem> materials,
            Instant generatedAt
    ) {
    }

    public record ProductionOrderTraceabilityItem(
            String id,
            String productId,
            java.math.BigDecimal quantity,
            java.time.LocalDate dueDate,
            com.kombaos.production.order.domain.ProductionOrderStatus status,
            Instant createdAt
    ) {
    }

    public record ProductTraceabilityItem(
            String id,
            String name,
            String description,
            boolean active
    ) {
    }

    public record MaterialTraceabilityItem(
            String id,
            String name,
            String supplier,
            String origin,
            boolean certified
    ) {
    }
}
