package com.kombaos.inventory.movement.jpa;

import com.kombaos.inventory.movement.InventoryMovement;
import com.kombaos.inventory.movement.InventoryMovementStore;
import com.kombaos.inventory.movement.InventoryMovementType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "cloud", matchIfMissing = true)
public class JpaInventoryMovementStore implements InventoryMovementStore {

    private final InventoryMovementJpaRepository repository;

    public JpaInventoryMovementStore(InventoryMovementJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<InventoryMovement> list(Optional<String> materialId) {
        List<InventoryMovementEntity> entities = materialId
                .map(repository::findAllByMaterialId)
                .orElseGet(repository::findAll);

        return entities.stream()
                .map(this::toModel)
                .sorted(Comparator.comparing(InventoryMovement::createdAt))
                .toList();
    }

    @Override
    public Optional<InventoryMovement> getById(String id) {
        return repository.findById(id).map(this::toModel);
    }

    @Override
    public InventoryMovement create(String materialId, InventoryMovementType type, BigDecimal quantity, String reason) {
        InventoryMovementEntity saved = repository.save(new InventoryMovementEntity(
                UUID.randomUUID().toString(),
                materialId,
                type,
                quantity,
                reason,
                Instant.now()
        ));
        return toModel(saved);
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Inventory movement not found: " + id);
        }
        repository.deleteById(id);
    }

    private InventoryMovement toModel(InventoryMovementEntity entity) {
        return new InventoryMovement(
                entity.getId(),
                entity.getMaterialId(),
                entity.getType(),
                entity.getQuantity(),
                entity.getReason(),
                entity.getCreatedAt()
        );
    }
}
