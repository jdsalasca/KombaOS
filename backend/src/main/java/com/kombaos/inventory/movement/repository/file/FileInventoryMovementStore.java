package com.kombaos.inventory.movement.repository.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kombaos.config.KombaosProperties;
import com.kombaos.inventory.movement.domain.InventoryMovement;
import com.kombaos.inventory.movement.domain.InventoryMovementType;
import com.kombaos.inventory.movement.repository.InventoryMovementStore;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import com.kombaos.persistence.file.FileJsonListStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "local")
public class FileInventoryMovementStore implements InventoryMovementStore {

    private static final TypeReference<List<InventoryMovement>> TYPE = new TypeReference<>() {
    };

    private final FileJsonListStore<InventoryMovement> store;

    public FileInventoryMovementStore(ObjectMapper objectMapper, KombaosProperties properties) {
        this.store = new FileJsonListStore<>(
                objectMapper,
                Path.of(properties.getLocalStorageDir()).resolve("inventory_movements.json"),
                TYPE
        );
    }

    @Override
    public List<InventoryMovement> list(Optional<String> materialId) {
        return store.withLock(() -> {
            List<InventoryMovement> all = store.readAll();
            List<InventoryMovement> filtered = materialId
                    .map(id -> all.stream().filter(m -> id.equals(m.materialId())).toList())
                    .orElse(all);
            return filtered.stream().sorted(Comparator.comparing(InventoryMovement::createdAt)).toList();
        });
    }

    @Override
    public Optional<InventoryMovement> getById(String id) {
        return store.withLock(() -> store.readAll().stream().filter(m -> m.id().equals(id)).findFirst());
    }

    @Override
    public InventoryMovement create(String materialId, InventoryMovementType type, BigDecimal quantity, String reason) {
        return store.withLock(() -> {
            List<InventoryMovement> all = store.readAll();
            InventoryMovement created = new InventoryMovement(
                    UUID.randomUUID().toString(),
                    materialId,
                    type,
                    quantity,
                    reason,
                    Instant.now()
            );
            all.add(created);
            store.writeAll(all);
            return created;
        });
    }

    @Override
    public void delete(String id) {
        store.withLock(() -> {
            List<InventoryMovement> all = store.readAll();
            List<InventoryMovement> remaining = all.stream().filter(m -> !m.id().equals(id)).toList();
            if (remaining.size() == all.size()) {
                throw new NoSuchElementException("Inventory movement not found: " + id);
            }
            store.writeAll(remaining);
        });
    }
}
