package com.kombaos.catalog.product.service;

import com.kombaos.catalog.product.domain.Product;
import com.kombaos.catalog.product.repository.ProductStore;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductStore store;

    public ProductService(ProductStore store) {
        this.store = store;
    }

    public List<Product> list() {
        return store.list();
    }

    public Product getById(String id) {
        return store.getById(id).orElseThrow(() -> new java.util.NoSuchElementException("Product not found: " + id));
    }

    public Product create(String name, String description, long priceCents, String currency, boolean active) {
        return store.create(name, description, priceCents, currency, active);
    }

    public Product update(String id, String name, String description, long priceCents, String currency, boolean active) {
        return store.update(id, name, description, priceCents, currency, active);
    }

    public void delete(String id) {
        store.delete(id);
    }
}
