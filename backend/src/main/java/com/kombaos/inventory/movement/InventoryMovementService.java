package com.kombaos.inventory.movement;

import com.kombaos.inventory.material.MaterialService;
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

    public InventoryMovementService(InventoryMovementStore store, MaterialService materialService) {
        this.store = store;
        this.materialService = materialService;
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
