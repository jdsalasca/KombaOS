package com.kombaos.production.order.controller;

import com.kombaos.production.order.domain.ProductionOrder;
import com.kombaos.production.order.dto.ProductionOrderCreateRequest;
import com.kombaos.production.order.dto.ProductionOrderResponse;
import com.kombaos.production.order.dto.ProductionOrderUpdateStatusRequest;
import com.kombaos.production.order.service.ProductionOrderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/production/orders")
public class ProductionOrderController {

    private final ProductionOrderService service;

    public ProductionOrderController(ProductionOrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductionOrderResponse> list() {
        return service.list().stream().map(ProductionOrderController::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ProductionOrderResponse getById(@PathVariable String id) {
        return toResponse(service.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductionOrderResponse create(@Valid @RequestBody ProductionOrderCreateRequest request) {
        return toResponse(service.create(request.productId(), request.quantity(), request.dueDate()));
    }

    @PutMapping("/{id}/status")
    public ProductionOrderResponse updateStatus(@PathVariable String id, @Valid @RequestBody ProductionOrderUpdateStatusRequest request) {
        return toResponse(service.updateStatus(id, request.status()));
    }

    private static ProductionOrderResponse toResponse(ProductionOrder order) {
        return new ProductionOrderResponse(
                order.id(),
                order.productId(),
                order.quantity(),
                order.dueDate(),
                order.status(),
                order.createdAt()
        );
    }
}
