package com.kombaos.inventory.threshold.service;

import com.kombaos.inventory.material.service.MaterialService;
import com.kombaos.inventory.threshold.domain.MaterialStockThreshold;
import com.kombaos.inventory.threshold.repository.MaterialStockThresholdStore;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class MaterialStockThresholdService {

    private final MaterialService materialService;
    private final MaterialStockThresholdStore store;

    public MaterialStockThresholdService(MaterialService materialService, MaterialStockThresholdStore store) {
        this.materialService = materialService;
        this.store = store;
    }

    public MaterialStockThreshold getByMaterialId(String materialId) {
        return store.getByMaterialId(materialId)
                .orElseThrow(() -> new NoSuchElementException("Material stock threshold not found for material: " + materialId));
    }

    public MaterialStockThreshold upsert(String materialId, BigDecimal minStock) {
        materialService.getById(materialId);

        if (minStock == null) {
            throw new IllegalArgumentException("minStock is required");
        }
        if (minStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("minStock must be >= 0");
        }

        MaterialStockThreshold saved = store.upsert(materialId, minStock);
        if (saved.updatedAt() == null) {
            return new MaterialStockThreshold(saved.materialId(), saved.minStock(), Instant.now());
        }
        return saved;
    }

    public void delete(String materialId) {
        store.delete(materialId);
    }
}
