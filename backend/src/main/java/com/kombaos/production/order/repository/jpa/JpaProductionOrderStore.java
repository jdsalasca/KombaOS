package com.kombaos.production.order.repository.jpa;

import com.kombaos.production.order.domain.ProductionOrder;
import com.kombaos.production.order.domain.ProductionOrderStatus;
import com.kombaos.production.order.repository.ProductionOrderStore;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "cloud", matchIfMissing = true)
public class JpaProductionOrderStore implements ProductionOrderStore {

    private final ProductionOrderJpaRepository repository;

    public JpaProductionOrderStore(ProductionOrderJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ProductionOrder> list() {
        return repository.findAll().stream()
                .map(this::toModel)
                .sorted(Comparator.comparing(ProductionOrder::createdAt))
                .toList();
    }

    @Override
    public Optional<ProductionOrder> getById(String id) {
        return repository.findById(id).map(this::toModel);
    }

    @Override
    public ProductionOrder create(String productId, BigDecimal quantity, LocalDate dueDate) {
        ProductionOrderEntity saved = repository.save(new ProductionOrderEntity(
                UUID.randomUUID().toString(),
                productId,
                quantity,
                dueDate,
                ProductionOrderStatus.PLANNED,
                Instant.now()
        ));
        return toModel(saved);
    }

    @Override
    public ProductionOrder updateStatus(String id, ProductionOrderStatus status) {
        ProductionOrderEntity entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Production order not found: " + id));
        entity.setStatus(status);
        return toModel(repository.save(entity));
    }

    private ProductionOrder toModel(ProductionOrderEntity entity) {
        return new ProductionOrder(
                entity.getId(),
                entity.getProductId(),
                entity.getQuantity(),
                entity.getDueDate(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
