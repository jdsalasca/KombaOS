package com.kombaos.catalog.product.repository.jpa;

import com.kombaos.catalog.product.domain.Product;
import com.kombaos.catalog.product.repository.ProductStore;
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
public class JpaProductStore implements ProductStore {

    private final ProductJpaRepository repository;

    public JpaProductStore(ProductJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Product> list() {
        return repository.findAll().stream()
                .map(this::toModel)
                .sorted(Comparator.comparing(Product::createdAt))
                .toList();
    }

    @Override
    public Optional<Product> getById(String id) {
        return repository.findById(id).map(this::toModel);
    }

    @Override
    public Product create(String name, String description, long priceCents, String currency, boolean active) {
        ProductEntity saved = repository.save(new ProductEntity(
                UUID.randomUUID().toString(),
                name,
                description,
                priceCents,
                currency,
                active,
                Instant.now()
        ));
        return toModel(saved);
    }

    @Override
    public Product update(String id, String name, String description, long priceCents, String currency, boolean active) {
        ProductEntity entity = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Product not found: " + id));
        entity.setName(name);
        entity.setDescription(description);
        entity.setPriceCents(priceCents);
        entity.setCurrency(currency);
        entity.setActive(active);
        ProductEntity saved = repository.save(entity);
        return toModel(saved);
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Product not found: " + id);
        }
        repository.deleteById(id);
    }

    private Product toModel(ProductEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPriceCents(),
                entity.getCurrency(),
                entity.isActive(),
                entity.getCreatedAt()
        );
    }
}
