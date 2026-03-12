package com.kombaos.sales.order.repository.jpa;

import com.kombaos.sales.order.domain.SalesOrder;
import com.kombaos.sales.order.domain.SalesOrderStatus;
import com.kombaos.sales.order.repository.SalesOrderStore;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "cloud", matchIfMissing = true)
public class JpaSalesOrderStore implements SalesOrderStore {

    private final SalesOrderJpaRepository repository;

    public JpaSalesOrderStore(SalesOrderJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<SalesOrder> list() {
        return repository.findAll().stream()
                .map(this::toModel)
                .sorted(Comparator.comparing(SalesOrder::createdAt))
                .toList();
    }

    @Override
    public Optional<SalesOrder> getById(String id) {
        return repository.findById(id).map(this::toModel);
    }

    @Override
    public SalesOrder create(String customerName, String customerEmail, String productId, BigDecimal quantity) {
        SalesOrderEntity saved = repository.save(new SalesOrderEntity(
                UUID.randomUUID().toString(),
                customerName,
                customerEmail,
                productId,
                quantity,
                SalesOrderStatus.PENDING,
                Instant.now()
        ));
        return toModel(saved);
    }

    @Override
    public SalesOrder updateStatus(String id, SalesOrderStatus status) {
        SalesOrderEntity entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Sales order not found: " + id));
        entity.setStatus(status);
        return toModel(repository.save(entity));
    }

    private SalesOrder toModel(SalesOrderEntity entity) {
        return new SalesOrder(
                entity.getId(),
                entity.getCustomerName(),
                entity.getCustomerEmail(),
                entity.getProductId(),
                entity.getQuantity(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
