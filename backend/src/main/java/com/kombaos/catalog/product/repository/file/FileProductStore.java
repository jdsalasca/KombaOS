package com.kombaos.catalog.product.repository.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kombaos.catalog.product.domain.Product;
import com.kombaos.catalog.product.repository.ProductStore;
import com.kombaos.config.KombaosProperties;
import com.kombaos.persistence.file.FileJsonListStore;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "local")
public class FileProductStore implements ProductStore {

    private static final TypeReference<List<Product>> PRODUCTS_TYPE = new TypeReference<>() {
    };

    private final FileJsonListStore<Product> store;

    public FileProductStore(ObjectMapper objectMapper, KombaosProperties properties) {
        this.store = new FileJsonListStore<>(
                objectMapper,
                Path.of(properties.getLocalStorageDir()).resolve("products.json"),
                PRODUCTS_TYPE
        );
    }

    @Override
    public List<Product> list() {
        return store.withLock(() -> {
            List<Product> items = store.readAll();
            items.sort(Comparator.comparing(Product::createdAt));
            return items;
        });
    }

    @Override
    public Optional<Product> getById(String id) {
        return store.withLock(() -> store.readAll().stream().filter(p -> p.id().equals(id)).findFirst());
    }

    @Override
    public Product create(String name, String description, long priceCents, String currency, boolean active) {
        return store.withLock(() -> {
            List<Product> all = store.readAll();
            Product created = new Product(UUID.randomUUID().toString(), name, description, priceCents, currency, active, Instant.now());
            all.add(created);
            store.writeAll(all);
            return created;
        });
    }

    @Override
    public Product update(String id, String name, String description, long priceCents, String currency, boolean active) {
        return store.withLock(() -> {
            List<Product> all = store.readAll();
            List<Product> updated = new ArrayList<>(all.size());
            Product result = null;
            for (Product p : all) {
                if (p.id().equals(id)) {
                    result = new Product(p.id(), name, description, priceCents, currency, active, p.createdAt());
                    updated.add(result);
                } else {
                    updated.add(p);
                }
            }
            if (result == null) {
                throw new NoSuchElementException("Product not found: " + id);
            }
            store.writeAll(updated);
            return result;
        });
    }

    @Override
    public void delete(String id) {
        store.withLock(() -> {
            List<Product> all = store.readAll();
            List<Product> remaining = all.stream().filter(p -> !p.id().equals(id)).toList();
            if (remaining.size() == all.size()) {
                throw new NoSuchElementException("Product not found: " + id);
            }
            store.writeAll(remaining);
        });
    }
}
