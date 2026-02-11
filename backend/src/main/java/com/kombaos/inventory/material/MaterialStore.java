package com.kombaos.inventory.material;

import java.util.List;
import java.util.Optional;

public interface MaterialStore {
    List<Material> list();

    Optional<Material> getById(String id);

    Material create(String name, String unit);

    Material update(String id, String name, String unit);

    void delete(String id);
}
