package com.kombaos.inventory.threshold.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kombaos.config.KombaosProperties;
import com.kombaos.inventory.threshold.MaterialStockThreshold;
import com.kombaos.inventory.threshold.MaterialStockThresholdStore;
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
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "local")
public class FileMaterialStockThresholdStore implements MaterialStockThresholdStore {

    private static final TypeReference<List<MaterialStockThreshold>> TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final Path storageFile;
    private final ReentrantLock lock = new ReentrantLock();

    public FileMaterialStockThresholdStore(ObjectMapper objectMapper, KombaosProperties properties) {
        this.objectMapper = objectMapper;
        this.storageFile = Path.of(properties.getLocalStorageDir()).resolve("material_stock_thresholds.json");
    }

    @Override
    public List<MaterialStockThreshold> list() {
        lock.lock();
        try {
            List<MaterialStockThreshold> all = readAll();
            return all.stream().sorted(Comparator.comparing(MaterialStockThreshold::materialId)).toList();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<MaterialStockThreshold> getByMaterialId(String materialId) {
        lock.lock();
        try {
            return readAll().stream().filter(t -> t.materialId().equals(materialId)).findFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public MaterialStockThreshold upsert(String materialId, BigDecimal minStock) {
        lock.lock();
        try {
            List<MaterialStockThreshold> all = readAll();
            List<MaterialStockThreshold> updated = new ArrayList<>(all.size() + 1);
            MaterialStockThreshold result = null;
            for (MaterialStockThreshold t : all) {
                if (t.materialId().equals(materialId)) {
                    result = new MaterialStockThreshold(materialId, minStock, Instant.now());
                    updated.add(result);
                } else {
                    updated.add(t);
                }
            }
            if (result == null) {
                result = new MaterialStockThreshold(materialId, minStock, Instant.now());
                updated.add(result);
            }
            writeAll(updated);
            return result;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(String materialId) {
        lock.lock();
        try {
            List<MaterialStockThreshold> all = readAll();
            List<MaterialStockThreshold> remaining = all.stream().filter(t -> !t.materialId().equals(materialId)).toList();
            if (remaining.size() == all.size()) {
                throw new NoSuchElementException("Material stock threshold not found for material: " + materialId);
            }
            writeAll(remaining);
        } finally {
            lock.unlock();
        }
    }

    private List<MaterialStockThreshold> readAll() {
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
            throw new IllegalStateException("Failed to read material stock thresholds store", e);
        }
    }

    private void writeAll(List<MaterialStockThreshold> items) {
        try {
            Files.createDirectories(storageFile.getParent());
            Path temp = storageFile.getParent().resolve(storageFile.getFileName() + ".tmp");
            byte[] json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(items);
            Files.write(temp, json);
            Files.move(temp, storageFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write material stock thresholds store", e);
        }
    }
}
