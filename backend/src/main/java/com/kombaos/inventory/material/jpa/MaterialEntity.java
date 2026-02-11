package com.kombaos.inventory.material.jpa;

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

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected MaterialEntity() {
    }

    public MaterialEntity(String id, String name, String unit, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.unit = unit;
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
