package com.kombaos.inventory.movement;

import java.util.List;
import java.util.Optional;

public interface InventoryMovementStore {
    List<InventoryMovement> list(Optional<String> materialId);

    Optional<InventoryMovement> getById(String id);

    InventoryMovement create(String materialId, InventoryMovementType type, java.math.BigDecimal quantity, String reason);

    void delete(String id);
}
