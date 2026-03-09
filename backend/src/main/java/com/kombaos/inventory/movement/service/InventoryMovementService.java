package com.kombaos.inventory.movement.service;

import com.kombaos.inventory.material.service.MaterialService;
import com.kombaos.inventory.movement.domain.InventoryMovement;
import com.kombaos.inventory.movement.domain.InventoryMovementType;
import com.kombaos.inventory.movement.repository.InventoryMovementStore;
import com.kombaos.inventory.threshold.repository.MaterialStockThresholdStore;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class InventoryMovementService {

    private final InventoryMovementStore store;
    private final MaterialService materialService;
    private final MaterialStockThresholdStore thresholdStore;

    public InventoryMovementService(
            InventoryMovementStore store,
            MaterialService materialService,
            MaterialStockThresholdStore thresholdStore
    ) {
        this.store = store;
        this.materialService = materialService;
        this.thresholdStore = thresholdStore;
    }

    public List<InventoryMovement> list(Optional<String> materialId) {
        return store.list(materialId).stream()
                .sorted(Comparator.comparing(InventoryMovement::createdAt))
                .toList();
    }

    public InventoryMovement getById(String id) {
        return store.getById(id).orElseThrow(() -> new NoSuchElementException("Inventory movement not found: " + id));
    }

    public InventoryMovement create(String materialId, InventoryMovementType type, BigDecimal quantity, String reason) {
        if (materialId == null || materialId.isBlank()) {
            throw new IllegalArgumentException("materialId is required");
        }
        materialService.getById(materialId);

        if (quantity == null) {
            throw new IllegalArgumentException("Quantity is required");
        }
        if (quantity.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Quantity must not be zero");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type is required");
        }
        if ((type == InventoryMovementType.IN || type == InventoryMovementType.OUT) && quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity must be positive for IN/OUT movements");
        }
        if ((type == InventoryMovementType.OUT || type == InventoryMovementType.ADJUST)
                && (reason == null || reason.isBlank())) {
            throw new IllegalArgumentException("Reason is required for OUT/ADJUST movements");
        }

        BigDecimal current = getStock(materialId);
        BigDecimal delta = switch (type) {
            case IN -> quantity;
            case OUT -> quantity.negate();
            case ADJUST -> quantity;
        };
        BigDecimal next = current.add(delta);
        if (next.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient stock for material: " + materialId);
        }

        if (type == InventoryMovementType.OUT) {
            thresholdStore.getByMaterialId(materialId).ifPresent(threshold -> {
                if (next.compareTo(threshold.minStock()) < 0) {
                    throw new IllegalArgumentException("Movement would leave stock below minimum threshold for material: " + materialId);
                }
            });
        }

        return store.create(materialId, type, quantity, reason);
    }

    public void delete(String id) {
        store.delete(id);
    }

    public BigDecimal getStock(String materialId) {
        materialService.getById(materialId);

        List<InventoryMovement> movements = store.list(Optional.of(materialId));
        BigDecimal stock = BigDecimal.ZERO;
        for (InventoryMovement m : movements) {
            BigDecimal q = m.quantity();
            if (q == null) {
                continue;
            }
            stock = switch (m.type()) {
                case IN -> stock.add(q);
                case OUT -> stock.subtract(q);
                case ADJUST -> stock.add(q);
            };
        }
        return stock;
    }
}
