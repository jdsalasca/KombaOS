package com.kombaos.inventory.movement.jpa;

import com.kombaos.inventory.movement.InventoryMovementType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovementEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String id;

    @Column(name = "material_id", nullable = false, length = 64)
    private String materialId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InventoryMovementType type;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;

    @Column(length = 200)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected InventoryMovementEntity() {
    }

    public InventoryMovementEntity(
            String id,
            String materialId,
            InventoryMovementType type,
            BigDecimal quantity,
            String reason,
            Instant createdAt
    ) {
        this.id = id;
        this.materialId = materialId;
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getMaterialId() {
        return materialId;
    }

    public InventoryMovementType getType() {
        return type;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public String getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
