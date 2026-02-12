package com.kombaos.inventory.movement.controller;

import com.kombaos.inventory.movement.domain.InventoryMovement;
import com.kombaos.inventory.movement.dto.InventoryMovementCreateRequest;
import com.kombaos.inventory.movement.dto.InventoryMovementResponse;
import com.kombaos.inventory.movement.dto.MaterialStockResponse;
import com.kombaos.inventory.movement.service.InventoryMovementService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryMovementController {

    private final InventoryMovementService service;

    public InventoryMovementController(InventoryMovementService service) {
        this.service = service;
    }

    @GetMapping("/api/inventory/movements")
    public List<InventoryMovementResponse> list(@RequestParam Optional<String> materialId) {
        return service.list(materialId).stream().map(InventoryMovementController::toResponse).toList();
    }

    @GetMapping("/api/inventory/movements/{id}")
    public InventoryMovementResponse getById(@PathVariable String id) {
        return toResponse(service.getById(id));
    }

    @PostMapping("/api/inventory/movements")
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryMovementResponse create(@Valid @RequestBody InventoryMovementCreateRequest request) {
        return toResponse(service.create(request.materialId(), request.type(), request.quantity(), request.reason()));
    }

    @DeleteMapping("/api/inventory/movements/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @GetMapping("/api/materials/{materialId}/stock")
    public MaterialStockResponse stock(@PathVariable String materialId) {
        return new MaterialStockResponse(materialId, service.getStock(materialId));
    }

    private static InventoryMovementResponse toResponse(InventoryMovement movement) {
        return new InventoryMovementResponse(
                movement.id(),
                movement.materialId(),
                movement.type(),
                movement.quantity(),
                movement.reason(),
                movement.createdAt()
        );
    }
}
