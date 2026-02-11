package com.kombaos.inventory.movement.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementJpaRepository extends JpaRepository<InventoryMovementEntity, String> {
    List<InventoryMovementEntity> findAllByMaterialId(String materialId);
}
