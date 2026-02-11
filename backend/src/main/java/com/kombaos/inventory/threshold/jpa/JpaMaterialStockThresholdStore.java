package com.kombaos.inventory.threshold.jpa;

import com.kombaos.inventory.threshold.MaterialStockThreshold;
import com.kombaos.inventory.threshold.MaterialStockThresholdStore;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "cloud", matchIfMissing = true)
public class JpaMaterialStockThresholdStore implements MaterialStockThresholdStore {

    private final MaterialStockThresholdJpaRepository repository;

    public JpaMaterialStockThresholdStore(MaterialStockThresholdJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MaterialStockThreshold> list() {
        return repository.findAll().stream()
                .map(this::toModel)
                .sorted(Comparator.comparing(MaterialStockThreshold::materialId))
                .toList();
    }

    @Override
    public Optional<MaterialStockThreshold> getByMaterialId(String materialId) {
        return repository.findById(materialId).map(this::toModel);
    }

    @Override
    public MaterialStockThreshold upsert(String materialId, BigDecimal minStock) {
        MaterialStockThresholdEntity saved = repository.save(new MaterialStockThresholdEntity(materialId, minStock, Instant.now()));
        return toModel(saved);
    }

    @Override
    public void delete(String materialId) {
        if (!repository.existsById(materialId)) {
            throw new NoSuchElementException("Material stock threshold not found for material: " + materialId);
        }
        repository.deleteById(materialId);
    }

    private MaterialStockThreshold toModel(MaterialStockThresholdEntity entity) {
        return new MaterialStockThreshold(entity.getMaterialId(), entity.getMinStock(), entity.getUpdatedAt());
    }
}
