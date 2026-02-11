package com.kombaos.inventory.threshold;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MaterialStockThresholdStore {
    List<MaterialStockThreshold> list();

    Optional<MaterialStockThreshold> getByMaterialId(String materialId);

    MaterialStockThreshold upsert(String materialId, BigDecimal minStock);

    void delete(String materialId);
}
