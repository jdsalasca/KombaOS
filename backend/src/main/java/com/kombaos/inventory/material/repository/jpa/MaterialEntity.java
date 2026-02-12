package com.kombaos.inventory.material.repository.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "materials")
public class MaterialEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 50)
    private String unit;

    @Column(length = 200)
    private String supplier;

    @Column(length = 200)
    private String origin;

    @Column(nullable = false)
    private boolean certified;

    @Column(name = "cost_cents")
    private Long costCents;

    @Column(length = 3)
    private String currency;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected MaterialEntity() {
    }

    public MaterialEntity(
            String id,
            String name,
            String unit,
            String supplier,
            String origin,
            boolean certified,
            Long costCents,
            String currency,
            Instant createdAt
    ) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.supplier = supplier;
        this.origin = origin;
        this.certified = certified;
        this.costCents = costCents;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public boolean isCertified() {
        return certified;
    }

    public void setCertified(boolean certified) {
        this.certified = certified;
    }

    public Long getCostCents() {
        return costCents;
    }

    public void setCostCents(Long costCents) {
        this.costCents = costCents;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
