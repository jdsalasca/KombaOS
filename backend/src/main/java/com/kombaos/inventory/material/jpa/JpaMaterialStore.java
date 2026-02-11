package com.kombaos.inventory.material.jpa;

import com.kombaos.inventory.material.Material;
import com.kombaos.inventory.material.MaterialStore;
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
public class JpaMaterialStore implements MaterialStore {

    private final MaterialJpaRepository repository;

    public JpaMaterialStore(MaterialJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Material> list() {
        return repository.findAll().stream()
                .map(this::toModel)
                .sorted(Comparator.comparing(Material::createdAt))
                .toList();
    }

    @Override
    public Optional<Material> getById(String id) {
        return repository.findById(id).map(this::toModel);
    }

    @Override
    public Material create(String name, String unit) {
        MaterialEntity saved = repository.save(new MaterialEntity(UUID.randomUUID().toString(), name, unit, Instant.now()));
        return toModel(saved);
    }

    @Override
    public Material update(String id, String name, String unit) {
        MaterialEntity entity = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Material not found: " + id));
        entity.setName(name);
        entity.setUnit(unit);
        MaterialEntity saved = repository.save(entity);
        return toModel(saved);
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Material not found: " + id);
        }
        repository.deleteById(id);
    }

    private Material toModel(MaterialEntity entity) {
        return new Material(entity.getId(), entity.getName(), entity.getUnit(), entity.getCreatedAt());
    }
}
