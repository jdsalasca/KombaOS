package com.kombaos.inventory.threshold.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "material_stock_thresholds")
public class MaterialStockThresholdEntity {

    @Id
    @Column(name = "material_id", nullable = false, length = 64)
    private String materialId;

    @Column(name = "min_stock", nullable = false, precision = 19, scale = 6)
    private BigDecimal minStock;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected MaterialStockThresholdEntity() {
    }

    public MaterialStockThresholdEntity(String materialId, BigDecimal minStock, Instant updatedAt) {
        this.materialId = materialId;
        this.minStock = minStock;
        this.updatedAt = updatedAt;
    }

    public String getMaterialId() {
        return materialId;
    }

    public BigDecimal getMinStock() {
        return minStock;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
