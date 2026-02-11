package com.kombaos.inventory.threshold.api;

import com.kombaos.inventory.threshold.InventoryAlertService;
import com.kombaos.inventory.threshold.MaterialStockThreshold;
import com.kombaos.inventory.threshold.MaterialStockThresholdService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MaterialStockThresholdController {

    private final MaterialStockThresholdService thresholdService;
    private final InventoryAlertService alertService;

    public MaterialStockThresholdController(MaterialStockThresholdService thresholdService, InventoryAlertService alertService) {
        this.thresholdService = thresholdService;
        this.alertService = alertService;
    }

    @GetMapping("/api/materials/{materialId}/threshold")
    public MaterialStockThresholdResponse get(@PathVariable String materialId) {
        return toResponse(thresholdService.getByMaterialId(materialId));
    }

    @PutMapping("/api/materials/{materialId}/threshold")
    public MaterialStockThresholdResponse upsert(
            @PathVariable String materialId,
            @Valid @RequestBody MaterialStockThresholdUpsertRequest request
    ) {
        return toResponse(thresholdService.upsert(materialId, request.minStock()));
    }

    @DeleteMapping("/api/materials/{materialId}/threshold")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String materialId) {
        thresholdService.delete(materialId);
    }

    @GetMapping("/api/inventory/alerts/low-stock")
    public List<LowStockAlertResponse> lowStock() {
        return alertService.lowStockAlerts().stream()
                .map(a -> new LowStockAlertResponse(a.materialId(), a.name(), a.unit(), a.stock(), a.minStock()))
                .toList();
    }

    private static MaterialStockThresholdResponse toResponse(MaterialStockThreshold threshold) {
        return new MaterialStockThresholdResponse(threshold.materialId(), threshold.minStock(), threshold.updatedAt());
    }
}

