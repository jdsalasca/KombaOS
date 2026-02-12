package com.kombaos.inventory.material.repository.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kombaos.config.KombaosProperties;
import com.kombaos.persistence.file.FileJsonListStore;
import com.kombaos.inventory.material.domain.Material;
import com.kombaos.inventory.material.repository.MaterialStore;
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
public class FileMaterialStore implements MaterialStore {

    private static final TypeReference<List<Material>> MATERIALS_TYPE = new TypeReference<>() {
    };

    private final FileJsonListStore<Material> store;

    public FileMaterialStore(ObjectMapper objectMapper, KombaosProperties properties) {
        this.store = new FileJsonListStore<>(
                objectMapper,
                Path.of(properties.getLocalStorageDir()).resolve("materials.json"),
                MATERIALS_TYPE
        );
    }

    @Override
    public List<Material> list() {
        return store.withLock(() -> {
            List<Material> items = store.readAll();
            items.sort(Comparator.comparing(Material::createdAt));
            return items;
        });
    }

    @Override
    public Optional<Material> getById(String id) {
        return store.withLock(() -> store.readAll().stream().filter(m -> m.id().equals(id)).findFirst());
    }

    @Override
    public Material create(String name, String unit, String supplier, String origin, boolean certified, Long costCents, String currency) {
        return store.withLock(() -> {
            List<Material> all = store.readAll();
            Material created = new Material(UUID.randomUUID().toString(), name, unit, supplier, origin, certified, costCents, currency, Instant.now());
            all.add(created);
            store.writeAll(all);
            return created;
        });
    }

    @Override
    public Material update(String id, String name, String unit, String supplier, String origin, boolean certified, Long costCents, String currency) {
        return store.withLock(() -> {
            List<Material> all = store.readAll();
            List<Material> updated = new ArrayList<>(all.size());
            Material result = null;
            for (Material m : all) {
                if (m.id().equals(id)) {
                    result = new Material(m.id(), name, unit, supplier, origin, certified, costCents, currency, m.createdAt());
                    updated.add(result);
                } else {
                    updated.add(m);
                }
            }
            if (result == null) {
                throw new NoSuchElementException("Material not found: " + id);
            }
            store.writeAll(updated);
            return result;
        });
    }

    @Override
    public void delete(String id) {
        store.withLock(() -> {
            List<Material> all = store.readAll();
            List<Material> remaining = all.stream().filter(m -> !m.id().equals(id)).toList();
            if (remaining.size() == all.size()) {
                throw new NoSuchElementException("Material not found: " + id);
            }
            store.writeAll(remaining);
        });
    }
}
