package com.kombaos.inventory.movement.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kombaos.config.KombaosProperties;
import com.kombaos.inventory.movement.InventoryMovement;
import com.kombaos.inventory.movement.InventoryMovementStore;
import com.kombaos.inventory.movement.InventoryMovementType;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "local")
public class FileInventoryMovementStore implements InventoryMovementStore {

    private static final TypeReference<List<InventoryMovement>> TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final Path storageFile;
    private final ReentrantLock lock = new ReentrantLock();

    public FileInventoryMovementStore(ObjectMapper objectMapper, KombaosProperties properties) {
        this.objectMapper = objectMapper;
        this.storageFile = Path.of(properties.getLocalStorageDir()).resolve("inventory_movements.json");
    }

    @Override
    public List<InventoryMovement> list(Optional<String> materialId) {
        lock.lock();
        try {
            List<InventoryMovement> all = readAll();
            List<InventoryMovement> filtered = materialId
                    .map(id -> all.stream().filter(m -> id.equals(m.materialId())).toList())
                    .orElse(all);
            return filtered.stream().sorted(Comparator.comparing(InventoryMovement::createdAt)).toList();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<InventoryMovement> getById(String id) {
        lock.lock();
        try {
            return readAll().stream().filter(m -> m.id().equals(id)).findFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public InventoryMovement create(String materialId, InventoryMovementType type, BigDecimal quantity, String reason) {
        lock.lock();
        try {
            List<InventoryMovement> all = readAll();
            InventoryMovement created = new InventoryMovement(
                    UUID.randomUUID().toString(),
                    materialId,
                    type,
                    quantity,
                    reason,
                    Instant.now()
            );
            all.add(created);
            writeAll(all);
            return created;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(String id) {
        lock.lock();
        try {
            List<InventoryMovement> all = readAll();
            List<InventoryMovement> remaining = all.stream().filter(m -> !m.id().equals(id)).toList();
            if (remaining.size() == all.size()) {
                throw new NoSuchElementException("Inventory movement not found: " + id);
            }
            writeAll(remaining);
        } finally {
            lock.unlock();
        }
    }

    private List<InventoryMovement> readAll() {
        try {
            if (!Files.exists(storageFile)) {
                return new ArrayList<>();
            }
            byte[] json = Files.readAllBytes(storageFile);
            if (json.length == 0) {
                return new ArrayList<>();
            }
            return new ArrayList<>(objectMapper.readValue(json, TYPE));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read inventory movements store", e);
        }
    }

    private void writeAll(List<InventoryMovement> items) {
        try {
            Files.createDirectories(storageFile.getParent());
            Path temp = storageFile.getParent().resolve(storageFile.getFileName() + ".tmp");
            byte[] json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(items);
            Files.write(temp, json);
            Files.move(temp, storageFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write inventory movements store", e);
        }
    }
}
