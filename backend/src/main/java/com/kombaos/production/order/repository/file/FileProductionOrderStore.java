package com.kombaos.production.order.repository.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kombaos.config.KombaosProperties;
import com.kombaos.persistence.file.FileJsonListStore;
import com.kombaos.production.order.domain.ProductionOrder;
import com.kombaos.production.order.domain.ProductionOrderStatus;
import com.kombaos.production.order.repository.ProductionOrderStore;
import java.math.BigDecimal;
import java.nio.file.Path;
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
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "local")
public class FileProductionOrderStore implements ProductionOrderStore {

    private static final TypeReference<List<ProductionOrder>> ORDERS_TYPE = new TypeReference<>() {
    };

    private final FileJsonListStore<ProductionOrder> store;

    public FileProductionOrderStore(ObjectMapper objectMapper, KombaosProperties properties) {
        this.store = new FileJsonListStore<>(
                objectMapper,
                Path.of(properties.getLocalStorageDir()).resolve("production_orders.json"),
                ORDERS_TYPE
        );
    }

    @Override
    public List<ProductionOrder> list() {
        return store.withLock(() -> store.readAll().stream()
                .sorted(Comparator.comparing(ProductionOrder::createdAt))
                .toList());
    }

    @Override
    public Optional<ProductionOrder> getById(String id) {
        return store.withLock(() -> store.readAll().stream().filter(o -> o.id().equals(id)).findFirst());
    }

    @Override
    public ProductionOrder create(String productId, BigDecimal quantity, LocalDate dueDate) {
        return store.withLock(() -> {
            List<ProductionOrder> all = store.readAll();
            ProductionOrder created = new ProductionOrder(
                    UUID.randomUUID().toString(),
                    productId,
                    quantity,
                    dueDate,
                    ProductionOrderStatus.PLANNED,
                    Instant.now()
            );
            all.add(created);
            store.writeAll(all);
            return created;
        });
    }

    @Override
    public ProductionOrder updateStatus(String id, ProductionOrderStatus status) {
        return store.withLock(() -> {
            List<ProductionOrder> all = store.readAll();
            ProductionOrder result = null;
            List<ProductionOrder> updated = all.stream().map(order -> {
                if (order.id().equals(id)) {
                    return new ProductionOrder(
                            order.id(),
                            order.productId(),
                            order.quantity(),
                            order.dueDate(),
                            status,
                            order.createdAt()
                    );
                }
                return order;
            }).toList();

            for (ProductionOrder order : updated) {
                if (order.id().equals(id)) {
                    result = order;
                    break;
                }
            }

            if (result == null) {
                throw new NoSuchElementException("Production order not found: " + id);
            }

            store.writeAll(updated);
            return result;
        });
    }
}
