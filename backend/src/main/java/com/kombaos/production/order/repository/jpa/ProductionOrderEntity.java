package com.kombaos.production.order.repository.jpa;

import com.kombaos.production.order.domain.ProductionOrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "production_orders")
public class ProductionOrderEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 50)
    private String productId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductionOrderStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    protected ProductionOrderEntity() {
    }

    public ProductionOrderEntity(String id, String productId, BigDecimal quantity, LocalDate dueDate, ProductionOrderStatus status, Instant createdAt) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.dueDate = dueDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public ProductionOrderStatus getStatus() {
        return status;
    }

    public void setStatus(ProductionOrderStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
