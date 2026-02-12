package com.kombaos.catalog.product.repository;

import com.kombaos.catalog.product.domain.Product;
import java.util.List;
import java.util.Optional;

public interface ProductStore {
    List<Product> list();

    Optional<Product> getById(String id);

    Product create(String name, String description, long priceCents, String currency, boolean active);

    Product update(String id, String name, String description, long priceCents, String currency, boolean active);

    void delete(String id);
}
