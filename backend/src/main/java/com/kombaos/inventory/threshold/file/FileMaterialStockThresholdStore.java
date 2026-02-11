package com.kombaos.inventory.threshold.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kombaos.config.KombaosProperties;
import com.kombaos.inventory.threshold.MaterialStockThreshold;
import com.kombaos.inventory.threshold.MaterialStockThresholdStore;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import com.kombaos.persistence.file.FileJsonListStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "local")
public class FileMaterialStockThresholdStore implements MaterialStockThresholdStore {

    private static final TypeReference<List<MaterialStockThreshold>> TYPE = new TypeReference<>() {
    };

    private final FileJsonListStore<MaterialStockThreshold> store;

    public FileMaterialStockThresholdStore(ObjectMapper objectMapper, KombaosProperties properties) {
        this.store = new FileJsonListStore<>(
                objectMapper,
                Path.of(properties.getLocalStorageDir()).resolve("material_stock_thresholds.json"),
                TYPE
        );
    }

    @Override
    public List<MaterialStockThreshold> list() {
        return store.withLock(() -> {
            List<MaterialStockThreshold> all = store.readAll();
            return all.stream().sorted(Comparator.comparing(MaterialStockThreshold::materialId)).toList();
        });
    }

    @Override
    public Optional<MaterialStockThreshold> getByMaterialId(String materialId) {
        return store.withLock(() -> store.readAll().stream().filter(t -> t.materialId().equals(materialId)).findFirst());
    }

    @Override
    public MaterialStockThreshold upsert(String materialId, BigDecimal minStock) {
        return store.withLock(() -> {
            List<MaterialStockThreshold> all = store.readAll();
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
            store.writeAll(updated);
            return result;
        });
    }

    @Override
    public void delete(String materialId) {
        store.withLock(() -> {
            List<MaterialStockThreshold> all = store.readAll();
            List<MaterialStockThreshold> remaining = all.stream().filter(t -> !t.materialId().equals(materialId)).toList();
            if (remaining.size() == all.size()) {
                throw new NoSuchElementException("Material stock threshold not found for material: " + materialId);
            }
            store.writeAll(remaining);
        });
    }
}
