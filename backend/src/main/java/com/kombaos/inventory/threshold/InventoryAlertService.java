package com.kombaos.inventory.threshold;

import com.kombaos.inventory.material.Material;
import com.kombaos.inventory.material.MaterialService;
import com.kombaos.inventory.movement.InventoryMovementService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InventoryAlertService {

    public record LowStockAlert(
            String materialId,
            String name,
            String unit,
            BigDecimal stock,
            BigDecimal minStock
    ) {
    }

    private final MaterialService materialService;
    private final InventoryMovementService movementService;
    private final MaterialStockThresholdStore thresholdStore;

    public InventoryAlertService(
            MaterialService materialService,
            InventoryMovementService movementService,
            MaterialStockThresholdStore thresholdStore
    ) {
        this.materialService = materialService;
        this.movementService = movementService;
        this.thresholdStore = thresholdStore;
    }

    public List<LowStockAlert> lowStockAlerts() {
        List<LowStockAlert> alerts = new ArrayList<>();
        for (MaterialStockThreshold t : thresholdStore.list()) {
            BigDecimal min = t.minStock();
            if (min == null || min.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            Material material = materialService.getById(t.materialId());
            BigDecimal stock = movementService.getStock(t.materialId());
            if (stock.compareTo(min) < 0) {
                alerts.add(new LowStockAlert(material.id(), material.name(), material.unit(), stock, min));
            }
        }
        alerts.sort(Comparator.comparing(LowStockAlert::materialId));
        return alerts;
    }
}
