package com.kombaos.sales.order.repository.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kombaos.config.KombaosProperties;
import com.kombaos.persistence.file.FileJsonListStore;
import com.kombaos.sales.order.domain.SalesOrder;
import com.kombaos.sales.order.domain.SalesOrderStatus;
import com.kombaos.sales.order.repository.SalesOrderStore;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "local")
public class FileSalesOrderStore implements SalesOrderStore {

    private static final TypeReference<List<SalesOrder>> ORDERS_TYPE = new TypeReference<>() {
    };

    private final FileJsonListStore<SalesOrder> store;

    public FileSalesOrderStore(ObjectMapper objectMapper, KombaosProperties properties) {
        this.store = new FileJsonListStore<>(
                objectMapper,
                Path.of(properties.getLocalStorageDir()).resolve("sales_orders.json"),
                ORDERS_TYPE
        );
    }

    @Override
    public List<SalesOrder> list() {
        return store.withLock(() -> store.readAll().stream()
                .sorted(Comparator.comparing(SalesOrder::createdAt))
                .toList());
    }

    @Override
    public Optional<SalesOrder> getById(String id) {
        return store.withLock(() -> store.readAll().stream().filter(o -> o.id().equals(id)).findFirst());
    }

    @Override
    public SalesOrder create(String customerName, String customerEmail, String productId, BigDecimal quantity) {
        return store.withLock(() -> {
            List<SalesOrder> all = store.readAll();
            SalesOrder created = new SalesOrder(
                    UUID.randomUUID().toString(),
                    customerName,
                    customerEmail,
                    productId,
                    quantity,
                    SalesOrderStatus.PENDING,
                    Instant.now()
            );
            all.add(created);
            store.writeAll(all);
            return created;
        });
    }

    @Override
    public SalesOrder updateStatus(String id, SalesOrderStatus status) {
        return store.withLock(() -> {
            List<SalesOrder> all = store.readAll();
            SalesOrder result = null;
            List<SalesOrder> updated = all.stream().map(order -> {
                if (order.id().equals(id)) {
                    return new SalesOrder(
                            order.id(),
                            order.customerName(),
                            order.customerEmail(),
                            order.productId(),
                            order.quantity(),
                            status,
                            order.createdAt()
                    );
                }
                return order;
            }).toList();

            for (SalesOrder order : updated) {
                if (order.id().equals(id)) {
                    result = order;
                    break;
                }
            }

            if (result == null) {
                throw new NoSuchElementException("Sales order not found: " + id);
            }

            store.writeAll(updated);
            return result;
        });
    }
}
