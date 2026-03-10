package com.kombaos.sales.order.controller;

import com.kombaos.sales.order.domain.SalesOrder;
import com.kombaos.sales.order.dto.SalesOrderCreateRequest;
import com.kombaos.sales.order.dto.SalesOrderResponse;
import com.kombaos.sales.order.dto.SalesOrderUpdateStatusRequest;
import com.kombaos.sales.order.service.SalesOrderService;
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
public class SalesOrderController {

    private final SalesOrderService service;

    public SalesOrderController(SalesOrderService service) {
        this.service = service;
    }

    @PostMapping("/api/public/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public SalesOrderResponse createPublicOrder(@Valid @RequestBody SalesOrderCreateRequest request) {
        return toResponse(service.create(
                request.customerName(),
                request.customerEmail(),
                request.productId(),
                request.quantity()
        ));
    }

    @GetMapping("/api/public/orders/{id}")
    public SalesOrderResponse getPublicOrder(@PathVariable String id) {
        return toResponse(service.getById(id));
    }

    @GetMapping("/api/sales/orders")
    public List<SalesOrderResponse> listBackofficeOrders() {
        return service.list().stream().map(SalesOrderController::toResponse).toList();
    }

    @PutMapping("/api/sales/orders/{id}/status")
    public SalesOrderResponse updateOrderStatus(@PathVariable String id, @Valid @RequestBody SalesOrderUpdateStatusRequest request) {
        return toResponse(service.updateStatus(id, request.status()));
    }

    private static SalesOrderResponse toResponse(SalesOrder order) {
        return new SalesOrderResponse(
                order.id(),
                order.customerName(),
                order.customerEmail(),
                order.productId(),
                order.quantity(),
                order.status(),
                order.createdAt()
        );
    }
}
