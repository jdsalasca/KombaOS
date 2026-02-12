package com.kombaos.inventory.material.service;

import com.kombaos.inventory.material.domain.Material;
import com.kombaos.inventory.material.repository.MaterialStore;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class MaterialService {

    private final MaterialStore store;

    public MaterialService(MaterialStore store) {
        this.store = store;
    }

    public List<Material> list() {
        return store.list();
    }

    public List<Material> list(String q, String supplier, String origin, Boolean certified) {
        String query = normalize(q);
        String supplierQuery = normalize(supplier);
        String originQuery = normalize(origin);
        Optional<Boolean> certifiedFilter = Optional.ofNullable(certified);

        return store.list().stream()
                .filter(m -> query == null || containsIgnoreCase(m.name(), query))
                .filter(m -> supplierQuery == null || equalsIgnoreCase(m.supplier(), supplierQuery))
                .filter(m -> originQuery == null || equalsIgnoreCase(m.origin(), originQuery))
                .filter(m -> certifiedFilter.map(v -> m.certified() == v).orElse(true))
                .toList();
    }

    public Material getById(String id) {
        return store.getById(id).orElseThrow(() -> new NoSuchElementException("Material not found: " + id));
    }

    public Material create(String name, String unit, String supplier, String origin, boolean certified, Long costCents, String currency) {
        return store.create(name, unit, supplier, origin, certified, costCents, currency);
    }

    public Material update(String id, String name, String unit, String supplier, String origin, boolean certified, Long costCents, String currency) {
        return store.update(id, name, unit, supplier, origin, certified, costCents, currency);
    }

    public void delete(String id) {
        store.delete(id);
    }

    private static String normalize(String value) {
        if (value == null) return null;
        String v = value.trim();
        return v.isEmpty() ? null : v;
    }

    private static boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query.toLowerCase());
    }

    private static boolean equalsIgnoreCase(String value, String query) {
        return Objects.equals(value == null ? null : value.toLowerCase(), query.toLowerCase());
    }
}
