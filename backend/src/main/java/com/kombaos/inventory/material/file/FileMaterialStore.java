package com.kombaos.inventory.material.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kombaos.config.KombaosProperties;
import com.kombaos.inventory.material.Material;
import com.kombaos.inventory.material.MaterialStore;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "local")
public class FileMaterialStore implements MaterialStore {

    private static final TypeReference<List<Material>> MATERIALS_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final Path storageFile;
    private final ReentrantLock lock = new ReentrantLock();

    public FileMaterialStore(ObjectMapper objectMapper, KombaosProperties properties) {
        this.objectMapper = objectMapper;
        this.storageFile = Path.of(properties.getLocalStorageDir()).resolve("materials.json");
    }

    @Override
    public List<Material> list() {
        lock.lock();
        try {
            List<Material> items = readAll();
            items.sort(Comparator.comparing(Material::createdAt));
            return items;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<Material> getById(String id) {
        lock.lock();
        try {
            return readAll().stream().filter(m -> m.id().equals(id)).findFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Material create(String name, String unit) {
        lock.lock();
        try {
            List<Material> all = readAll();
            Material created = new Material(UUID.randomUUID().toString(), name, unit, Instant.now());
            all.add(created);
            writeAll(all);
            return created;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Material update(String id, String name, String unit) {
        lock.lock();
        try {
            List<Material> all = readAll();
            List<Material> updated = new ArrayList<>(all.size());
            Material result = null;
            for (Material m : all) {
                if (m.id().equals(id)) {
                    result = new Material(m.id(), name, unit, m.createdAt());
                    updated.add(result);
                } else {
                    updated.add(m);
                }
            }
            if (result == null) {
                throw new NoSuchElementException("Material not found: " + id);
            }
            writeAll(updated);
            return result;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(String id) {
        lock.lock();
        try {
            List<Material> all = readAll();
            List<Material> remaining = all.stream().filter(m -> !m.id().equals(id)).toList();
            if (remaining.size() == all.size()) {
                throw new NoSuchElementException("Material not found: " + id);
            }
            writeAll(remaining);
        } finally {
            lock.unlock();
        }
    }

    private List<Material> readAll() {
        try {
            if (!Files.exists(storageFile)) {
                return new ArrayList<>();
            }
            byte[] json = Files.readAllBytes(storageFile);
            if (json.length == 0) {
                return new ArrayList<>();
            }
            return new ArrayList<>(objectMapper.readValue(json, MATERIALS_TYPE));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read materials store", e);
        }
    }

    private void writeAll(List<Material> materials) {
        try {
            Files.createDirectories(storageFile.getParent());
            Path temp = storageFile.getParent().resolve(storageFile.getFileName() + ".tmp");
            byte[] json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(materials);
            Files.write(temp, json);
            Files.move(temp, storageFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write materials store", e);
        }
    }
}
