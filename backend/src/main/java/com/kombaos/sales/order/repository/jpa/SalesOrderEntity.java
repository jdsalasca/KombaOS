package com.kombaos.sales.order.repository.jpa;

import com.kombaos.sales.order.domain.SalesOrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "sales_orders")
public class SalesOrderEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 200)
    private String customerName;

    @Column(nullable = false, length = 200)
    private String customerEmail;

    @Column(nullable = false, length = 50)
    private String productId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SalesOrderStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    protected SalesOrderEntity() {
    }

    public SalesOrderEntity(String id, String customerName, String customerEmail, String productId, BigDecimal quantity, SalesOrderStatus status, Instant createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getProductId() {
        return productId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public SalesOrderStatus getStatus() {
        return status;
    }

    public void setStatus(SalesOrderStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
