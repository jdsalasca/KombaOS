package com.kombaos.inventory.material;

import java.util.List;
import java.util.NoSuchElementException;

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

    public Material getById(String id) {
        return store.getById(id).orElseThrow(() -> new NoSuchElementException("Material not found: " + id));
    }

    public Material create(String name, String unit) {
        return store.create(name, unit);
    }

    public Material update(String id, String name, String unit) {
        return store.update(id, name, unit);
    }

    public void delete(String id) {
        store.delete(id);
    }
}
